@echo off
set PARSER=parser.exe
set INSTANCE_FILE=parse-example.txt

REM accept
%PARSER% -visitor XmlDisplayer -file %INSTANCE_FILE%
%PARSER% -visitor XmlDisplayer -string "中文时间15:32:59"

REM reject
%PARSER% -visitor XmlDisplayer -string "时间13:12:15"

pause
