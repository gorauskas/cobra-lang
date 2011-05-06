@cls
@echo.
@echo Compiling installation program...
@Snapshot\cobra.exe -debug -ert:yes InstallFromWorkspace.cobra -- %*
