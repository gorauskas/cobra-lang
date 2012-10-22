@cls
@cd %~dp0
@cd ..
@echo.
@echo Compiling installation program...
@Snapshot\cobra.exe -debug -ert:yes InstallFromWorkspace.cobra SubversionUtils.cobra -- %*
@cd ..\..
