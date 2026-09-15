$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$testClasses = Join-Path $root 'target/card-review-tests'
New-Item -ItemType Directory -Path $testClasses -Force | Out-Null

javac -encoding UTF-8 -source 8 -target 8 -d $testClasses (Join-Path $root 'src/main/java/Ptilopsis/cards/DrawPileOrder.java') (Join-Path $PSScriptRoot 'DrawPileOrderTest.java')
if ($LASTEXITCODE -ne 0) {
    throw 'Draw pile order test compilation failed.'
}
java -cp $testClasses Ptilopsis.cards.DrawPileOrderTest
if ($LASTEXITCODE -ne 0) {
    throw 'Draw pile order tests failed.'
}

foreach ($language in @('ZHS', 'ENG')) {
    $localization = Join-Path $root "src/main/resources/PtilopsisResources/localization/$language"
    $cards = Get-Content -LiteralPath (Join-Path $localization 'cards.json') -Raw -Encoding UTF8 | ConvertFrom-Json
    foreach ($sourceFile in Get-ChildItem -LiteralPath (Join-Path $root 'src/main/java/Ptilopsis/cards') -Filter '*.java') {
        $source = Get-Content -LiteralPath $sourceFile.FullName -Raw -Encoding UTF8
        if ($source -notmatch 'public static final String ID\s*=') {
            continue
        }
        # Resolve the two ID declarations used by this project's cards.
        if ($source -match 'String ID\s*=\s*Ptilopsis\.makeID\((\w+)\.class\.getSimpleName\(\)\)') {
            $id = 'Ptilopsis:' + $Matches[1]
        } elseif ($source -match 'String ID\s*=\s*Ptilopsis\.makeID\("([^"]+)"\)') {
            $id = 'Ptilopsis:' + $Matches[1]
        } else {
            throw "Unsupported card ID declaration: $($sourceFile.Name)"
        }
        $entry = $cards.PSObject.Properties[$id]
        if (-not $entry -or [string]::IsNullOrWhiteSpace($entry.Value.NAME) -or
                [string]::IsNullOrWhiteSpace($entry.Value.DESCRIPTION)) {
            throw "Missing card localization: $language $id ($($sourceFile.Name))"
        }
        if ($source.Contains('.UPGRADE_DESCRIPTION') -and
                [string]::IsNullOrWhiteSpace($entry.Value.UPGRADE_DESCRIPTION)) {
            throw "Missing upgraded card description: $language $id"
        }
    }
    $keywords = Get-Content -LiteralPath (Join-Path $localization 'keywords.json') -Raw -Encoding UTF8 | ConvertFrom-Json
    $names = @()
    foreach ($keyword in $keywords) {
        if (-not $keyword.NAMES -or -not $keyword.DESCRIPTION) {
            throw "Invalid keyword: $language"
        }
        $names += $keyword.NAMES
    }
    foreach ($file in Get-ChildItem -LiteralPath $localization -Filter '*.json') {
        $json = Get-Content -LiteralPath $file.FullName -Raw -Encoding UTF8
        $null = $json | ConvertFrom-Json
        foreach ($match in [regex]::Matches($json, 'ptilopsis:([\p{L}_]+)')) {
            if ($match.Groups[1].Value -notin $names) {
                throw "Unknown keyword in ${language}: $($match.Value)"
            }
        }
    }
}

$zhsCardsPath = Join-Path $root 'src/main/resources/PtilopsisResources/localization/ZHS/cards.json'
$zhsCards = Get-Content -LiteralPath $zhsCardsPath -Raw -Encoding UTF8 | ConvertFrom-Json
$nativeKeywords = @('保留', '消耗', '固有', '格挡', '升级', '易伤')
$customKeywords = @('线程', '构造', '函数', '挂载', '激发', '析构', '主定理', '逆序对', '余震')
foreach ($card in $zhsCards.PSObject.Properties) {
    foreach ($field in @('DESCRIPTION', 'UPGRADE_DESCRIPTION')) {
        $description = $card.Value.$field
        if (-not $description) {
            continue
        }
        foreach ($keyword in $nativeKeywords) {
            foreach ($match in [regex]::Matches($description, [regex]::Escape($keyword))) {
                $leftSeparated = $match.Index -eq 0 -or [char]::IsWhiteSpace($description[$match.Index - 1])
                $rightIndex = $match.Index + $match.Length
                $rightSeparated = $rightIndex -eq $description.Length -or [char]::IsWhiteSpace($description[$rightIndex])
                if (-not $leftSeparated -or -not $rightSeparated) {
                    throw "Native keyword must be separated by spaces: $($card.Name).$field -> $keyword"
                }
            }
        }
        foreach ($keyword in $customKeywords) {
            $withoutRegisteredKeywords = $description -replace [regex]::Escape("ptilopsis:$keyword"), ''
            if ($withoutRegisteredKeywords.Contains($keyword)) {
                throw "Custom keyword is missing its namespace: $($card.Name).$field -> $keyword"
            }
        }
    }
}
'card_localization_ok'
