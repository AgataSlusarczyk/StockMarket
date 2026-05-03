@echo off
if "%~1"=="" (
    echo Uzycie: start.bat ^<PORT^>
    exit /b 1
)

set APP_PORT=%1

echo Uruchamianie srodowiska (Docker)...
docker-compose up --build -d

echo Gotowe! Symulator gieldy dziala pod adresem: http://localhost:%APP_PORT%