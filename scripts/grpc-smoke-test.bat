@echo off
setlocal
pushd "%~dp0.."

set "BASH_EXE=%ProgramFiles%\Git\bin\bash.exe"
if not exist "%BASH_EXE%" set "BASH_EXE=bash"

"%BASH_EXE%" ./scripts/grpc-smoke-test.sh
set "EXIT_CODE=%ERRORLEVEL%"

echo.
echo Script exited with code %EXIT_CODE%.
pause
exit /b %EXIT_CODE%
