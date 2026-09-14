# ============================================================
# Inmobiliaria UTS - empaquetar_war.ps1
# Compila las fuentes y genera Inmobiliaria.war para desplegar
# la aplicación en cualquier servidor web (Tomcat).
#
# Uso:  powershell -ExecutionPolicy Bypass -File scripts\empaquetar_war.ps1
# Salida: scripts\Inmobiliaria.war
#
# EXCLUSIONES de seguridad:
#   - WEB-INF\classes\smtp.properties NO se incluye (contiene
#     credenciales reales de Mailjet). Si el destino la necesita,
#     cree un smtp.properties nuevo en la instancia desplegada.
#   - uploads\, src\, test\ y *.class durante empaquetado no se copian.
# ============================================================
param([string]$Salida = "$PSScriptRoot\Inmobiliaria.war", [switch]$ConSmtp)

$ErrorActionPreference = "Stop"
$raiz = Split-Path -Parent $PSScriptRoot
Set-Location $raiz

$jar = (Get-Command jar.exe -ErrorAction SilentlyContinue).Source
if (-not $jar) {
    $jar = (Get-ChildItem "C:\Program Files\Java", "C:\Program Files\Eclipse Adoptium", "$env:LOCALAPPDATA\Programs" `
        -Recurse -Filter jar.exe -ErrorAction SilentlyContinue | Select-Object -First 1).FullName
}
if (-not $jar) { throw "No se encontró jar.exe. Instale el JDK o agregue la carpeta bin del JDK al PATH." }

Write-Host "==> 1/3 Compilando fuentes a WEB-INF\classes ..."
$tmp = Join-Path $env:TEMP "inmo_war_$PID"
New-Item -ItemType Directory -Force -Path $tmp | Out-Null
javac -encoding UTF-8 `
    -cp "C:\xampp\tomcat\lib\servlet-api.jar;C:\xampp\tomcat\lib\jsp-api.jar;WEB-INF\lib\mysql-connector-j-8.0.33.jar;WEB-INF\lib\javax.mail-1.6.2.jar;WEB-INF\lib\activation-1.1.1.jar" `
    -d WEB-INF\classes (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName })
if ($LASTEXITCODE -ne 0) { throw "La compilación falló." }

Write-Host "==> 2/3 Copiando la aplicación (sin smtp.properties ni fuentes) ..."
Get-ChildItem -Force | Where-Object { $_.Name -notin @('src', 'test', '.git') } | ForEach-Object {
    Copy-Item $_.FullName -Destination $tmp -Recurse -Force
}
if (Test-Path "$tmp\WEB-INF\classes\smtp.properties") { Remove-Item "$tmp\WEB-INF\classes\smtp.properties" }
Get-ChildItem "$tmp\uploads" -ErrorAction SilentlyContinue | Remove-Item -Recurse -Force

Write-Host "==> 3/3 Generando $Salida ..."
Push-Location $tmp
& $jar -cf $Salida -C . .
Pop-Location
Remove-Item $tmp -Recurse -Force
Write-Host "Listo: $Salida"