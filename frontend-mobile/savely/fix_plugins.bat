@echo off
echo ========================================
echo Fixing image_picker plugin configuration
echo ========================================
echo.

echo Step 1: Cleaning Flutter project...
call flutter clean
if %errorlevel% neq 0 (
    echo ERROR: flutter clean failed
    pause
    exit /b 1
)

echo.
echo Step 2: Getting dependencies...
call flutter pub get
if %errorlevel% neq 0 (
    echo ERROR: flutter pub get failed
    pause
    exit /b 1
)

echo.
echo Step 3: Cleaning Android build...
cd android
call gradlew.bat clean
if %errorlevel% neq 0 (
    echo WARNING: Android clean failed, but continuing...
)
cd ..

echo.
echo ========================================
echo Fix completed!
echo.
echo IMPORTANT: You must now:
echo 1. Stop the application completely
echo 2. Run: flutter run
echo    (Do NOT use hot reload, do a full rebuild)
echo ========================================
pause

