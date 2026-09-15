$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $PSScriptRoot
[xml]$pom = Get-Content -LiteralPath (Join-Path $root 'pom.xml') -Raw
$steamPath = $pom.project.properties.'Steam.path'
$gameJar = Join-Path $steamPath 'common/SlayTheSpire/desktop-1.0.jar'
$baseModJar = Join-Path $steamPath 'workshop/content/646570/1605833019/BaseMod.jar'
$classes = Join-Path $root 'target/classes'
$testClasses = Join-Path $root 'target/test-classes'
$classPath = "$classes;$testClasses;$gameJar;$baseModJar"

New-Item -ItemType Directory -Path $testClasses -Force | Out-Null
javac -encoding UTF-8 -source 8 -target 8 -cp $classPath -d $testClasses (Join-Path $PSScriptRoot 'SourcePrivateKeyEvaluatorTest.java')
if ($LASTEXITCODE -ne 0) {
    throw 'Source Private Key test compilation failed. Run Maven compile first.'
}
java -cp $classPath Ptilopsis.potions.SourcePrivateKeyEvaluatorTest
if ($LASTEXITCODE -ne 0) {
    throw 'Source Private Key evaluation tests failed.'
}
