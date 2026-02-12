# Surveille src/ et pom.xml, lance "mvn compile" à chaque modification.
# DevTools redémarre l'app automatiquement après la compilation (avec profil dev).
# Utilisation : Terminal 1 = .\run-dev.bat   Terminal 2 = .\watch-compile.ps1

$projectRoot = $PSScriptRoot
$srcPath = Join-Path $projectRoot "src"
$pomPath = Join-Path $projectRoot "pom.xml"

if (-not (Test-Path $srcPath)) {
    Write-Host "Dossier src introuvable." -ForegroundColor Red
    exit 1
}

Write-Host "Rechargement auto : surveillance de src/ et pom.xml (Ctrl+C pour arreter)" -ForegroundColor Cyan
Write-Host "Lancez l'app avec run-dev.bat dans un autre terminal." -ForegroundColor Gray

$lastCompile = Get-Date
$debounceSeconds = 2

while ($true) {
    $srcChanged = Get-ChildItem -Path $srcPath -Recurse -File -ErrorAction SilentlyContinue |
        Where-Object { $_.LastWriteTime -gt $lastCompile }
    $pomChanged = $false
    if (Test-Path $pomPath) {
        $pomChanged = (Get-Item $pomPath).LastWriteTime -gt $lastCompile
    }
    if ($srcChanged -or $pomChanged) {
        $lastCompile = Get-Date
        Write-Host "[$($lastCompile.ToString('HH:mm:ss'))] Compilation..." -ForegroundColor Yellow
        Set-Location $projectRoot
        & mvn compile -q
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[$(Get-Date -Format 'HH:mm:ss')] OK - redemarrage automatique (DevTools)." -ForegroundColor Green
        } else {
            Write-Host "[$(Get-Date -Format 'HH:mm:ss')] Erreur de compilation." -ForegroundColor Red
        }
    }
    Start-Sleep -Seconds $debounceSeconds
}
