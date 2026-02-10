# Surveille src/ et lance "mvn compile" à chaque modification.
# DevTools redémarrera l'app automatiquement après la compilation.
# Utilisation : dans un 2e terminal, exécuter .\watch-compile.ps1
# (Terminal 1 : .\run-dev.bat   Terminal 2 : .\watch-compile.ps1)

$srcPath = Join-Path $PSScriptRoot "src"
if (-not (Test-Path $srcPath)) {
    Write-Host "Dossier src introuvable." -ForegroundColor Red
    exit 1
}

Write-Host "Surveillance de $srcPath - Modifiez un fichier pour declencher mvn compile (Ctrl+C pour arreter)" -ForegroundColor Cyan

$lastCompile = Get-Date
$debounceSeconds = 2

while ($true) {
    $changes = Get-ChildItem -Path $srcPath -Recurse -File -ErrorAction SilentlyContinue |
        Where-Object { $_.LastWriteTime -gt $lastCompile }
    if ($changes) {
        $lastCompile = Get-Date
        Write-Host "[$(Get-Date -Format 'HH:mm:ss')] Compilation..." -ForegroundColor Yellow
        & mvn compile -q
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[$(Get-Date -Format 'HH:mm:ss')] OK - DevTools va redemarrer l'app si besoin." -ForegroundColor Green
        } else {
            Write-Host "[$(Get-Date -Format 'HH:mm:ss')] Erreur de compilation." -ForegroundColor Red
        }
    }
    Start-Sleep -Seconds $debounceSeconds
}
