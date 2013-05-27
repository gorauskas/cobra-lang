@echo off
cls

echo Cobra Testify
echo.
date /t
time /t
@rem dir cobra.exe Cobra.Core.dll
dir cobra.exe | findstr cobra.exe
dir Cobra.Core.dll | findstr Cobra.Core.dll
echo.

type NUL > r-testify-failures.text
cobra -testify %*
@rem e r-testify

echo.
echo Failures:
echo.
type r-testify-failures.text
