@echo off
setlocal enabledelayedexpansion

cd /d "%~dp0"
title HaUI Course App

echo ============================================================
echo           HaUI Course App - He Thong Khoa Hoc Online
echo ============================================================
echo.

set "SQL_AUTH=-S localhost -U sa -P 123456"
sqlcmd !SQL_AUTH! -Q "SELECT 1;" -b >nul 2>&1
if !ERRORLEVEL! NEQ 0 (
    set "SQL_AUTH=-S localhost -E"
)

where sqlcmd >nul 2>&1
if !ERRORLEVEL! EQU 0 (
    echo [1/3] Kiem tra co so du lieu HaUICourseDB...
    sqlcmd !SQL_AUTH! -Q "IF DB_ID('HaUICourseDB') IS NULL THROW 50000, 'DB Not Found', 1;" -b >nul 2>&1
    if !ERRORLEVEL! EQU 0 (
        echo [CSDL] Co so du lieu HaUICourseDB da ton tai. Bo qua khoi tao.
    ) else (
        echo [CSDL] Chua tim thay co so du lieu HaUICourseDB. Dang khoi tao...
        sqlcmd !SQL_AUTH! -i "sql\schema.sql" -b
        if !ERRORLEVEL! EQU 0 (
            echo [CSDL] Khoi tao co so du lieu HaUICourseDB thanh cong.
        ) else (
            echo [CSDL] Khong the khoi tao CSDL. Ung dung se khoi dong voi che do du lieu mau.
        )
    )
) else (
    echo [CSDL] Khong tim thay lenh sqlcmd trong he thong. Bo qua kiem tra CSDL.
)

echo.
echo [2/3] Kiem tra ma nguon Java...

where java >nul 2>&1
if !ERRORLEVEL! NEQ 0 (
    echo [LOI] Khong tim thay Java trong he thong. Vui long cai dat JDK.
    pause
    exit /b 1
)

if not exist "bin\Main.class" (
    echo [Java] Dang bien dich ma nguon Java...
    if not exist "bin" mkdir "bin"
    javac -encoding UTF-8 -d "bin" -cp "lib/*" src\Main.java src\dao\*.java src\model\*.java src\session\*.java src\ui\*.java src\utils\*.java
    if !ERRORLEVEL! NEQ 0 (
        echo [LOI] Bien dich that bai.
        pause
        exit /b 1
    )
    echo [Java] Bien dich ma nguon thanh cong.
) else (
    echo [Java] File thuc thi da san sang.
)

echo.
echo [3/3] Dang khoi dong ung dung HaUI Course App...
echo.

java -cp "bin;lib/*" Main

if !ERRORLEVEL! NEQ 0 (
    echo.
    echo [THONG BAO] Ung dung da dong voi ma loi: !ERRORLEVEL!
    pause
)
