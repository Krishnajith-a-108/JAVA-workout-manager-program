$ErrorActionPreference = "Stop"
Set-Location -LiteralPath $PSScriptRoot

Add-Type -AssemblyName System.Windows.Forms
Add-Type -AssemblyName System.Drawing

function Show-LauncherMessage([string]$Message, [string]$Title, [System.Windows.Forms.MessageBoxIcon]$Icon) {
    [System.Windows.Forms.MessageBox]::Show(
        $Message,
        $Title,
        [System.Windows.Forms.MessageBoxButtons]::OK,
        $Icon) | Out-Null
}

function Get-DatabaseCredentials {
    $form = New-Object System.Windows.Forms.Form
    $form.Text = "Gym Workout Manager - MySQL"
    $form.ClientSize = New-Object System.Drawing.Size(390, 150)
    $form.FormBorderStyle = [System.Windows.Forms.FormBorderStyle]::FixedDialog
    $form.StartPosition = [System.Windows.Forms.FormStartPosition]::CenterScreen
    $form.MaximizeBox = $false
    $form.MinimizeBox = $false
    $form.ShowInTaskbar = $true

    $userLabel = New-Object System.Windows.Forms.Label
    $userLabel.Text = "MySQL username:"
    $userLabel.Location = New-Object System.Drawing.Point(15, 18)
    $userLabel.AutoSize = $true

    $userInput = New-Object System.Windows.Forms.TextBox
    $userInput.Location = New-Object System.Drawing.Point(145, 15)
    $userInput.Size = New-Object System.Drawing.Size(225, 24)
    $userInput.Text = "root"

    $passwordLabel = New-Object System.Windows.Forms.Label
    $passwordLabel.Text = "MySQL password:"
    $passwordLabel.Location = New-Object System.Drawing.Point(15, 52)
    $passwordLabel.AutoSize = $true

    $passwordInput = New-Object System.Windows.Forms.TextBox
    $passwordInput.Location = New-Object System.Drawing.Point(145, 49)
    $passwordInput.Size = New-Object System.Drawing.Size(225, 24)
    $passwordInput.UseSystemPasswordChar = $true

    $okButton = New-Object System.Windows.Forms.Button
    $okButton.Text = "Run"
    $okButton.Location = New-Object System.Drawing.Point(195, 95)
    $okButton.Size = New-Object System.Drawing.Size(80, 30)
    $okButton.DialogResult = [System.Windows.Forms.DialogResult]::OK

    $cancelButton = New-Object System.Windows.Forms.Button
    $cancelButton.Text = "Cancel"
    $cancelButton.Location = New-Object System.Drawing.Point(285, 95)
    $cancelButton.Size = New-Object System.Drawing.Size(80, 30)
    $cancelButton.DialogResult = [System.Windows.Forms.DialogResult]::Cancel

    $form.Controls.AddRange(@($userLabel, $userInput, $passwordLabel, $passwordInput, $okButton, $cancelButton))
    $form.AcceptButton = $okButton
    $form.CancelButton = $cancelButton
    $form.add_Shown({ $userInput.Focus() })

    try {
        if ($form.ShowDialog() -ne [System.Windows.Forms.DialogResult]::OK) {
            return $null
        }
        if ([string]::IsNullOrWhiteSpace($userInput.Text)) {
            Show-LauncherMessage "Enter your MySQL username." "MySQL credentials required" `
                ([System.Windows.Forms.MessageBoxIcon]::Warning)
            return $null
        }

        return @{
            User = $userInput.Text.Trim()
            Password = $passwordInput.Text
        }
    }
    finally {
        $form.Dispose()
    }
}

try {
    $driver = Join-Path $PSScriptRoot "lib\mysql-connector-j-8.4.0.jar"
    $sourceDir = Join-Path $PSScriptRoot "src"
    $outputDir = Join-Path $PSScriptRoot "bin"

    if (-not (Test-Path -LiteralPath $driver)) {
        throw "MySQL JDBC driver not found: $driver"
    }

    if (-not (Get-Command javac.exe -ErrorAction SilentlyContinue) -or
        -not (Get-Command java.exe -ErrorAction SilentlyContinue)) {
        throw "Java JDK not found. Install a JDK and make sure java and javac are on PATH."
    }

    $sourceFiles = @(Get-ChildItem -LiteralPath $sourceDir -Recurse -Filter "*.java" |
        ForEach-Object { $_.FullName })
    if ($sourceFiles.Count -eq 0) {
        throw "No Java source files found in $sourceDir"
    }

    New-Item -ItemType Directory -Path $outputDir -Force | Out-Null
    $compileOutput = (& javac.exe -encoding UTF-8 -cp $driver -d $outputDir $sourceFiles 2>&1 | Out-String)
    if ($LASTEXITCODE -ne 0) {
        throw "Compilation failed:`r`n$compileOutput"
    }

    $credentials = Get-DatabaseCredentials
    if ($null -eq $credentials) {
        exit 0
    }

    $env:GYM_DB_USER = $credentials.User
    $env:GYM_DB_PASSWORD = $credentials.Password
    $javaOutput = (& java.exe -cp "$outputDir;$driver" Main 2>&1 | Out-String)
    $appExitCode = $LASTEXITCODE
    if ($appExitCode -ne 0) {
        Show-LauncherMessage "The application exited with code $appExitCode.`r`n$javaOutput" `
            "Gym Workout Manager" ([System.Windows.Forms.MessageBoxIcon]::Error)
    }
}
catch {
    Show-LauncherMessage $_.Exception.Message "Could not start Gym Workout Manager" `
        ([System.Windows.Forms.MessageBoxIcon]::Error)
    exit 1
}
finally {
    Remove-Item Env:GYM_DB_USER -ErrorAction SilentlyContinue
    Remove-Item Env:GYM_DB_PASSWORD -ErrorAction SilentlyContinue
}
