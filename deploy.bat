@echo off

if exist target rd /s /q "target"

mvn -Dmaven.test.skip=true clean install assembly:assembly -U

pause >nul