package Ptilopsis;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.google.gson.Gson;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.localization.Keyword;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import Ptilopsis.cards.BootThread;
import Ptilopsis.cards.CacheRefresh;
import Ptilopsis.cards.Closure;
import Ptilopsis.cards.CoroutineYield;
import Ptilopsis.cards.CrystallineDream;
import Ptilopsis.cards.DivideAndConquer;
import Ptilopsis.cards.Enkephalin;
import Ptilopsis.cards.ExtremeDataConstruction;
import Ptilopsis.cards.Hyperthreading;
import Ptilopsis.cards.LIS;
import Ptilopsis.cards.Magallan;
import Ptilopsis.cards.MemoryGuard;
import Ptilopsis.cards.MedicalInstruction;
import Ptilopsis.cards.MergeSort;
import Ptilopsis.cards.Muelsyse;
import Ptilopsis.cards.NeuralStrike;
import Ptilopsis.cards.Offline;
import Ptilopsis.cards.QuickIteration;
import Ptilopsis.cards.ReadOnlyThread;
import Ptilopsis.cards.Rosmontis;
import Ptilopsis.cards.WakeRoutine;
import Ptilopsis.character.PtilopsisCharacter;
import Ptilopsis.potions.SourcePrivateKey;
import Ptilopsis.relics.RhineLabBadge;
import basemod.BaseMod;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditCharactersSubscriber;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;

@SpireInitializer
public class Ptilopsis implements EditCardsSubscriber, EditCharactersSubscriber, EditKeywordsSubscriber, EditRelicsSubscriber, EditStringsSubscriber, PostInitializeSubscriber {
    public static final String MOD_ID = "Ptilopsis";
    private static final String KEYWORD_MOD_ID = MOD_ID.toLowerCase();

    public static final Color PTILOPSIS_COLOR = new Color(0.36078432F, 0.80784315F, 0.8745098F, 1.0F);

    private static final String CHARACTER_BUTTON = resourcePath("img/char/Character_Button.png");
    private static final String CHARACTER_PORTRAIT = resourcePath("img/char/Character_Portrait.png");
    private static final String BG_ATTACK_512 = resourcePath("img/512/bg_attack_512.png");
    private static final String BG_SKILL_512 = resourcePath("img/512/bg_skill_512.png");
    private static final String BG_POWER_512 = resourcePath("img/512/bg_power_512.png");
    private static final String ENERGY_ORB = resourcePath("img/char/cost_orb.png");
    private static final String BG_ATTACK_1024 = resourcePath("img/1024/bg_attack.png");
    private static final String BG_SKILL_1024 = resourcePath("img/1024/bg_skill.png");
    private static final String BG_POWER_1024 = resourcePath("img/1024/bg_power.png");
    private static final String BIG_ORB = resourcePath("img/char/card_orb.png");
    private static final String SMALL_ORB = resourcePath("img/char/small_orb.png");

    public Ptilopsis() {
        BaseMod.subscribe(this);
        BaseMod.addColor(
                PtilopsisCharacter.Enums.PTILOPSIS_COLOR,
                PTILOPSIS_COLOR,
                PTILOPSIS_COLOR,
                PTILOPSIS_COLOR,
                PTILOPSIS_COLOR,
                PTILOPSIS_COLOR,
                PTILOPSIS_COLOR,
                PTILOPSIS_COLOR,
                BG_ATTACK_512,
                BG_SKILL_512,
                BG_POWER_512,
                ENERGY_ORB,
                BG_ATTACK_1024,
                BG_SKILL_1024,
                BG_POWER_1024,
                BIG_ORB,
                SMALL_ORB
        );
    }

    public static void initialize() {
        new Ptilopsis();
    }

    public static String makeID(String id) {
        return MOD_ID + ":" + id;
    }

    public static String resourcePath(String path) {
        return "PtilopsisResources/" + path;
    }

    public static String keywordID(String id) {
        return KEYWORD_MOD_ID + ":" + id;
    }

    @Override
    public void receivePostInitialize() {
        BaseMod.addPotion(SourcePrivateKey.class, PTILOPSIS_COLOR.cpy(), Color.WHITE.cpy(),
                new Color(0.85F, 0.72F, 0.28F, 1.0F), SourcePrivateKey.ID);
    }

    @Override
    public void receiveEditStrings() {
        loadLocalization("ENG");
        if (Settings.language == Settings.GameLanguage.ZHS) {
            loadLocalization("ZHS");
        }
    }

    @Override
    public void receiveEditCards() {
        List<AbstractCard> cards = Arrays.asList(
                new NeuralStrike(),
                new MemoryGuard(),
                new BootThread(),
                new ReadOnlyThread(),
                new MedicalInstruction(),
                new WakeRoutine(),
                new CacheRefresh(),
                new Enkephalin(),
                new DivideAndConquer(),
                new LIS(),
                new CoroutineYield(),
                new CrystallineDream(),
                new Hyperthreading(),
                new MergeSort(),
                new QuickIteration(),
                new Offline(),
                new Magallan(),
                new Rosmontis(),
                new ExtremeDataConstruction(),
                new Muelsyse(),
                new Closure()
        );

        for (AbstractCard card : cards) {
            BaseMod.addCard(card);
            UnlockTracker.unlockCard(card.cardID);
        }
    }

    @Override
    public void receiveEditRelics() {
        BaseMod.addRelicToCustomPool(new RhineLabBadge(), PtilopsisCharacter.Enums.PTILOPSIS_COLOR);
        UnlockTracker.markRelicAsSeen(RhineLabBadge.ID);
    }

    @Override
    public void receiveEditCharacters() {
        BaseMod.addCharacter(
                new PtilopsisCharacter(CardCrawlGame.playerName),
                CHARACTER_BUTTON,
                CHARACTER_PORTRAIT,
                PtilopsisCharacter.Enums.PTILOPSIS
        );
    }

    @Override
    public void receiveEditKeywords() {
        String language = Settings.language == Settings.GameLanguage.ZHS ? "ZHS" : "ENG";
        Keyword[] keywords = new Gson().fromJson(Gdx.files.internal(
                resourcePath("localization/" + language + "/keywords.json")).readString("UTF-8"), Keyword[].class);
        for (Keyword keyword : keywords) {
            String[] names = Arrays.stream(keyword.NAMES)
                    .map(name -> name.toLowerCase(Locale.ROOT)).toArray(String[]::new);
            BaseMod.addKeyword(KEYWORD_MOD_ID, keyword.NAMES[0], names, keyword.DESCRIPTION);
        }
    }

    private static void loadLocalization(String language) {
        BaseMod.loadCustomStringsFile(CardStrings.class, resourcePath("localization/" + language + "/cards.json"));
        BaseMod.loadCustomStringsFile(CharacterStrings.class, resourcePath("localization/" + language + "/characters.json"));
        BaseMod.loadCustomStringsFile(PowerStrings.class, resourcePath("localization/" + language + "/powers.json"));
        BaseMod.loadCustomStringsFile(PotionStrings.class, resourcePath("localization/" + language + "/potions.json"));
        BaseMod.loadCustomStringsFile(RelicStrings.class, resourcePath("localization/" + language + "/relics.json"));
    }
}
