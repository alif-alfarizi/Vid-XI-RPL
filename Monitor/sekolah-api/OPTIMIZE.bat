@echo off
echo ================================
echo Mengoptimalkan Laravel Server
echo ================================

echo.
echo [1/5] Clearing cache...
php artisan cache:clear

echo.
echo [2/5] Clearing config cache...
php artisan config:clear

echo.
echo [3/5] Caching config untuk performa lebih baik...
php artisan config:cache

echo.
echo [4/5] Caching routes untuk performa lebih baik...
php artisan route:cache

echo.
echo [5/5] Optimizing autoloader...
composer dump-autoload --optimize --no-dev --classmap-authoritative 2>nul
if errorlevel 1 (
    echo Skipping composer optimization ^(development mode^)
    composer dump-autoload -o
)

echo.
echo ================================
echo Optimasi Selesai!
echo ================================
echo.
echo Tips untuk performa maksimal:
echo - Pastikan APP_DEBUG=false di production
echo - Gunakan OPcache PHP
echo - Set LOG_LEVEL=error di .env
echo.

pause
