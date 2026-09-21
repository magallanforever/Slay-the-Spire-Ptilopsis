package Ptilopsis.threads;

import Ptilopsis.actions.GenerateDiscountedPtilopsisCardsAction;
import Ptilopsis.actions.MallocAction;
import Ptilopsis.actions.SourcePrivateKeyAction;
import Ptilopsis.actions.ThreadCommandAction;
import Ptilopsis.cards.Hyperthreading;
import Ptilopsis.cards.Malloc;
import Ptilopsis.character.PtilopsisCharacter;
import Ptilopsis.powers.EnkephalinPower;
import Ptilopsis.powers.HyperthreadingPower;
import Ptilopsis.threads.effects.CardThreadFunction;
import basemod.ReflectionHacks;
import basemod.abstracts.AbstractCardModifier;
import basemod.cardmods.ExhaustMod;
import basemod.helpers.CardModifierManager;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.CardModifierPatches;
import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.exordium.JawWorm;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

final class CardInteractionTest {
    static void run(AbstractPlayer player) throws Exception {
        AbstractPower.atlas.addRegion("48/loop", new TextureRegion(ImageMaster.WHITE_SQUARE_IMG));
        AbstractPower.atlas.addRegion("128/loop", new TextureRegion(ImageMaster.WHITE_SQUARE_IMG));
        AbstractPower.atlas.addRegion("48/draw", new TextureRegion(ImageMaster.WHITE_SQUARE_IMG));
        AbstractPower.atlas.addRegion("128/draw", new TextureRegion(ImageMaster.WHITE_SQUARE_IMG));
        ImageMaster.WHITE_RING = AbstractPower.atlas.findRegion("128/loop");
        List<AbstractMonster> monsters = new ArrayList<>(AbstractDungeon.getMonsters().monsters);
        AbstractDungeon.getMonsters().monsters.clear();
        QuietMonster enemy = ThreadCombatTest.bare(QuietMonster.class);
        enemy.powers = new ArrayList<>();
        enemy.currentHealth = enemy.maxHealth = 30;
        AbstractDungeon.getMonsters().monsters.add(enemy);
        try {
            testStartingDeck();
            testEnkephalin(player);
            testHyperthreading(player, false, true);
            testHyperthreading(player, true, false);
            testHyperthreading(player, false, false);
            testHyperthreading(player, true, true);
            testHyperthreading(player, false, true, false, true);
            testQuickIteration(player);
            testSourcePrivateKey(player);
            testMalloc(player, 0, -1, false, false, false, 0, 0);
            testMalloc(player, 0, -1, true, false, false, 0, 0);
            testMalloc(player, 1, -1, true, false, false, 1, 1);
            testMalloc(player, 3, -1, false, false, false, 3, 0);
            testMalloc(player, 5, -1, true, false, false, 5, 1);
            testMalloc(player, 3, 3, true, true, false, 3, 4);
            testMalloc(player, 5, 2, true, true, false, 2, 6);
            testMalloc(player, 3, 0, true, true, false, 0, 3);
            testMalloc(player, 0, 0, true, false, true, 2, 0);
            testMalloc(player, 1, 1, true, false, true, 3, 1);
        } finally {
            AbstractDungeon.getMonsters().monsters.clear();
            AbstractDungeon.getMonsters().monsters.addAll(monsters);
        }
        player.powers.clear();
        player.orbs.clear();
        player.relics.clear();
        EnergyPanel.totalCount = 0;
        AbstractDungeon.actionManager = new GameActionManager();
    }

    private static void testStartingDeck() throws Exception {
        PtilopsisCharacter character = ThreadCombatTest.bare(PtilopsisCharacter.class);
        List<String> deck = character.getStartingDeck();
        equal(9, deck.size(), "starting deck size");
        equal(0, Collections.frequency(deck, com.megacrit.cardcrawl.cards.blue.Capacitor.ID),
                "Capacitor must not be a starting card");
    }

    private static void testEnkephalin(AbstractPlayer player) throws Exception {
        AbstractDungeon.actionManager = new GameActionManager();
        QuietEnkephalinPower power = new QuietEnkephalinPower(player, 1);
        AbstractCard card = offerCard("enkephalin-probe", 1);

        power.onUseCard(card, null);
        power.onUseCard(card, null);
        equal(2, AbstractDungeon.actionManager.actions.size(), "one stack triggers once");

        power.stackPower(2);
        power.onUseCard(card, null);
        power.onUseCard(card, null);
        power.onUseCard(card, null);
        equal(6, AbstractDungeon.actionManager.actions.size(), "new stacks add this turn's triggers");
        assertMountAndDrawPairs(AbstractDungeon.actionManager.actions);

        card.isInAutoplay = true;
        power.onUseCard(card, null);
        card.isInAutoplay = false;
        card.purgeOnUse = true;
        power.onUseCard(card, null);
        equal(6, AbstractDungeon.actionManager.actions.size(), "automatic plays do not consume triggers");

        AbstractDungeon.actionManager.actions.clear();
        card.purgeOnUse = false;
        power.atStartOfTurn();
        power.onUseCard(card, null);
        power.onUseCard(card, null);
        power.onUseCard(card, null);
        power.onUseCard(card, null);
        equal(6, AbstractDungeon.actionManager.actions.size(), "stacked power resets each turn");
        assertMountAndDrawPairs(AbstractDungeon.actionManager.actions);
    }

    private static void assertMountAndDrawPairs(List<AbstractGameAction> actions) {
        for (int i = 0; i < actions.size(); i += 2) {
            if (!(actions.get(i) instanceof ThreadCommandAction)
                    || !(actions.get(i + 1) instanceof DrawCardAction)) {
                throw new AssertionError("Enkephalin must queue one mount and one draw per trigger");
            }
        }
    }

    private static void testHyperthreading(AbstractPlayer player, boolean... upgrades) throws Exception {
        player.powers.clear();
        player.orbs.clear();
        player.hb = new Hitbox(10, 10);
        AbstractDungeon.actionManager = new GameActionManager();
        int normal = 0;
        int upgraded = 0;
        for (boolean upgrade : upgrades) {
            Hyperthreading card = ThreadCombatTest.bare(Hyperthreading.class);
            card.magicNumber = 1;
            card.upgraded = upgrade;
            card.use(player, null);
            if (upgrade) { upgraded++; } else { normal++; }
        }
        // Queue both cards before applying either power, as copied plays can do.
        while (!AbstractDungeon.actionManager.actions.isEmpty()) {
            AbstractGameAction action = AbstractDungeon.actionManager.actions.remove(0);
            if (!(action instanceof ApplyPowerAction)) {
                throw new AssertionError("Hyperthreading must use native power application");
            }
            for (int frame = 0; !action.isDone && frame < 100; frame++) {
                action.update();
            }
            if (!action.isDone) { throw new AssertionError("power application did not finish"); }
        }
        equal(normal, stacks(player, HyperthreadingPower.POWER_ID), "normal stacks");
        equal(upgraded, stacks(player, HyperthreadingPower.UPGRADED_POWER_ID), "upgraded stacks");

        CountingFunction left = new CountingFunction();
        CountingFunction right = new CountingFunction();
        ThreadOrb first = new ThreadOrb(false, left);
        ThreadOrb last = new ThreadOrb(false, right);
        player.orbs.add(first);
        player.orbs.add(last);
        triggerPowers(player, true);
        equal(normal + upgraded, left.hits, "left edge triggers once per stack");
        equal(upgraded, right.hits, "only upgraded stacks trigger the right edge");
        triggerPowers(player, false);
        equal(normal + upgraded, left.hits, "enemy turn must not trigger Hyperthreading");
        player.orbs.remove(last);
        left.hits = 0;
        triggerPowers(player, true);
        equal(normal + 2 * upgraded, left.hits, "a single thread receives both edge effects");
        if (first.isEmpty()) { throw new AssertionError("Hyperthreading must not remove its thread"); }
        player.orbs.clear();
        triggerPowers(player, true);
        equal(normal + 2 * upgraded, left.hits, "empty queue has no effects");
    }

    private static int stacks(AbstractPlayer player, String id) {
        AbstractPower power = player.getPower(id);
        return power == null ? 0 : power.amount;
    }

    private static void triggerPowers(AbstractPlayer player, boolean playerTurn) {
        for (AbstractPower power : player.powers) {
            power.atEndOfTurnPreEndTurnCards(playerTurn);
        }
    }

    private static void testQuickIteration(AbstractPlayer player) throws Exception {
        Map<String, UIStrings> ui = ReflectionHacks.getPrivateStatic(LocalizedStrings.class, "ui");
        ui.putAll(new Gson().fromJson(Gdx.files.classpath("localization/basemod/eng/cardMods.json").readString("UTF-8"),
                new TypeToken<Map<String, UIStrings>>() { }.getType()));
        SpireField<ArrayList<AbstractCardModifier>> originalField = CardModifierPatches.CardModifierFields.cardModifiers;
        CardLibrary.LibraryType originalColor = PtilopsisCharacter.LibraryEnums.PTILOPSIS_COLOR;
        Map<String, AbstractCard> originalCards = new HashMap<>(CardLibrary.cards);
        // ModTheSpire's field injection is absent in the headless test process.
        CardModifierPatches.CardModifierFields.cardModifiers = new SpireField<ArrayList<AbstractCardModifier>>(ArrayList::new) {
            private final Map<Object, ArrayList<AbstractCardModifier>> values = new IdentityHashMap<>();
            @Override public ArrayList<AbstractCardModifier> get(Object card) {
                return values.computeIfAbsent(card, ignored -> new ArrayList<>());
            }
            @Override public void set(Object card, ArrayList<AbstractCardModifier> value) { values.put(card, value); }
        };
        try {
            PtilopsisCharacter.LibraryEnums.PTILOPSIS_COLOR = CardLibrary.LibraryType.RED;
            for (boolean alreadyExhausts : new boolean[] {false, true}) {
                CardLibrary.cards.clear();
                CopyCard template = copyCard(alreadyExhausts);
                CardLibrary.cards.put(template.cardID, template);
                AbstractDungeon.actionManager = new GameActionManager();
                new GenerateDiscountedPtilopsisCardsAction(player, 2).update();
                equal(2, AbstractDungeon.actionManager.actions.size(), "requested cards are generated");
                for (AbstractGameAction action : AbstractDungeon.actionManager.actions) {
                    if (!(action instanceof MakeTempCardInHandAction)) {
                        throw new AssertionError("generated cards must use native hand insertion");
                    }
                    Method makeNewCard = MakeTempCardInHandAction.class.getDeclaredMethod("makeNewCard");
                    makeNewCard.setAccessible(true);
                    AbstractCard card = (AbstractCard) makeNewCard.invoke(action);
                    assertDiscountedExhaust(card, alreadyExhausts);
                    assertDiscountedExhaust(card.makeStatEquivalentCopy(), alreadyExhausts);
                    CardThreadFunction function = new CardThreadFunction(card);
                    if (!function.expiresAtEndOfTurn()) {
                        throw new AssertionError("generated Exhaust card must expire its mounted thread");
                    }
                    assertDiscountedExhaust(function.makePreviewCards().get(0), alreadyExhausts);
                }
                equal(2, template.cost, "card library cost must remain unchanged");
                if (template.exhaust != alreadyExhausts || !CardModifierManager.modifiers(template).isEmpty()) {
                    throw new AssertionError("generation modified the library template");
                }
            }
        } finally {
            CardModifierPatches.CardModifierFields.cardModifiers = originalField;
            PtilopsisCharacter.LibraryEnums.PTILOPSIS_COLOR = originalColor;
            CardLibrary.cards.clear();
            CardLibrary.cards.putAll(originalCards);
        }
    }

    private static void testSourcePrivateKey(AbstractPlayer player) throws Exception {
        Map<String, PotionStrings> potions = ReflectionHacks.getPrivateStatic(LocalizedStrings.class, "potions");
        potions.putAll(new Gson().fromJson(
                Gdx.files.internal("PtilopsisResources/localization/ENG/potions.json").readString("UTF-8"),
                new TypeToken<Map<String, PotionStrings>>() { }.getType()));
        Map<String, AbstractCard> originalCards = new HashMap<>(CardLibrary.cards);
        CardRewardScreen originalScreen = AbstractDungeon.cardRewardScreen;
        CardGroup originalDrawPile = player.drawPile;
        CardGroup originalDiscardPile = player.discardPile;
        boolean originalScreenState = AbstractDungeon.isScreenUp;
        try {
            CardLibrary.cards.clear();
            int cost = 1;
            for (String id : new String[] {"Scrawl", "Wraith Form", "Well Laid Plans",
                    "Demon Form", "DevaForm", "Echo Form"}) {
                CardLibrary.cards.put(id, offerCard(id, cost++));
            }
            player.drawPile = new CardGroup(CardGroup.CardGroupType.DRAW_PILE);
            player.discardPile = new CardGroup(CardGroup.CardGroupType.DISCARD_PILE);
            player.currentHealth = player.maxHealth = 50;
            MockSourceRewardScreen screen = ThreadCombatTest.bare(MockSourceRewardScreen.class);
            AbstractDungeon.cardRewardScreen = screen;
            AbstractDungeon.isScreenUp = false;
            AbstractDungeon.actionManager = new GameActionManager();

            SourcePrivateKeyAction action = new SourcePrivateKeyAction(1);
            action.update();
            equal(3, screen.rewardGroup.size(), "Source Private Key candidates");
            for (AbstractCard candidate : screen.rewardGroup) {
                if (candidate.costForTurn != candidate.cost || candidate.isCostModifiedForTurn) {
                    throw new AssertionError("Source Private Key must display each candidate's normal cost");
                }
            }

            screen.choose(0);
            action.update();
            equal(1, AbstractDungeon.actionManager.actions.size(), "selected card enters the hand");
            AbstractGameAction queued = AbstractDungeon.actionManager.actions.get(0);
            if (!(queued instanceof MakeTempCardInHandAction)) {
                throw new AssertionError("Source Private Key must use native hand insertion");
            }
            Method makeNewCard = MakeTempCardInHandAction.class.getDeclaredMethod("makeNewCard");
            makeNewCard.setAccessible(true);
            AbstractCard reward = (AbstractCard) makeNewCard.invoke(queued);
            equal(0, reward.costForTurn, "selected card costs zero this turn");
        } finally {
            CardLibrary.cards.clear();
            CardLibrary.cards.putAll(originalCards);
            AbstractDungeon.cardRewardScreen = originalScreen;
            AbstractDungeon.isScreenUp = originalScreenState;
            player.drawPile = originalDrawPile;
            player.discardPile = originalDiscardPile;
        }
    }

    private static void testMalloc(AbstractPlayer player, int energy, int energyOnUse, boolean upgraded,
            boolean free, boolean chemicalX, int expectedThreads, int expectedEnergy) throws Exception {
        player.powers.clear();
        player.orbs.clear();
        player.relics.clear();
        player.maxOrbs = 3;
        player.energy = new EnergyManager(3);
        EnergyPanel.totalCount = energy;
        for (int i = 0; i < player.maxOrbs; i++) {
            player.orbs.add(new EmptyOrbSlot());
        }
        QuietChemicalX relic = ThreadCombatTest.bare(QuietChemicalX.class);
        ReflectionHacks.setPrivate(relic, AbstractRelic.class, "relicId", ChemicalX.ID);
        if (chemicalX) { player.relics.add(relic); }
        AbstractDungeon.actionManager = new GameActionManager();
        Malloc card = ThreadCombatTest.bare(Malloc.class);
        card.energyOnUse = energyOnUse;
        card.upgraded = upgraded;
        card.freeToPlayOnce = free;
        card.use(player, null);
        equal(1, AbstractDungeon.actionManager.actions.size(), "malloc queues one X-cost action");
        AbstractGameAction use = AbstractDungeon.actionManager.actions.remove(0);
        if (!(use instanceof MallocAction)) { throw new AssertionError("wrong malloc action"); }
        use.update();
        int queued = AbstractDungeon.actionManager.actions.size();
        use.update();
        equal(queued, AbstractDungeon.actionManager.actions.size(), "malloc action cannot resolve twice");
        int frames = 1000;
        while (!AbstractDungeon.actionManager.actions.isEmpty() && frames > 0) {
            AbstractGameAction action = AbstractDungeon.actionManager.actions.remove(0);
            while (!action.isDone && frames-- > 0) { action.update(); }
        }
        if (frames <= 0) { throw new AssertionError("malloc native action queue did not finish"); }
        equal(expectedThreads, AbstractDungeon.actionManager.orbsChanneledThisCombat.size(), "malloc generated count");
        equal(Math.min(expectedThreads, 3), ThreadNetwork.threadCount(player), "malloc respects native capacity");
        equal(3, player.orbs.size(), "malloc must not increase slot capacity");
        equal(expectedEnergy, EnergyPanel.totalCount, "malloc energy cost and refund");
        equal(chemicalX ? 1 : 0, relic.flashes, "Chemical X triggers once");
        for (AbstractOrb orb : player.orbs) {
            if (orb instanceof ThreadOrb && (!((ThreadOrb) orb).isMutable() || !((ThreadOrb) orb).isEmpty())) {
                throw new AssertionError("malloc must create empty mutable threads");
            }
        }
    }

    private static void assertDiscountedExhaust(AbstractCard card, boolean inherent) {
        equal(0, card.cost, "generated combat cost");
        equal(0, card.costForTurn, "generated turn cost");
        if (!card.exhaust) { throw new AssertionError("Exhaust was lost during copying"); }
        equal(inherent ? 0 : 1, CardModifierManager.getModifiers(card, ExhaustMod.ID).size(), "no duplicate Exhaust modifier");
        String description = CardModifierManager.onCreateDescription(card, card.rawDescription);
        equal(1, description.split("Exhaust", -1).length - 1, "description displays Exhaust once");
    }

    private static CopyCard copyCard(boolean inherent) throws InstantiationException {
        CopyCard card = ThreadCombatTest.bare(CopyCard.class);
        card.inherentExhaust = card.exhaust = inherent;
        card.cardID = card.name = "copy-probe";
        card.rawDescription = inherent ? "Gain 1 Block. NL Exhaust." : "Gain 1 Block.";
        card.cost = card.costForTurn = 2;
        card.color = AbstractCard.CardColor.RED;
        card.type = AbstractCard.CardType.SKILL;
        card.rarity = AbstractCard.CardRarity.COMMON;
        card.tags = new ArrayList<>();
        return card;
    }

    private static OfferCard offerCard(String id, int cost) throws InstantiationException {
        OfferCard card = ThreadCombatTest.bare(OfferCard.class);
        card.cardID = card.name = id;
        card.rawDescription = "";
        card.cost = card.costForTurn = cost;
        card.color = AbstractCard.CardColor.RED;
        card.type = AbstractCard.CardType.SKILL;
        card.rarity = AbstractCard.CardRarity.COMMON;
        card.target = AbstractCard.CardTarget.SELF;
        card.tags = new ArrayList<>();
        card.uuid = UUID.randomUUID();
        return card;
    }

    private static class CopyCard extends AbstractCard {
        boolean inherentExhaust;
        private CopyCard() { super("probe", "probe", (String) null, 2, "", CardType.SKILL, CardColor.RED, CardRarity.COMMON, CardTarget.SELF); }
        public void use(AbstractPlayer player, AbstractMonster monster) { }
        public void upgrade() { }
        @Override public void initializeDescription() { }
        public AbstractCard makeCopy() {
            try { return copyCard(this.inherentExhaust); }
            catch (InstantiationException error) { throw new AssertionError(error); }
        }
        @Override public AbstractCard makeStatEquivalentCopy() {
            AbstractCard copy = super.makeStatEquivalentCopy();
            // Execute BaseMod's real copy hook explicitly, without runtime patch injection.
            CardModifierPatches.CardModifierStatEquivalentCopyModifiers.Insert(this, copy);
            return copy;
        }
    }

    private static class OfferCard extends AbstractCard {
        private OfferCard() { super("probe", "probe", (String) null, 1, "", CardType.SKILL, CardColor.RED, CardRarity.COMMON, CardTarget.SELF); }
        public void use(AbstractPlayer player, AbstractMonster monster) { }
        public void upgrade() { }
        @Override public void initializeDescription() { }
        public AbstractCard makeCopy() {
            try { return offerCard(this.cardID, this.cost); }
            catch (InstantiationException error) { throw new AssertionError(error); }
        }
    }

    private static class QuietEnkephalinPower extends EnkephalinPower {
        private QuietEnkephalinPower(AbstractPlayer owner, int amount) { super(owner, amount); }
        @Override public void flash() { }
    }

    private static class MockSourceRewardScreen extends CardRewardScreen {
        @Override public void customCombatOpen(ArrayList<AbstractCard> choices, String prompt, boolean skippable) {
            if (skippable) { throw new AssertionError("Source Private Key selection must not be skippable"); }
            this.rewardGroup = choices;
            this.discoveryCard = null;
            AbstractDungeon.isScreenUp = true;
        }
        void choose(int index) {
            this.discoveryCard = this.rewardGroup.get(index);
            AbstractDungeon.isScreenUp = false;
        }
    }

    private static class CountingFunction implements ThreadFunction {
        int hits;
        public String getDisplayName() { return "counter"; }
        public List<AbstractCard> makePreviewCards() { return Collections.emptyList(); }
        public void triggerDestructEffect(AbstractPlayer player) { hits++; }
    }

    private static class QuietMonster extends JawWorm {
        private QuietMonster() { super(0, 0); }
        @Override public void applyPowers() { }
    }

    private static class QuietChemicalX extends ChemicalX {
        int flashes;
        @Override public void flash() { flashes++; }
    }

    private static void equal(int expected, int actual, String label) {
        if (expected != actual) { throw new AssertionError(label + ": expected " + expected + ", got " + actual); }
    }
}
