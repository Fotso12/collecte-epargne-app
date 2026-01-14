@echo off
cls
echo ================================================
echo   REDEMARRAGE DU BACKEND
echo   Arret puis relance avec CORS active
echo ================================================
echo.

cd /d "%~dp0"

echo Arret des processus Java en cours...
taskkill /F /IM java.exe 2>nul

echo.
echo Attente de 3 secondes...
timeout /t 3 /nobreak >nul

echo.
echo Redemarrage du backend...
echo.

call mvnw.cmd spring-boot:run -DskipTests

pause

