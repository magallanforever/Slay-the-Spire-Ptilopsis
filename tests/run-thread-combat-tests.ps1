$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
[xml]$pom = Get-Content -LiteralPath (Join-Path $root 'pom.xml') -Raw
$steam = $pom.project.properties.'Steam.path'
$classes = Join-Path $root 'target/classes'
$testClasses = Join-Path $root 'target/thread-combat-tests'
$gameJar = Join-Path $steam 'common/SlayTheSpire/desktop-1.0.jar'
$baseModJar = Join-Path $steam 'workshop/content/646570/1605833019/BaseMod.jar'
$mtsJar = Join-Path $steam 'workshop/content/646570/1605060445/ModTheSpire.jar'
$classPath = "$classes;$testClasses;$gameJar;$baseModJar;$mtsJar"
New-Item -ItemType Directory -Path $testClasses -Force | Out-Null
javac -encoding UTF-8 -source 8 -target 8 -cp $classPath -d $testClasses (Join-Path $PSScriptRoot 'ThreadCombatTest.java') (Join-Path $PSScriptRoot 'SilenceTest.java') (Join-Path $PSScriptRoot 'CardInteractionTest.java')
if ($LASTEXITCODE -ne 0) { throw 'Thread combat test compilation failed. Run Maven compile first.' }
Push-Location $testClasses
try {
    java -cp $classPath Ptilopsis.threads.ThreadCombatTest
    if ($LASTEXITCODE -ne 0) { throw 'Thread combat tests failed.' }
} finally {
    Pop-Location
}
