@echo off
chcp 65001 > nul
echo 停止SSH隧道...

taskkill /F /IM ssh.exe 2>nul
if exist tunnel.pid del tunnel.pid

echo SSH隧道已停止
pause
