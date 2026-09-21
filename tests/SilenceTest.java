package Ptilopsis.threads;

import Ptilopsis.actions.SilenceDraftAction;
import Ptilopsis.cards.Silence;
import Ptilopsis.character.PtilopsisCharacter;
import Ptilopsis.powers.SilencePower;
import Ptilopsis.threads.effects.CardThreadFunction;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

final class SilenceTest {
    static void run(AbstractPlayer player) throws Exception {
        Map<String, CardStrings> cardStrings = ReflectionHacks.getPrivateStatic(LocalizedStrings.class, "cards");
        cardStrings.putAll(new Gson().fromJson(Gdx.files.internal("PtilopsisResources/localization/ENG/cards.json").readString("UTF-8"),
                new TypeToken<Map<String, CardStrings>>() { }.getType()));
        Map<String, PowerStrings> powerStrings = ReflectionHacks.getPrivateStatic(LocalizedStrings.class, "powers");
        powerStrings.putAll(new Gson().fromJson(Gdx.files.internal("PtilopsisResources/localization/ENG/powers.json").readString("UTF-8"),
                new TypeToken<Map<String, PowerStrings>>() { }.getType()));
        AbstractPower.atlas = new TextureAtlas();
        PtilopsisCharacter.Enums.PTILOPSIS_COLOR = AbstractCard.CardColor.RED;
        AbstractDungeon.actionManager = new GameActionManager();
        AbstractDungeon.cardRandomRng = new Random(17L);
        AbstractDungeon.srcCommonCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        AbstractDungeon.srcUncommonCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        AbstractDungeon.srcRareCardPool = new CardGroup(CardGroup.CardGroupType.CARD_POOL);
        TestCard a = card("a");
        TestCard b = card("b");
        TestCard c = card("c");
        TestCard healing = card("healing");
        healing.tags.add(AbstractCard.CardTags.HEALING);
        TestCard basic = card("basic");
        basic.rarity = AbstractCard.CardRarity.BASIC;
        TestCard otherClass = card("otherClass");
        otherClass.color = AbstractCard.CardColor.GREEN;
        AbstractDungeon.srcCommonCardPool.group.addAll(Arrays.asList(a, b, c, healing, basic, otherClass, a));
        MockRewardScreen screen = ThreadCombatTest.bare(MockRewardScreen.class);
        AbstractDungeon.cardRewardScreen = screen;
        AbstractDungeon.isScreenUp = false;
        player.orbs.clear();
        for (int i = 0; i < 3; i++) {
            player.orbs.add(new EmptyOrbSlot());
        }
        ThreadNetwork network = ThreadNetwork.forPlayer(player);
        network.installNewMutable(new CardThreadFunction(a));
        ThreadOrb existing = (ThreadOrb) player.orbs.get(0);
        network.createEmptyMutableSlot();
        ThreadOrb empty = (ThreadOrb) player.orbs.get(1);

        SilenceDraftAction action = new SilenceDraftAction(player);
        action.update();
        assertChoices(screen);
        equal(2, ThreadNetwork.threadCount(player), "no new thread before the first choice");
        String first = screen.rewardGroup.get(0).cardID;
        screen.choose(0);
        action.update();
        assertChoices(screen);
        equal(2, screen.openCount, "second selection opens separately");
        equal(2, ThreadNetwork.threadCount(player), "no partial thread after only one choice");
        String second = screen.rewardGroup.get(1).cardID;
        screen.choose(1);
        action.update();
        equal(3, ThreadNetwork.threadCount(player), "both cards create exactly one new thread");
        if (!action.isDone || player.orbs.get(0) != existing || player.orbs.get(1) != empty || !empty.isEmpty()) {
            throw new AssertionError("Silence changed an existing thread instead of creating one");
        }
        ThreadOrb result = (ThreadOrb) player.orbs.get(2);
        java.lang.reflect.Field function = ThreadOrb.class.getDeclaredField("function");
        function.setAccessible(true);
        CardThreadFunction mounted = (CardThreadFunction) function.get(result);
        List<AbstractCard> previews = mounted.makePreviewCards();
        if (previews.size() != 2 || !previews.get(0).cardID.equals(first) || !previews.get(1).cardID.equals(second)) {
            throw new AssertionError("mounted cards must keep selection order");
        }
        equal(0, player.hand.size(), "chosen cards are mounted, not added to hand");
        if (mounted.expiresAtEndOfTurn()) {
            throw new AssertionError("ordinary two-card function must persist");
        }
        previews.get(0).baseDamage = 999;
        equal(0, mounted.makePreviewCards().get(0).baseDamage, "previews cannot mutate stored cards");
        b.exhaust = true;
        if (!new CardThreadFunction(Arrays.asList(a, b)).expiresAtEndOfTurn()) {
            throw new AssertionError("a dynamically Exhausting card must expire the whole thread");
        }
        b.exhaust = false;
        b.type = AbstractCard.CardType.POWER;
        if (!new CardThreadFunction(Arrays.asList(b, a)).expiresAtEndOfTurn()) {
            throw new AssertionError("a Power must expire the whole thread");
        }
        AbstractDungeon.srcCommonCardPool.group.clear();
        SilenceDraftAction noChoices = new SilenceDraftAction(player);
        noChoices.update();
        if (!noChoices.isDone || AbstractDungeon.isScreenUp) {
            throw new AssertionError("empty pool must terminate without opening a broken screen");
        }

        Silence card = ThreadCombatTest.bare(Silence.class);
        card.use(player, null);
        equal(1, AbstractDungeon.actionManager.actions.size(), "base Silence only applies its power");
        AbstractDungeon.actionManager.actions.clear();
        card.upgraded = true;
        card.use(player, null);
        equal(2, AbstractDungeon.actionManager.actions.size(), "upgraded Silence applies power and triggers once");
        if (!(AbstractDungeon.actionManager.actions.get(0) instanceof ApplyPowerAction)
                || !(AbstractDungeon.actionManager.actions.get(1) instanceof SilenceDraftAction)) {
            throw new AssertionError("immediate trigger must follow applying the Power");
        }
        AbstractDungeon.actionManager.actions.clear();
        SilentPower power = ThreadCombatTest.bare(SilentPower.class);
        power.owner = player;
        power.amount = 2;
        power.atStartOfTurn();
        equal(2, AbstractDungeon.actionManager.actions.size(), "two stacks produce two complete selection actions");
        AbstractDungeon.actionManager.actions.clear();
        player.orbs.clear();
    }

    private static void assertChoices(MockRewardScreen screen) {
        equal(3, screen.rewardGroup.size(), "three candidates");
        HashSet<String> ids = new HashSet<>();
        for (AbstractCard card : screen.rewardGroup) {
            if (!Arrays.asList("a", "b", "c").contains(card.cardID)) {
                throw new AssertionError("invalid in-combat candidate: " + card.cardID);
            }
            ids.add(card.cardID);
        }
        equal(3, ids.size(), "offers must not contain duplicate IDs");
    }

    private static TestCard card(String id) throws InstantiationException {
        TestCard card = ThreadCombatTest.bare(TestCard.class);
        card.cardID = card.name = id;
        card.color = AbstractCard.CardColor.RED;
        card.rarity = AbstractCard.CardRarity.COMMON;
        card.type = AbstractCard.CardType.SKILL;
        card.tags = new ArrayList<>();
        return card;
    }

    private static class TestCard extends AbstractCard {
        private TestCard() {
            super("probe", "probe", (String) null, 0, "", CardType.SKILL, CardColor.RED, CardRarity.COMMON, CardTarget.SELF);
        }
        public void use(AbstractPlayer player, AbstractMonster monster) { }
        public void upgrade() { }
        public AbstractCard makeCopy() {
            try {
                TestCard copy = card(this.cardID);
                copy.type = this.type;
                return copy;
            } catch (InstantiationException error) {
                throw new AssertionError(error);
            }
        }
    }

    private static class MockRewardScreen extends CardRewardScreen {
        int openCount;
        @Override public void customCombatOpen(ArrayList<AbstractCard> choices, String prompt, boolean skippable) {
            if (skippable) { throw new AssertionError("Silence selection must not be skippable"); }
            this.openCount++;
            this.rewardGroup = choices;
            this.discoveryCard = null;
            AbstractDungeon.isScreenUp = true;
        }
        void choose(int index) {
            this.discoveryCard = this.rewardGroup.get(index);
            AbstractDungeon.isScreenUp = false;
        }
    }

    private static class SilentPower extends SilencePower {
        private SilentPower() { super(null, 0); }
        @Override public void flash() { }
    }

    private static void equal(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + ": expected " + expected + ", got " + actual);
        }
    }
}
