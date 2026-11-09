@echo off
echo ========================================
echo Lancement du Backend Spring Boot
echo Port: 8082
echo Base de donnees: H2 (en memoire)
echo ========================================
echo.
echo Demarrage en cours...
echo.

cd /d "%~dp0"
call mvnw.cmd spring-boot:run -DskipTests -Dspring-boot.run.profiles=dev

pause

