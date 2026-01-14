@echo off
echo ========================================
echo Lancement Backend avec logs detailles
echo ========================================
echo.

cd /d "%~dp0"
call mvnw.cmd spring-boot:run -DskipTests -Dspring-boot.run.profiles=dev -e -X > debug-logs.txt 2>&1

echo.
echo Logs sauvegardes dans debug-logs.txt
echo.
type debug-logs.txt | findstr /i "error exception caused"
echo.
pause

