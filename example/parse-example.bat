@echo off
set PARSER=parser.exe
set INSTANCE_FILE=parse-example.txt

REM accept
%PARSER% -visitor XmlDisplayer -file %INSTANCE_FILE%
%PARSER% -visitor XmlDisplayer -string "����ʱ��15:32:59"

REM reject
%PARSER% -visitor XmlDisplayer -string "ʱ��13:12:15"

pause
