@echo off
chcp 65001 >nul 2>&1
title 三国杀TCG - 抽卡模拟器

echo.
echo  ============================================
echo     三国杀TCG - 抽卡模拟器 启动中...
echo  ============================================
echo.

REM 检查 Java 是否已安装
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo  [错误] 未检测到 Java 运行环境！
    echo.
    echo  请先安装 Java（JDK 8 或以上版本）。
    echo  推荐下载地址：https://adoptium.net/
    echo.
    echo  安装完成后，请关闭本窗口，重新双击本脚本即可。
    echo.
    pause
    exit /b 1
)

echo  [√] 已检测到 Java 环境：
java -version 2>&1 | findstr /i "version"
echo.

REM 获取脚本所在目录
set "SCRIPT_DIR=%~dp0"
set "JAR_FILE=%SCRIPT_DIR%sgs-ctzy-demo.jar"

REM 检查 JAR 文件是否存在
if not exist "%JAR_FILE%" (
    echo  [错误] 未找到 sgs-ctzy-demo.jar 文件！
    echo  请确保本脚本和 sgs-ctzy-demo.jar 放在同一个文件夹中。
    echo.
    pause
    exit /b 1
)

echo  [√] 正在启动服务，请稍候...
echo.
echo  启动成功后，请在浏览器中访问：
echo.
echo       http://localhost:8080
echo.
echo  ============================================
echo  提示：关闭本窗口即可停止服务
echo  ============================================
echo.

REM 启动 JAR
java -jar "%JAR_FILE%"

echo.
echo  服务已停止。
pause
