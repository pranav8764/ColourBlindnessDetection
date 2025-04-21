@echo off
echo Copying Ishihara test images to assets directory...

set APP_DIR=%~dp0app
set ASSESTS_DIR=%APP_DIR%\src\assests\images
set ASSETS_DIR=%APP_DIR%\src\main\assets

REM Create the assets directory if it doesn't exist
mkdir "%ASSETS_DIR%\images" 2>nul

REM Copy all files from assests/images to assets/images
echo Copying from %ASSESTS_DIR% to %ASSETS_DIR%\images
copy "%ASSESTS_DIR%\*.*" "%ASSETS_DIR%\images\" /Y

echo Assets copied successfully!
echo.
echo Asset source directory: %ASSESTS_DIR%
echo Asset destination directory: %ASSETS_DIR%\images
echo.
echo Available images:
dir /b "%ASSETS_DIR%\images\"

pause
