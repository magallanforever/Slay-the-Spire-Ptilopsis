package Ptilopsis.threads;

import Ptilopsis.actions.GenerateDiscountedPtilopsisCardsAction;
import Ptilopsis.actions.MallocAction;
import Ptilopsis.actions.ManualMountAction;
import Ptilopsis.actions.SourcePrivateKeyAction;
import Ptilopsis.actions.ThreadCommandAction;
import Ptilopsis.cards.Hyperthreading;
import Ptilopsis.cards.Malloc;
import Ptilopsis.cards.BootWisdom;
import Ptilopsis.cards.GoldCardVariable;
import Ptilopsis.powers.FreePowerCardsPower;
import Ptilopsis.patches.FreePowerCardsPatch;
import basemod.BaseMod;
import basemod.abstracts.DynamicVariable;
import Ptilopsis.character.PtilopsisCharacter;
import Ptilopsis.powers.EnkephalinPower;
import Ptilopsis.powers.HyperthreadingPower;
import Ptilopsis.threads.effects.CardThreadFunction;
import Ptilopsis.threads.effects.OfflineThreadEffect;
import Ptilopsis.threads.effects.GuardThreadEffect;
import Ptilopsis.threads.effects.WakeRoutineThreadEffect;
import Ptilopsis.threads.effects.MedicalPulseThreadEffect;
import Ptilopsis.threads.effects.GradientDescentThreadEffect;
import Ptilopsis.threads.effects.CalcificationThreadEffect;
import Ptilopsis.ui.ThreadPreview;
import Ptilopsis.ui.ThreadUiText;
import basemod.helpers.CardPowerTip;
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
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.DrawPileToHandAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.EnergyManager;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.exordium.JawWorm;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
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
            testThreadCopies();
            testBootWisdom(player);
            testGoldCardVariables();
            testThreadPreviews(player);
            testManualMount(player);
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

    private static void testBootWisdom(AbstractPlayer player) throws Exception {
        player.powers.clear();
        player.orbs.clear();
        AbstractDungeon.actionManager.actions.clear();
        BootWisdom card = new BootWisdom();
        equal(3, card.cost, "Boot Wisdom costs 3");
        if (card.rarity != AbstractCard.CardRarity.UNCOMMON || card.type != AbstractCard.CardType.SKILL
                || card.exhaust || card.isInnate) {
            throw new AssertionError("Boot Wisdom must be an Uncommon non-Exhausting Skill");
        }
        card.use(player, null);
        List<AbstractGameAction> actions = AbstractDungeon.actionManager.actions;
        equal(3, actions.size(), "Boot Wisdom grants energy, fetches Powers, and grants free plays");
        if (!(actions.get(0) instanceof GainEnergyAction)
                || (Integer) ReflectionHacks.getPrivate(actions.get(0), GainEnergyAction.class, "energyGain") != 1
                || !(actions.get(1) instanceof DrawPileToHandAction) || actions.get(1).amount != 2
                || ReflectionHacks.getPrivate(actions.get(1), DrawPileToHandAction.class, "typeToCheck") != AbstractCard.CardType.POWER
                || !(actions.get(2) instanceof ApplyPowerAction) || actions.get(2).amount != 2) {
            throw new AssertionError("Boot Wisdom actions do not match the specified effects");
        }
        AbstractPower applied = ReflectionHacks.getPrivate(actions.get(2), ApplyPowerAction.class, "powerToApply");
        if (!(applied instanceof FreePowerCardsPower)) { throw new AssertionError("wrong free-play power"); }
        card.upgrade();
        card.upgrade();
        equal(3, card.cost, "upgrading keeps the cost");
        equal(1, card.timesUpgraded, "Boot Wisdom can only upgrade once");
        if (!card.isInnate || card.exhaust) { throw new AssertionError("upgrade adds only Innate"); }
        actions.clear();

        FreePowerCardsPower power = new QuietFreePower(player, 2);
        player.powers.add(power);
        OfferCard first = offerCard("power-one", 3);
        OfferCard second = offerCard("power-two", 2);
        OfferCard third = offerCard("power-three", 1);
        first.type = second.type = third.type = AbstractCard.CardType.POWER;
        if (!FreePowerCardsPatch.Postfix(false, first) || FreePowerCardsPatch.Postfix(false, card)) {
            throw new AssertionError("only Power cards become free");
        }
        power.onUseCard(card, null);
        equal(2, power.amount, "Skills cannot consume free Power charges");
        first.purgeOnUse = true;
        power.onUseCard(first, null);
        equal(2, power.amount, "purged thread replays do not consume charges");
        first.purgeOnUse = false;
        power.onUseCard(first, null);
        equal(1, power.amount, "first Power consumes one charge");
        power.onUseCard(second, null);
        equal(0, power.amount, "second Power consumes the last charge");
        if (!first.freeToPlay() || !second.freeToPlay() || FreePowerCardsPatch.Postfix(false, third)) {
            throw new AssertionError("the last Power must remain free during payment; the next must cost energy");
        }
        equal(3, first.costForTurn, "free play does not rewrite the card's cost");
        equal(2, second.costForTurn, "last free play does not rewrite the card's cost");
        if (!(actions.get(0) instanceof RemoveSpecificPowerAction)) {
            throw new AssertionError("using the last charge removes the buff");
        }
        actions.clear();
        player.powers.clear();
        power = new QuietFreePower(player, 2);
        player.powers.add(power);
        power.stackPower(2);
        equal(4, power.amount, "repeated Boot Wisdom stacks charges");
        OfferCard zeroCost = offerCard("free-power", 0);
        zeroCost.type = AbstractCard.CardType.POWER;
        power.onUseCard(zeroCost, null);
        equal(3, power.amount, "naturally free Powers consume charges like Swivel");
        power.atEndOfTurn(false);
        equal(3, power.amount, "enemy-turn callbacks do not expire a new player-turn buff");
        power.atEndOfTurn(true);
        equal(0, power.amount, "unused charges expire at player turn end");
        if (FreePowerCardsPatch.Postfix(false, third) || !(actions.get(0) instanceof RemoveSpecificPowerAction)) {
            throw new AssertionError("turn end must stop free plays and remove the buff");
        }
        if (!FreePowerCardsPatch.Postfix(true, third)) {
            throw new AssertionError("other sources of free play must be preserved");
        }
        player.powers.clear();
        actions.clear();
    }

    private static void testGoldCardVariables() throws Exception {
        DynamicVariable nativeDamage = BaseMod.cardDynamicVariableMap.get("D");
        GoldCardVariable.register();
        if (nativeDamage != BaseMod.cardDynamicVariableMap.get("D")) {
            throw new AssertionError("number colors must not replace other characters' variables");
        }
        OfferCard card = offerCard("numbers", 1);
        card.baseDamage = 8;
        card.damage = 12;
        card.baseBlock = 3;
        card.block = 5;
        card.baseMagicNumber = 2;
        card.magicNumber = 3;
        card.upgradedDamage = card.upgradedBlock = card.upgradedMagicNumber = true;
        int[] base = {8, 3, 2};
        int[] current = {12, 5, 3};
        String[] keys = {"Ptilopsis:D", "Ptilopsis:B", "Ptilopsis:M"};
        for (int i = 0; i < keys.length; i++) {
            DynamicVariable variable = BaseMod.cardDynamicVariableMap.get(keys[i]);
            equal(base[i], variable.baseValue(card), "gold variable preserves the base value");
            equal(current[i], variable.value(card), "gold variable preserves the calculated value");
            variable.setIsModified(card, true);
            if (!variable.isModified(card) || !variable.upgraded(card)
                    || !Settings.GOLD_COLOR.equals(variable.getNormalColor())
                    || !Settings.GOLD_COLOR.equals(variable.getUpgradedColor(card))
                    || !Settings.GOLD_COLOR.equals(variable.getIncreasedValueColor())
                    || !Settings.GOLD_COLOR.equals(variable.getDecreasedValueColor())) {
                throw new AssertionError("dynamic numbers must stay gold before and after changes");
            }
        }
    }

    private static class QuietFreePower extends FreePowerCardsPower {
        QuietFreePower(AbstractPlayer player, int amount) { super(player, amount); }
        @Override public void flash() { }
    }

    private static void testThreadPreviews(AbstractPlayer player) throws Exception {
        player.orbs.clear();
        player.powers.clear();
        Settings.GameLanguage previousLanguage = Settings.language;
        Map<String, CardStrings> strings = ReflectionHacks.getPrivateStatic(LocalizedStrings.class, "cards");
        Map<String, CardStrings> savedStrings = new HashMap<>(strings);
        int queued = AbstractDungeon.actionManager.cardQueue.size();
        try {
            for (Settings.GameLanguage language : new Settings.GameLanguage[] {
                    Settings.GameLanguage.ENG, Settings.GameLanguage.ZHS}) {
                Settings.language = language;
                boolean chinese = language == Settings.GameLanguage.ZHS;
                strings.putAll(new Gson().fromJson(Gdx.files.internal("PtilopsisResources/localization/"
                        + (chinese ? "ZHS" : "ENG") + "/cards.json").readString("UTF-8"),
                        new TypeToken<Map<String, CardStrings>>() { }.getType()));
                String none = chinese ? "无" : "None";
                String block = chinese ? "获得 3 点 格挡" : "Gain 3 Block";
                PowerTip guard = new ThreadPreview(new GuardThreadEffect("guard", 3, 1), false).makeTips(0).get(0);
                assertPhases(guard, chinese, block, block, chinese ? "抽 1 张牌" : "Draw 1 card");
                if (guard instanceof CardPowerTip) { throw new AssertionError("fixed effects use a compact text tip"); }
                assertPhases(new ThreadPreview(new GuardThreadEffect("guard+", 5, 2), false).makeTips(0).get(0),
                        chinese, chinese ? "获得 5 点 格挡" : "Gain 5 Block",
                        chinese ? "获得 5 点 格挡" : "Gain 5 Block", chinese ? "抽 2 张牌" : "Draw 2 card");
                PowerTip wake = new ThreadPreview(new WakeRoutineThreadEffect("wake", 1), false).makeTips(0).get(0);
                assertPhases(wake, chinese, none, chinese ? "回合开始" : "At turn start", "[E]");
                for (int amount : new int[] {4, 6}) {
                    assertPhases(new ThreadPreview(new GradientDescentThreadEffect("gradient", amount), false).makeTips(0).get(0),
                            chinese, none, chinese ? "给予随机敌人 " + amount : "Apply " + amount,
                            chinese ? "给予所有敌人 " + amount : "Apply " + amount);
                }
                assertPhases(new ThreadPreview(new MedicalPulseThreadEffect("pulse", 3, 6), false).makeTips(0).get(0),
                        chinese, none, chinese ? "造成 3 点伤害" : "Deal 3 damage",
                        chinese ? "造成 6 点伤害" : "Deal 6 damage");
                PowerTip calcium = new ThreadPreview(new CalcificationThreadEffect(true), false).makeTips(0).get(0);
                assertPhases(calcium, chinese, none, chinese ? "获得 5 点 格挡" : "Gain 5 Block",
                        chinese ? "2 倍" : "by 2");
                if (!calcium.body.contains(chinese ? "常驻" : "Passive")) {
                    throw new AssertionError("Calcification must retain its passive bonus note");
                }

                OfferCard first = offerCard("first", 1);
                OfferCard second = offerCard("second", 2);
                first.exhaust = true;
                ThreadPreview mounted = new ThreadPreview(new CardThreadFunction(java.util.Arrays.asList(first, second)), true);
                CardPowerTip firstTip = (CardPowerTip) mounted.makeTips(0).get(0);
                assertPhases(firstTip, chinese, none, chinese ? "依次免费打出 2 张牌" : "Play these 2 cards", none);
                if (!firstTip.body.contains(chinese ? "结算后移除线程" : "Remove this Thread")) {
                    throw new AssertionError("expiration remains visible separately from destruction effects");
                }
                CardPowerTip secondTip = (CardPowerTip) mounted.makeTips(1).get(0);
                if (!secondTip.card.cardID.equals("second") || !secondTip.body.contains("2/2")) {
                    throw new AssertionError("scroll must select the second card and show its position");
                }
                CardPowerTip wrapped = (CardPowerTip) mounted.makeTips(1).get(0);
                if (!wrapped.card.cardID.equals("first") || !wrapped.body.contains("1/2")) {
                    throw new AssertionError("card previews must wrap in mount order");
                }
                if (first.freeToPlayOnce || second.freeToPlayOnce) {
                    throw new AssertionError("hover must not change original card flags");
                }
                PowerTip offline = new ThreadPreview(new OfflineThreadEffect("offline",
                        java.util.Arrays.asList(first, second), 4), false).makeTips(0).get(0);
                assertPhases(offline, chinese, none, chinese ? "2 张牌" : "2 cards",
                        chinese ? "获得 4 点 格挡" : "Gain 4 Block");
                if (!(offline instanceof CardPowerTip) || offline.body.contains(chinese ? "结算后移除线程" : "Remove this Thread")) {
                    throw new AssertionError("Offline keeps its card view and does not expire after playback");
                }
                assertPhases(new PowerTip("empty", ThreadUiText.emptyMutableBody(1)), chinese, none, none, none);

                ThreadPreview dynamic = new ThreadPreview(new GuardThreadEffect("guard", 3, 1), false);
                DexterityPower dexterity = ThreadCombatTest.bare(DexterityPower.class);
                dexterity.ID = DexterityPower.POWER_ID;
                dexterity.owner = player;
                dexterity.amount = 2;
                player.powers.add(dexterity);
                assertPhases(dynamic.makeTips(0).get(0), chinese,
                        chinese ? "获得 5 点 格挡" : "Gain 5 Block",
                        chinese ? "获得 5 点 格挡" : "Gain 5 Block", chinese ? "抽 1" : "Draw 1");
                player.powers.clear();
                assertPhases(dynamic.makeTips(0).get(0), chinese, block, block, chinese ? "抽 1" : "Draw 1");
            }
            equal(queued, AbstractDungeon.actionManager.cardQueue.size(), "hover never queues a gameplay effect");
        } finally {
            Settings.language = previousLanguage;
            strings.clear();
            strings.putAll(savedStrings);
            player.powers.clear();
        }
    }

    private static void assertPhases(PowerTip tip, boolean chinese, String construct, String function, String destruct) {
        String[] lines = tip.body.replaceAll("#[bygrw]", "").split(" NL ");
        String[] labels = chinese ? new String[] {"构造：", "函数：", "析构："}
                : new String[] {"Construct: ", "Function: ", "Destruct: "};
        String[] expected = {construct, function, destruct};
        if (lines.length < 3) { throw new AssertionError("missing lifecycle effects: " + tip.body); }
        for (int i = 0; i < 3; i++) {
            if (!lines[i].startsWith(labels[i]) || !lines[i].contains(expected[i])) {
                throw new AssertionError("wrong phase " + i + ": " + tip.body);
            }
        }
        if (tip.body.matches("(?s).*![^!]+!.*") || tip.body.contains("ptilopsis:")) {
            throw new AssertionError("tooltip contains unresolved tokens: " + tip.body);
        }
    }

    private static void testThreadCopies() throws Exception {
        OfferCard original = offerCard("thread-copy", 2);
        original.exhaust = true;
        original.baseDamage = 7;
        for (ThreadFunction function : new ThreadFunction[] {
                new CardThreadFunction(original),
                new OfflineThreadEffect("offline", Collections.singletonList(original), 3)}) {
            AbstractCard preview = function.makePreviewCards().get(0);
            if (!preview.exhaust || preview == original) {
                throw new AssertionError("all thread templates preserve dynamic Exhaust in independent copies");
            }
            preview.baseDamage = 999;
            preview.exhaust = false;
            AbstractCard fresh = function.makePreviewCards().get(0);
            equal(7, fresh.baseDamage, "preview cannot alter the stored template");
            if (!fresh.exhaust || !original.exhaust) {
                throw new AssertionError("preview cannot alter stored or original Exhaust");
            }
        }
        AbstractCard replay = ThreadCardPlayback.copyTemplate(original);
        if (!replay.exhaust) {
            throw new AssertionError("replay uses the same template copy rule");
        }
    }

    private static void testManualMount(AbstractPlayer player) throws Exception {
        HandCardSelectScreen previousScreen = AbstractDungeon.handCardSelectScreen;
        CardGroup previousHand = player.hand;
        MockHandScreen screen = ThreadCombatTest.bare(MockHandScreen.class);
        screen.selectedCards = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
        AbstractDungeon.handCardSelectScreen = screen;
        player.hand = new CardGroup(CardGroup.CardGroupType.HAND) {
            @Override public void refreshHandLayout() { }
        };
        player.energy = new EnergyManager(3);
        AbstractDungeon.isScreenUp = false;
        player.endTurnQueued = player.isEndingTurn = false;
        try {
            ThreadOrb orb = emptyMountTarget(player);
            OfferCard card = offerCard("manual-mount", 2);
            card.hb = new Hitbox(10, 10);
            player.hand.addToTop(card);
            EnergyPanel.totalCount = 1;
            ManualMountAction action = new ManualMountAction(player, orb);
            action.update();
            equal(1, screen.opens, "manual mount opens one selection");
            action.update();
            equal(1, screen.opens, "open selection waits without reopening");
            screen.choose(player, card);
            action.update();
            action.update();
            equal(0, EnergyPanel.totalCount, "confirmed mount spends energy exactly once");
            equal(1, player.hand.size(), "original card returns exactly once");
            if (orb.isEmpty() || !action.isDone || !screen.wereCardsRetrieved) {
                throw new AssertionError("confirmed selection must finish and mount");
            }

            orb = emptyMountTarget(player);
            EnergyPanel.totalCount = 1;
            action = new ManualMountAction(player, orb);
            action.update();
            screen.choose(player, null);
            action.update();
            equal(1, EnergyPanel.totalCount, "skipping selection is free");
            if (!orb.isEmpty() || !action.isDone) { throw new AssertionError("skip must finish without mounting"); }

            action = new ManualMountAction(player, orb);
            action.update();
            screen.choose(player, card);
            ThreadNetwork.forPlayer(player).destructThread(orb);
            // No ThreadOrb remains to update; the queued action must still return the card.
            action.update();
            equal(1, player.hand.size(), "target removal still returns the selected card");
            equal(1, EnergyPanel.totalCount, "removed target cannot charge energy");
            if (!action.isDone) { throw new AssertionError("removed target must not strand the action"); }

            orb = emptyMountTarget(player);
            action = new ManualMountAction(player, orb);
            action.update();
            screen.choose(player, card);
            EnergyPanel.totalCount = 0;
            action.update();
            equal(1, player.hand.size(), "energy loss still returns the selected card");
            if (!orb.isEmpty()) { throw new AssertionError("mount must recheck energy on confirmation"); }
            int opens = screen.opens;
            action = new ManualMountAction(player, orb);
            action.update();
            equal(opens, screen.opens, "zero energy cannot open a selection");
            if (!action.isDone) { throw new AssertionError("unavailable mount must terminate"); }

            EnergyPanel.totalCount = 1;
            action = new ManualMountAction(player, orb);
            action.update();
            screen.choose(player, card);
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            action.update();
            if (!action.isDone || !screen.wereCardsRetrieved || !screen.selectedCards.isEmpty()) {
                throw new AssertionError("ended combat must discard the abandoned selection");
            }
            equal(1, EnergyPanel.totalCount, "ended combat cannot charge energy");
        } finally {
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMBAT;
            AbstractDungeon.handCardSelectScreen = previousScreen;
            AbstractDungeon.isScreenUp = false;
            player.hand = previousHand;
        }
    }

    private static ThreadOrb emptyMountTarget(AbstractPlayer player) {
        player.maxOrbs = 1;
        player.orbs.clear();
        ThreadOrb orb = new ThreadOrb(true);
        player.orbs.add(orb);
        return orb;
    }

    private static class MockHandScreen extends HandCardSelectScreen {
        int opens;
        @Override public void open(String prompt, int count, boolean anyNumber, boolean canPickZero,
                boolean forUpgrade, boolean forTransform, boolean upTo) {
            opens++;
            this.selectedCards.clear();
            this.wereCardsRetrieved = false;
            AbstractDungeon.isScreenUp = true;
        }
        void choose(AbstractPlayer player, AbstractCard card) {
            if (card != null) {
                player.hand.removeCard(card);
                this.selectedCards.addToTop(card);
            }
            AbstractDungeon.isScreenUp = false;
        }
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
