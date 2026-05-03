//STATE
let currentWalletId = null;
let bankStocks = [];
let logEntries = [];
let pendingStocks = [];

//TOAST
function toast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    const el = document.createElement('div');
    el.className = `toast ${type}`;
    const icon = type === 'success' ? '✓' : type === 'error' ? '✕' : 'ℹ';
    el.innerHTML = `<span>${icon}</span><span>${message}</span>`;
    container.appendChild(el);
    setTimeout(() => el.remove(), 3100);
}

//API
async function api(path, method = 'GET', body = null) {
    const opts = {method, headers: {'Content-Type': 'application/json'}};
    if (body) opts.body = JSON.stringify(body);
    const res = await fetch(path, opts);
    if (!res.ok) {
        let msg = `HTTP ${res.status}`;
        try {
            const err = await res.json();
            msg = err.detail || err.message || msg;
        } catch { /* ignore */
        }
        throw new Error(msg);
    }
    const ct = res.headers.get('content-type') || '';
    if (ct.includes('application/json')) return res.json();
    const text = await res.text();
    return text === '' ? null : text;
}

//BANK
async function refreshBank() {
    spinRefresh('refresh-icon-bank');
    try {
        const data = await api('/stocks');
        bankStocks = data.stocks || [];
        renderBank();
        renderTickerBar();
        renderStats();
    } catch (e) {
        toast('Error during loading bank state: ' + e.message, 'error');
    }
}

function renderBank() {
    const el = document.getElementById('bank-stocks');
    if (bankStocks.length === 0) {
        el.innerHTML = `
      <div class="empty-state">
        <svg width="32" height="32" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24">
          <path d="M3 12l9-9 9 9M5 10v9a1 1 0 001 1h4v-5h4v5h4a1 1 0 001-1v-9"/>
        </svg>
        NO STOCKS IN BANK
      </div>`;
        return;
    }
    el.innerHTML = bankStocks.map(s => {
        const qtyClass = s.quantity === 0 ? 'zero' : s.quantity < 10 ? 'low' : '';
        return `
      <div class="stock-row">
        <div class="stock-row-left">
          <span class="stock-name">${s.name}</span>
          <span class="stock-qty">Available: <span class="qty-val ${qtyClass}">${s.quantity}</span></span>
        </div>
        <div class="stock-row-actions">
          <button class="btn btn-buy" onclick="trade('${s.name}', 'buy')"
            ${s.quantity === 0 ? 'disabled style="opacity:0.4;cursor:not-allowed"' : ''}>
            BUY
          </button>
        </div>
      </div>`;
    }).join('');
}

function renderTickerBar() {
    const el = document.getElementById('ticker-bar');
    if (bankStocks.length === 0) {
        el.innerHTML = '<span class="ticker-item" style="color:var(--text-dim)">— NO AUDIT —</span>';
        return;
    }
    el.innerHTML = bankStocks.map(s => `
    <span class="ticker-item">
      <span class="name">${s.name}</span>
      <span class="qty">${s.quantity}</span>
    </span>
  `).join('');
}

//ADDING STOCK TO BANK
function renderPendingStocks() {
    const el = document.getElementById('pending-stocks');
    if (pendingStocks.length === 0) {
        el.innerHTML = '<span class="pending-empty">List is empty - add stocks above</span>';
        return;
    }
    el.innerHTML = pendingStocks.map((s, i) => `
    <div class="pending-row">
      <span class="pending-name">${s.name}</span>
      <div style="display:flex;align-items:center;gap:6px">
        <button class="btn btn-ghost" style="padding:2px 10px;font-size:0.8rem;line-height:1" onclick="changePendingQty(${i}, -10)">−−</button>
        <button class="btn btn-ghost" style="padding:2px 8px;font-size:0.8rem;line-height:1" onclick="changePendingQty(${i}, -1)">−</button>
        <span class="pending-qty">${s.quantity}</span>
        <button class="btn btn-ghost" style="padding:2px 8px;font-size:0.8rem;line-height:1" onclick="changePendingQty(${i}, 1)">+</button>
        <button class="btn btn-ghost" style="padding:2px 10px;font-size:0.8rem;line-height:1" onclick="changePendingQty(${i}, 10)">++</button>
        <button class="btn btn-sell" style="padding:2px 8px;font-size:0.65rem" onclick="removePending(${i})">✕</button>
      </div>
    </div>
  `).join('');
}

function addPendingStock() {
    const nameEl = document.getElementById('new-stock-name');
    const qtyEl = document.getElementById('new-stock-qty');
    const name = nameEl.value.trim().toUpperCase();
    const qty = parseInt(qtyEl.value, 10);

    if (!name) {
        toast('Provide the stock name', 'error');
        return;
    }
    if (!qty || qty < 1) {
        toast('Quantity must be higher than 0', 'error');
        return;
    }

    const existing = pendingStocks.findIndex(s => s.name === name);
    if (existing >= 0) {
        pendingStocks[existing].quantity += qty;
        toast(`Updated ${name}`, 'info');
    } else {
        pendingStocks.push({name, quantity: qty});
        toast(`Added ${name}`, 'success');
    }

    nameEl.value = '';
    qtyEl.value = '100';
    nameEl.focus();
    renderPendingStocks();
}

function changePendingQty(index, delta) {
    pendingStocks[index].quantity = Math.max(1, pendingStocks[index].quantity + delta);
    renderPendingStocks();
}

function removePending(index) {
    const name = pendingStocks[index].name;
    pendingStocks.splice(index, 1);
    toast(`Deleted ${name}`, 'info');
    renderPendingStocks();
}

async function sendBankStocks() {
    if (pendingStocks.length === 0) {
        toast('Add at least one stock', 'error');
        return;
    }
    try {
        await api('/stocks', 'POST', {stocks: pendingStocks});
        toast(`Set ${pendingStocks.length} stock types in bank`, 'success');
        pendingStocks = [];
        renderPendingStocks();
        await refreshBank();
    } catch (e) {
        toast('Error: ' + e.message, 'error');
    }
}

//WALLET
async function loadWallet() {
    const id = document.getElementById('wallet-id').value.trim();
    if (!id) {
        toast('Provide wallet ID', 'error');
        return;
    }
    currentWalletId = id;

    try {
        const data = await api(`/wallets/${currentWalletId}`);
        renderWallet(data.stocks || [], false);
        const header = document.getElementById('wallet-header-id');
        if (header) header.textContent = currentWalletId;
    } catch {
        renderWallet([], true);
        const header = document.getElementById('wallet-header-id');
        if (header) header.textContent = currentWalletId;
        toast(`Wallet "${currentWalletId}" will be created after the purchase`, 'info');
    }
}

async function refreshWallet() {
    if (!currentWalletId) return;
    try {
        const data = await api(`/wallets/${currentWalletId}`);
        renderWallet(data.stocks || [], false);
    } catch {
        renderWallet([], true);
    }
}

function renderWallet(stocks, isNew = false) {
    const el = document.getElementById('wallet-stocks');

    if (stocks.length === 0) {
        el.innerHTML = `
      <div class="empty-state">
        <svg width="32" height="32" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24">
          <path d="M21 12V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2h7"/>
          <path d="M16 19l2 2 4-4"/>
        </svg>
        ${isNew ? 'NEW WALLET<br><span style="font-size:0.6rem;opacity:0.6">will be created after the purchase</span>' : 'WALLET IS EMPTY'}
      </div>`;
        return;
    }

    el.innerHTML = stocks.map(s => `
    <div class="wallet-row">
      <div class="stock-row-left">
        <span class="stock-name">${s.name}</span>
        <span class="stock-qty">Owned: <span class="qty-val">${s.quantity}</span></span>
      </div>
      <div style="display:flex;align-items:center;gap:8px">
        <span class="wallet-badge">×${s.quantity}</span>
        <button class="btn btn-sell" onclick="trade('${s.name}', 'sell')">SELL</button>
      </div>
    </div>
  `).join('');
}

//TRADE
async function trade(stockName, type) {
    if (!currentWalletId) {
        toast('Load the wallet first', 'error');
        return;
    }
    try {
        await api(`/wallets/${currentWalletId}/stocks/${stockName}`, 'POST', {type});
        const label = type === 'buy' ? 'Bought' : 'Sold';
        toast(`${label}: ${stockName}`, 'success');
        await Promise.all([refreshBank(), refreshWallet(), refreshLog()]);
    } catch (e) {
        toast(e.message, 'error');
    }
}

//LOG
async function refreshLog() {
    spinRefresh('refresh-icon-log');
    try {
        const data = await api('/log');
        logEntries = data.log || [];
        renderLog();
        renderStats();
    } catch (e) {
        toast('Error during loading logs: ' + e.message, 'error');
    }
}

function renderLog() {
    const el = document.getElementById('audit-log');
    if (logEntries.length === 0) {
        el.innerHTML = `
      <div class="empty-state">
        <svg width="32" height="32" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24">
          <path d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
        </svg>
        NO OPERATIONS
      </div>`;
        return;
    }
    el.innerHTML = [...logEntries].reverse().map(l => `
    <div class="log-entry ${l.type}">
      <span class="log-badge">${l.type.toUpperCase()}</span>
      <div class="log-details">
        <span class="log-stock">${l.stock_name}</span>
        <span class="log-wallet">${l.wallet_id}</span>
      </div>
    </div>
  `).join('');
}

//STATS
function renderStats() {
    const totalQty = bankStocks.reduce((a, s) => a + s.quantity, 0);
    document.getElementById('stat-total-qty').textContent = totalQty;
    document.getElementById('stat-types').textContent = bankStocks.length;
    document.getElementById('stat-log-count').textContent = logEntries.length;
}

//CHAOS
async function triggerChaos() {
    const btn = document.getElementById('chaos-btn');
    btn.classList.add('chaos-active');
    try {
        await api('/chaos', 'POST');
        toast('Instant killed — Nginx will transfer traffic', 'info');
    } catch {
        toast('Instant killed  (connection reset)', 'info');
    }
    btn.classList.remove('chaos-active');
}

//SPIN ICON
function spinRefresh(id) {
    const el = document.getElementById(id);
    if (!el) return;
    el.classList.add('spin');
    setTimeout(() => el.classList.remove('spin'), 500);
}

//INIT
document.addEventListener('DOMContentLoaded', () => {
    lucide.createIcons();
    refreshBank();
    refreshLog();
    renderPendingStocks();

    document.getElementById('new-stock-name')
        .addEventListener('keydown', e => {
            if (e.key === 'Enter') addPendingStock();
        });

    document.getElementById('wallet-id')
        .addEventListener('keydown', e => {
            if (e.key === 'Enter') loadWallet();
        });
});