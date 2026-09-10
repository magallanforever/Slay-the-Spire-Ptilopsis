package Ptilopsis;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import Ptilopsis.cards.BootThread;
import Ptilopsis.cards.CacheRefresh;
import Ptilopsis.cards.Closure;
import Ptilopsis.cards.CoroutineYield;
import Ptilopsis.cards.Magallan;
import Ptilopsis.cards.MemoryGuard;
import Ptilopsis.cards.Muelsyse;
import Ptilopsis.cards.NeuralStrike;
import Ptilopsis.cards.Offline;
import Ptilopsis.cards.QuickIteration;
import Ptilopsis.cards.ReadOnlyThread;
import Ptilopsis.cards.WakeRoutine;
import Ptilopsis.character.PtilopsisCharacter;
import basemod.BaseMod;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditCharactersSubscriber;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditStringsSubscriber;

@SpireInitializer
public class Ptilopsis implements EditCardsSubscriber, EditCharactersSubscriber, EditKeywordsSubscriber, EditStringsSubscriber {
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
                new WakeRoutine(),
                new CacheRefresh(),
                new CoroutineYield(),
                new QuickIteration(),
                new Offline(),
                new Magallan(),
                new Muelsyse(),
                new Closure()
        );

        for (AbstractCard card : cards) {
            BaseMod.addCard(card);
            UnlockTracker.unlockCard(card.cardID);
        }
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
        if (Settings.language == Settings.GameLanguage.ZHS) {
            BaseMod.addKeyword(
                    KEYWORD_MOD_ID,
                    "恰当的时候",
                    new String[]{"恰当的时候"},
                    "op 是本场战斗中你已打出的牌数。未升级时，本牌费用为 (op ^ 15) & 15。升级后，本牌费用为 (op ^ 7) & 7。"
            );
        } else {
            BaseMod.addKeyword(
                    KEYWORD_MOD_ID,
                    "Right Moment",
                    new String[]{"right_moment"},
                    "op is the number of cards you have played this combat. Unupgraded, this card costs (op ^ 15) & 15. Upgraded, it costs (op ^ 7) & 7."
            );
        }
    }

    private static void loadLocalization(String language) {
        BaseMod.loadCustomStringsFile(CardStrings.class, resourcePath("localization/" + language + "/cards.json"));
        BaseMod.loadCustomStringsFile(CharacterStrings.class, resourcePath("localization/" + language + "/characters.json"));
        BaseMod.loadCustomStringsFile(PowerStrings.class, resourcePath("localization/" + language + "/powers.json"));
    }
}
