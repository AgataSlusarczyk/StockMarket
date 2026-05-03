#!/bin/bash
if [ -z "$1" ]; then
  echo "Uzycie: ./start.sh <PORT>"
  exit 1
fi

export APP_PORT=$1

echo "Uruchamianie srodowiska (Docker)..."
docker-compose up --build -d

echo "Gotowe! Symulator gieldy dziala pod adresem: http://localhost:$APP_PORT"