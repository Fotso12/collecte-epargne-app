@echo off
cd /d "%~dp0"
call mvnw.cmd clean compile -DskipTests
pause
