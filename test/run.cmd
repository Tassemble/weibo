@echo off

setlocal EnableDelayedExpansion

for %%F in (lib/*.jar) do set CP=!CP!;lib\%%F

set CP=!CP!;conf

echo %CP%

java -cp %CP%  com.techq.weibo.api.imp.WeiboRobot
endlocal

