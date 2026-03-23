@echo off
chcp 65001 > nul
echo 正在后台启动SSH隧道...
echo 进程ID将保存到 tunnel.pid

start /B "" "C:\Windows\System32\OpenSSH\ssh.exe" -N -L 13306:127.0.0.1:3306 root@121.36.231.106
echo %! > tunnel.pid
echo SSH隧道已在后台启动
