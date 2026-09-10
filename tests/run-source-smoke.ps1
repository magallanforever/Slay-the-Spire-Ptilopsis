$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $PSScriptRoot
Push-Location $root
try {
    mvn -DskipTests compile

    $requiredClasses = @(
        'target/classes/Ptilopsis/Ptilopsis.class',
        'target/classes/Ptilopsis/character/PtilopsisCharacter.class',
        'target/classes/Ptilopsis/cards/QuickIteration.class',
        'target/classes/Ptilopsis/cards/Offline.class',
        'target/classes/Ptilopsis/cards/Muelsyse.class',
        'target/classes/Ptilopsis/threads/ThreadOrb.class',
        'target/classes/Ptilopsis/patches/ThreadOrbPatches.class',
        'target/classes/Ptilopsis/threads/effects/CardThreadFunction.class',
        'target/classes/Ptilopsis/powers/ThreadNetworkPower.class',
        'target/classes/Ptilopsis/threads/effects/OfflineThreadEffect.class'
    )

    foreach ($classFile in $requiredClasses) {
        $path = Join-Path $root $classFile
        if (-not (Test-Path -Path $path)) {
            throw "Missing expected compiled class: $classFile"
        }
    }

    'source_smoke_ok'
} finally {
    Pop-Location
}
