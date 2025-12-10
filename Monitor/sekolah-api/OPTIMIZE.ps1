Write-Host "================================" -ForegroundColor Cyan
Write-Host "Mengoptimalkan Laravel Server" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "[1/5] Clearing cache..." -ForegroundColor Yellow
php artisan cache:clear

Write-Host ""
Write-Host "[2/5] Clearing config cache..." -ForegroundColor Yellow
php artisan config:clear

Write-Host ""
Write-Host "[3/5] Caching config untuk performa lebih baik..." -ForegroundColor Yellow
php artisan config:cache

Write-Host ""
Write-Host "[4/5] Caching routes untuk performa lebih baik..." -ForegroundColor Yellow
php artisan route:cache

Write-Host ""
Write-Host "[5/5] Optimizing autoloader..." -ForegroundColor Yellow
$composerResult = composer dump-autoload --optimize --no-dev --classmap-authoritative 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "Skipping composer optimization (development mode)" -ForegroundColor Gray
    composer dump-autoload -o
}

Write-Host ""
Write-Host "================================" -ForegroundColor Green
Write-Host "Optimasi Selesai!" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Green
Write-Host ""
Write-Host "Tips untuk performa maksimal:" -ForegroundColor White
Write-Host "- Pastikan APP_DEBUG=false di production" -ForegroundColor Gray
Write-Host "- Gunakan OPcache PHP" -ForegroundColor Gray
Write-Host "- Set LOG_LEVEL=error di .env" -ForegroundColor Gray
Write-Host ""
