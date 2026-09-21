$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $PSScriptRoot
Push-Location $root
try {
    mvn -DskipTests compile
    if ($LASTEXITCODE -ne 0) {
        throw 'Maven compile failed.'
    }

    $requiredClasses = @(
        'target/classes/Ptilopsis/Ptilopsis.class',
        'target/classes/Ptilopsis/character/PtilopsisCharacter.class',
        'target/classes/Ptilopsis/relics/RhineLabBadge.class',
        'target/classes/Ptilopsis/cards/QuickIteration.class',
        'target/classes/Ptilopsis/cards/Malloc.class',
        'target/classes/Ptilopsis/actions/MallocAction.class',
        'target/classes/Ptilopsis/cards/Offline.class',
        'target/classes/Ptilopsis/cards/Muelsyse.class',
        'target/classes/Ptilopsis/cards/Enkephalin.class',
        'target/classes/Ptilopsis/cards/Capacitor.class',
        'target/classes/Ptilopsis/cards/DivideAndConquer.class',
        'target/classes/Ptilopsis/cards/LIS.class',
        'target/classes/Ptilopsis/cards/OverflowCheck.class',
        'target/classes/Ptilopsis/cards/ProtocolConcurrency.class',
        'target/classes/Ptilopsis/cards/MemoryLeak.class',
        'target/classes/Ptilopsis/cards/GradientDescent.class',
        'target/classes/Ptilopsis/cards/BloodBrainBarrier.class',
        'target/classes/Ptilopsis/actions/BloodBrainBarrierAction.class',
        'target/classes/Ptilopsis/threads/effects/GradientDescentThreadEffect.class',
        'target/classes/Ptilopsis/cards/MedicalInstruction.class',
        'target/classes/Ptilopsis/threads/effects/MedicalPulseThreadEffect.class',
        'target/classes/Ptilopsis/cards/Rosmontis.class',
        'target/classes/Ptilopsis/cards/Saria.class',
        'target/classes/Ptilopsis/cards/Silence.class',
        'target/classes/Ptilopsis/powers/SilencePower.class',
        'target/classes/Ptilopsis/actions/SilenceDraftAction.class',
        'target/classes/Ptilopsis/powers/StunPower.class',
        'target/classes/Ptilopsis/threads/effects/CalcificationThreadEffect.class',
        'target/classes/Ptilopsis/patches/CalcificationBlockPatch.class',
        'target/classes/Ptilopsis/cards/ExtremeDataConstruction.class',
        'target/classes/Ptilopsis/cards/CrystallineDream.class',
        'target/classes/Ptilopsis/cards/Hyperthreading.class',
        'target/classes/Ptilopsis/cards/MergeSort.class',
        'target/classes/Ptilopsis/cards/DrawPileOrder.class',
        'target/classes/Ptilopsis/potions/SourcePrivateKey.class',
        'target/classes/Ptilopsis/potions/SourcePrivateKeyEvaluator.class',
        'target/classes/Ptilopsis/actions/SourcePrivateKeyAction.class',
        'target/classes/Ptilopsis/threads/ThreadOrb.class',
        'target/classes/Ptilopsis/threads/ThreadCardPlayback.class',
        'target/classes/Ptilopsis/threads/ThreadCardPlayback$Origin.class',
        'target/classes/Ptilopsis/cards/ThreadEffectCard.class',
        'target/classes/Ptilopsis/patches/ThreadOrbPatches.class',
        'target/classes/Ptilopsis/threads/effects/CardThreadFunction.class',
        'target/classes/Ptilopsis/threads/ThreadNetwork.class',
        'target/classes/Ptilopsis/powers/EnkephalinPower.class',
        'target/classes/Ptilopsis/powers/MasterTheoremPower.class',
        'target/classes/Ptilopsis/powers/ExtremeDataConstructionPower.class',
        'target/classes/Ptilopsis/powers/HyperthreadingPower.class',
        'target/classes/Ptilopsis/threads/effects/OfflineThreadEffect.class'
    )

    foreach ($classFile in $requiredClasses) {
        $path = Join-Path $root $classFile
        if (-not (Test-Path -Path $path)) {
            throw "Missing expected compiled class: $classFile"
        }
    }

    foreach ($language in @('ZHS', 'ENG')) {
        $cardsPath = Join-Path $root "target/classes/PtilopsisResources/localization/$language/cards.json"
        $cards = Get-Content -LiteralPath $cardsPath -Raw -Encoding UTF8 | ConvertFrom-Json
        foreach ($id in @('Ptilopsis:CacheBarrier', 'Ptilopsis:WakeSignal', 'Ptilopsis:EnergyRecovery', 'Ptilopsis:MedicalInstruction', 'Ptilopsis:MedicalPulse', 'Ptilopsis:CalcificationGuard', 'Ptilopsis:CalcificationRelease', 'Ptilopsis:Saria', 'Ptilopsis:GradientDescent', 'Ptilopsis:GradientStep', 'Ptilopsis:GradientRelease')) {
            if (-not $cards.$id.NAME -or -not $cards.$id.DESCRIPTION) {
                throw "Missing thread effect card localization: $language $id"
            }
        }
        $relicPath = Join-Path $root "target/classes/PtilopsisResources/localization/$language/relics.json"
        $relics = Get-Content -LiteralPath $relicPath -Raw -Encoding UTF8 | ConvertFrom-Json
        $badge = $relics.'Ptilopsis:RhineLabBadge'
        if (-not $badge.NAME -or $badge.DESCRIPTIONS.Count -ne 1) {
            throw "Missing RhineLabBadge localization: $language"
        }
        $potionPath = Join-Path $root "target/classes/PtilopsisResources/localization/$language/potions.json"
        $potions = Get-Content -LiteralPath $potionPath -Raw -Encoding UTF8 | ConvertFrom-Json
        $potion = $potions.'Ptilopsis:SourcePrivateKey'
        if (-not $potion.NAME -or $potion.DESCRIPTIONS.Count -ne 3) {
            throw "Missing SourcePrivateKey localization: $language"
        }
    }

    'source_smoke_ok'
} finally {
    Pop-Location
}
