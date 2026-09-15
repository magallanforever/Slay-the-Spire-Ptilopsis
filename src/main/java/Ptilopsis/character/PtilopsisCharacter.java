package Ptilopsis.character;

import Ptilopsis.Ptilopsis;
import Ptilopsis.cards.BootThread;
import Ptilopsis.cards.MemoryGuard;
import Ptilopsis.cards.MedicalInstruction;
import Ptilopsis.cards.NeuralStrike;
import Ptilopsis.cards.ReadOnlyThread;
import Ptilopsis.relics.RhineLabBadge;
import basemod.abstracts.CustomPlayer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.cutscenes.CutscenePanel;
import com.megacrit.cardcrawl.events.city.Vampires;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.CharacterStrings;
import com.megacrit.cardcrawl.screens.CharSelectInfo;
import java.util.ArrayList;

public class PtilopsisCharacter extends CustomPlayer {
    public static final String CHARACTER_ID = Ptilopsis.makeID("PtilopsisCharacter");

    private static final int ENERGY_PER_TURN = 3;
    private static final int STARTING_HP = 75;
    private static final int STARTING_GOLD = 99;
    private static final int STARTING_HAND_SIZE = 5;
    private static final int ASCENSION_MAX_HP_LOSS = 5;

    private static final String CHARACTER_IMAGE = Ptilopsis.resourcePath("img/char/character.png");
    private static final String SHOULDER_1 = Ptilopsis.resourcePath("img/char/shoulder1.png");
    private static final String SHOULDER_2 = Ptilopsis.resourcePath("img/char/shoulder2.png");
    private static final String CORPSE_IMAGE = Ptilopsis.resourcePath("img/char/corpse.png");
    private static final String VICTORY_1 = Ptilopsis.resourcePath("img/char/Victory1.png");
    private static final String VICTORY_2 = Ptilopsis.resourcePath("img/char/Victory2.png");
    private static final String VICTORY_3 = Ptilopsis.resourcePath("img/char/Victory3.png");

    private static final String[] ORB_TEXTURES = {
            Ptilopsis.resourcePath("img/UI/orb/layer5.png"),
            Ptilopsis.resourcePath("img/UI/orb/layer4.png"),
            Ptilopsis.resourcePath("img/UI/orb/layer3.png"),
            Ptilopsis.resourcePath("img/UI/orb/layer2.png"),
            Ptilopsis.resourcePath("img/UI/orb/layer1.png"),
            Ptilopsis.resourcePath("img/UI/orb/layer6.png"),
            Ptilopsis.resourcePath("img/UI/orb/layer5d.png"),
            Ptilopsis.resourcePath("img/UI/orb/layer4d.png"),
            Ptilopsis.resourcePath("img/UI/orb/layer3d.png"),
            Ptilopsis.resourcePath("img/UI/orb/layer2d.png"),
            Ptilopsis.resourcePath("img/UI/orb/layer1d.png")
    };

    private static final float[] LAYER_SPEED = {-40.0F, -32.0F, 20.0F, -20.0F, 0.0F, -10.0F, -8.0F, 5.0F, -5.0F, 0.0F};

    public PtilopsisCharacter(String name) {
        super(
                name,
                Enums.PTILOPSIS,
                ORB_TEXTURES,
                Ptilopsis.resourcePath("img/UI/orb/vfx.png"),
                LAYER_SPEED,
                null,
                null
        );
        this.dialogX = this.drawX;
        this.dialogY = this.drawY + 150.0F * Settings.scale;
        initializeClass(
                CHARACTER_IMAGE,
                SHOULDER_2,
                SHOULDER_1,
                CORPSE_IMAGE,
                getLoadout(),
                0.0F,
                -5.0F,
                240.0F,
                244.0F,
                new EnergyManager(ENERGY_PER_TURN)
        );
    }

    @Override
    public ArrayList<String> getStartingDeck() {
        ArrayList<String> deck = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            deck.add(NeuralStrike.ID);
        }
        for (int i = 0; i < 3; i++) {
            deck.add(MemoryGuard.ID);
        }
        deck.add(BootThread.ID);
        deck.add(ReadOnlyThread.ID);
        deck.add(MedicalInstruction.ID);
        return deck;
    }

    @Override
    public ArrayList<String> getStartingRelics() {
        ArrayList<String> relics = new ArrayList<>();
        relics.add(RhineLabBadge.ID);
        return relics;
    }

    @Override
    public CharSelectInfo getLoadout() {
        CharacterStrings strings = getCharacterStrings();
        return new CharSelectInfo(
                strings.NAMES[0],
                strings.TEXT[0],
                STARTING_HP,
                STARTING_HP,
                3,
                STARTING_GOLD,
                STARTING_HAND_SIZE,
                this,
                getStartingRelics(),
                getStartingDeck(),
                false
        );
    }

    @Override
    public String getTitle(PlayerClass playerClass) {
        return getCharacterStrings().NAMES[0];
    }

    @Override
    public AbstractCard.CardColor getCardColor() {
        return Enums.PTILOPSIS_COLOR;
    }

    @Override
    public AbstractCard getStartCardForEvent() {
        return new NeuralStrike();
    }

    @Override
    public Color getCardTrailColor() {
        return Ptilopsis.PTILOPSIS_COLOR;
    }

    @Override
    public int getAscensionMaxHPLoss() {
        return ASCENSION_MAX_HP_LOSS;
    }

    @Override
    public BitmapFont getEnergyNumFont() {
        return FontHelper.energyNumFontBlue;
    }

    @Override
    public void doCharSelectScreenSelectEffect() {
        CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);
    }

    @Override
    public ArrayList<CutscenePanel> getCutscenePanels() {
        ArrayList<CutscenePanel> panels = new ArrayList<>();
        panels.add(new CutscenePanel(VICTORY_1, "ATTACK_MAGIC_FAST_1"));
        panels.add(new CutscenePanel(VICTORY_2));
        panels.add(new CutscenePanel(VICTORY_3));
        return panels;
    }

    @Override
    public String getCustomModeCharacterButtonSoundKey() {
        return "ATTACK_MAGIC_FAST_1";
    }

    @Override
    public String getLocalizedCharacterName() {
        return getCharacterStrings().NAMES[0];
    }

    @Override
    public AbstractPlayer newInstance() {
        return new PtilopsisCharacter(this.name);
    }

    @Override
    public String getSpireHeartText() {
        return getCharacterStrings().TEXT[1];
    }

    @Override
    public Color getSlashAttackColor() {
        return Ptilopsis.PTILOPSIS_COLOR;
    }

    @Override
    public String getVampireText() {
        return Vampires.DESCRIPTIONS[1];
    }

    @Override
    public Color getCardRenderColor() {
        return Ptilopsis.PTILOPSIS_COLOR;
    }

    @Override
    public AbstractGameAction.AttackEffect[] getSpireHeartSlashEffect() {
        return new AbstractGameAction.AttackEffect[]{
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL,
                AbstractGameAction.AttackEffect.FIRE,
                AbstractGameAction.AttackEffect.SLASH_HEAVY,
                AbstractGameAction.AttackEffect.SLASH_DIAGONAL,
                AbstractGameAction.AttackEffect.FIRE,
                AbstractGameAction.AttackEffect.SLASH_HEAVY
        };
    }

    private static CharacterStrings getCharacterStrings() {
        return CardCrawlGame.languagePack.getCharacterString(CHARACTER_ID);
    }

    public static class Enums {
        @SpireEnum
        public static PlayerClass PTILOPSIS;

        @SpireEnum(name = "PTILOPSIS_COLOR")
        public static AbstractCard.CardColor PTILOPSIS_COLOR;
    }

    public static class LibraryEnums {
        @SpireEnum(name = "PTILOPSIS_COLOR")
        public static CardLibrary.LibraryType PTILOPSIS_COLOR;
    }
}
