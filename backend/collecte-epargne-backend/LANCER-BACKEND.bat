@echo off
cls
echo ================================================
echo   LANCEMENT BACKEND - Port 8082
echo   Base de donnees: H2 (en memoire)
echo ================================================
echo.
echo Nettoyage et demarrage en cours...
echo Cela peut prendre 30-60 secondes.
echo.
echo Attendez le message: "Started CollecteEpargneApplication"
echo.

cd /d "%~dp0"
call mvnw.cmd clean spring-boot:run -DskipTests

pause

