@echo off
title Sistem Kasir Sederhana POS Desktop - Rafael Rizky (3125522007)
echo ======================================================================
echo    MEMULAI SISTEM KASIR SEDERHANA (POINT OF SALE) VERSI DESKTOP GUI
echo    Mahasiswa : Rafael Rizky Luga Tanjung (NRP: 3125522007)
echo    Kampus    : PENS PSDKU Sumenep (2026)
echo ======================================================================
echo.

cd /d "%~dp0P15 POS System Desktop Version 3125522007_Rafael_Rizky\SourceCode"

echo Memeriksa file binari...
if not exist "bin\MainDesktop.class" (
    echo Mengompilasi program pertama kali...
    if not exist "bin" mkdir bin
    javac -d bin MainDesktop.java model/*.java model/exception/*.java view/*.java
    if %errorlevel% neq 0 (
        echo [ERROR] Kompilasi gagal! Pastikan JDK terpasang dengan benar.
        pause
        exit /b %errorlevel%
    )
    echo Kompilasi berhasil!
)

echo Menjalankan aplikasi Desktop GUI...
start javaw -cp bin MainDesktop

exit /b 0

