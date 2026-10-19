$ErrorActionPreference = "Stop"
Set-Location -LiteralPath $PSScriptRoot

$driver = Join-Path $PSScriptRoot "lib\mysql-connector-j-8.4.0.jar"
$sourceDir = Join-Path $PSScriptRoot "src"
$outputDir = Join-Path $PSScriptRoot "bin"

if (-not (Test-Path -LiteralPath $driver)) {
    Write-Host "MySQL JDBC driver not found: $driver" -ForegroundColor Red
    Read-Host "Press Enter to close"
    exit 1
}

if (-not (Get-Command javac.exe -ErrorAction SilentlyContinue) -or
    -not (Get-Command java.exe -ErrorAction SilentlyContinue)) {
    Write-Host "Java JDK not found. Install a JDK and make sure javac is on PATH." -ForegroundColor Red
    Read-Host "Press Enter to close"
    exit 1
}

$sourceFiles = @(Get-ChildItem -LiteralPath $sourceDir -Recurse -Filter "*.java" |
    ForEach-Object { $_.FullName })
if ($sourceFiles.Count -eq 0) {
    Write-Host "No Java source files found in $sourceDir" -ForegroundColor Red
    Read-Host "Press Enter to close"
    exit 1
}

New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
Write-Host "Compiling application..."
& javac.exe -encoding UTF-8 -cp $driver -d $outputDir $sourceFiles
if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed. Fix the compiler errors above." -ForegroundColor Red
    Read-Host "Press Enter to close"
    exit 1
}

$dbUser = Read-Host "MySQL username [root]"
if ([string]::IsNullOrWhiteSpace($dbUser)) {
    $dbUser = "root"
}

$securePassword = Read-Host "MySQL password (not the gym app login password)" -AsSecureString
$passwordPointer = [IntPtr]::Zero
try {
    $passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
    $env:GYM_DB_USER = $dbUser
    $env:GYM_DB_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
    Write-Host "Starting Gym Workout Manager..."
    & java.exe -cp "$outputDir;$driver" Main
    $appExitCode = $LASTEXITCODE
}
finally {
    if ($passwordPointer -ne [IntPtr]::Zero) {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
    }
    Remove-Item Env:GYM_DB_USER -ErrorAction SilentlyContinue
    Remove-Item Env:GYM_DB_PASSWORD -ErrorAction SilentlyContinue
}

if ($appExitCode -ne 0) {
    Write-Host "The application exited with code $appExitCode." -ForegroundColor Red
}
Read-Host "Press Enter to close"
exit $appExitCode
