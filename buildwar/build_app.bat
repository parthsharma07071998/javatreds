echo off
set PATH=C:\Program Files\Java\jdk1.8.0_251\bin;%PATH%
set APPPATH=D:\RXIL\Projects\Oracle\DEVCS\DEV\GIT\treds
set BUILDPATH=%APPPATH%\buildwar
set SRCWEBAPP=%APPPATH%\webapp


set APPCLASSES=%APPPATH%\classes\
set DEP_LIBS=D:\RXIL\Projects\Oracle\DEVCS\DEV\GIT\treds\lib

cd %BUILDPATH%
del ROOT.war

rd /S /Q webapp
echo ################### DIRECTORY CLEARED ###################
pause

mkdir .\webapp\assets
mkdir .\webapp\css
mkdir .\webapp\fonts
mkdir .\webapp\images
mkdir .\webapp\js
mkdir .\webapp\jsp
rem mkdir .\webapp\META-INF
mkdir .\webapp\static
mkdir .\webapp\WEB-INF
mkdir .\webapp\WEB-INF\lib

rem xcopy /Y /E %SRCWEBAPP% .\webapp\

xcopy /Y /E %SRCWEBAPP%\assets .\webapp\assets\
xcopy /Y /E %SRCWEBAPP%\css .\webapp\css\
xcopy /Y /E %SRCWEBAPP%\fonts .\webapp\fonts\
xcopy /Y /E %SRCWEBAPP%\images .\webapp\images\
xcopy /Y /E %SRCWEBAPP%\js .\webapp\js\
xcopy /Y /E %SRCWEBAPP%\jsp .\webapp\jsp\
rem xcopy /Y /E %SRCWEBAPP%\META-INF .\webapp\META-INF\
xcopy /Y /E %SRCWEBAPP%\static .\webapp\static\
xcopy /Y /E %SRCWEBAPP%\WEB-INF .\webapp\WEB-INF\

copy %SRCWEBAPP%\*.jsp .\webapp\ /Y
echo ################### FILES COPIED ###################
pause

jar cvf .\webapp\WEB-INF\lib\treds.jar -C %APPCLASSES% com -C %APPCLASSES% config.xml
copy %DEP_LIBS%\common.jar .\webapp\WEB-INF\lib\ /Y
copy %DEP_LIBS%\commonn.jar .\webapp\WEB-INF\lib\ /Y
copy %DEP_LIBS%\sshd.jar .\webapp\WEB-INF\lib\ /Y

cd webapp
jar cvf ..\ROOT.war  *
cd ..

rd /S /Q webapp
echo ################### WAR & ZIP FILE CREATED ###################
pause
