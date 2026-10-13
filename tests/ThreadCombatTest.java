package Ptilopsis.threads;

import Ptilopsis.patches.CalcificationBlockPatch;
import Ptilopsis.patches.ThreadOrbPatches;
import Ptilopsis.powers.StunPower;
import Ptilopsis.actions.ThreadCommandAction;
import Ptilopsis.actions.BloodBrainBarrierAction;
import Ptilopsis.character.PtilopsisCharacter;
import Ptilopsis.powers.MasterTheoremPower;
import Ptilopsis.powers.ExtremeDataConstructionPower;
import Ptilopsis.threads.effects.CalcificationThreadEffect;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.defect.AnimateOrbAction;
import com.megacrit.cardcrawl.actions.defect.EvokeOrbAction;
import com.megacrit.cardcrawl.actions.defect.ChannelAction;
import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.Prefs;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.monsters.exordium.JawWorm;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.stances.NeutralStance;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import sun.misc.Unsafe;

/** Uses the installed game's combat calculations without constructing graphical objects. */
public class ThreadCombatTest {
    private static final Unsafe UNSAFE = unsafe();

    public static void main(String[] args) throws Exception {
        Gdx.files = new LwjglFiles();
        GdxNativesLoader.load();
        Gdx.gl = Gdx.gl20 = graphicsStub(GL20.class);
        Gdx.graphics = graphicsStub(Graphics.class);
        Gdx.app = graphicsStub(Application.class);
        Settings.language = Settings.GameLanguage.ENG;
        Settings.gamePref = new Prefs() { @Override public void flush() { } };
        Settings.soundPref = Settings.gamePref;
        CardCrawlGame.playerPref = Settings.gamePref;
        for (Field field : UnlockTracker.class.getDeclaredFields()) {
            if (field.getType() == Prefs.class && Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                field.set(null, Settings.gamePref);
            }
        }
        CardCrawlGame.languagePack = new LocalizedStrings();
        CardCrawlGame.sound = bare(SilentSound.class);
        BitmapFont font = new BitmapFont();
        TextureAtlas cardAtlas = new TextureAtlas();
        cardAtlas.addRegion("status/beta", font.getRegion());
        ReflectionHacks.setPrivateStatic(AbstractCard.class, "cardAtlas", cardAtlas);
        ReflectionHacks.setPrivateStatic(AbstractCard.class, "oldCardAtlas", cardAtlas);
        for (Field field : ImageMaster.class.getDeclaredFields()) {
            if (field.getType() == Texture.class && Modifier.isStatic(field.getModifiers())
                    && !Modifier.isFinal(field.getModifiers())) {
                field.setAccessible(true);
                field.set(null, font.getRegion().getTexture());
            }
        }
        for (Field field : FontHelper.class.getDeclaredFields()) {
            if (field.getType() == BitmapFont.class && Modifier.isStatic(field.getModifiers())
                    && !Modifier.isFinal(field.getModifiers())) {
                field.setAccessible(true);
                field.set(null, font);
            }
        }
        AbstractPlayer player = bare(ProbeOrbPlayer.class);
        player.isPlayer = true;
        player.powers = new ArrayList<>();
        player.relics = new ArrayList<>();
        player.orbs = new ArrayList<>();
        player.hand = new CardGroup(CardGroup.CardGroupType.HAND);
        player.stance = bare(NeutralStance.class);
        AbstractDungeon.player = player;
        AbstractDungeon.actionManager = new GameActionManager();
        AbstractMonster enemy = bare(JawWorm.class);
        enemy.powers = new ArrayList<>();
        AbstractMonster other = bare(JawWorm.class);
        other.powers = new ArrayList<>();
        MapRoomNode node = bare(MapRoomNode.class);
        node.room = bare(MonsterRoom.class);
        node.room.phase = AbstractRoom.RoomPhase.COMBAT;
        node.room.monsters = new MonsterGroup(new AbstractMonster[] {enemy, other});
        AbstractDungeon.currMapNode = node;

        ProbeCard card = bare(ProbeCard.class);
        card.baseDamage = 8;
        card.damageTypeForTurn = DamageInfo.DamageType.NORMAL;
        card.type = AbstractCard.CardType.ATTACK;
        card.calculateCardDamage(enemy);
        equal(8, card.damage, "base damage");
        WeakPower weak = bare(WeakPower.class);
        weak.owner = player;
        weak.ID = WeakPower.POWER_ID;
        player.powers.add(weak);
        card.calculateCardDamage(enemy);
        equal(6, card.damage, "Weak at replay time");
        VulnerablePower vulnerable = bare(VulnerablePower.class);
        vulnerable.owner = enemy;
        vulnerable.ID = VulnerablePower.POWER_ID;
        enemy.powers.add(vulnerable);
        card.calculateCardDamage(enemy);
        equal(9, card.damage, "Weak and target Vulnerable");
        card.calculateCardDamage(other);
        equal(6, card.damage, "new target must not retain Vulnerable");
        card.setMultiDamage(true);
        card.calculateCardDamage(null);
        equal(9, card.multiDamage[0], "AOE vulnerable target");
        equal(6, card.multiDamage[1], "AOE normal target");
        player.powers.clear();
        enemy.powers.clear();
        card.setMultiDamage(false);
        card.calculateCardDamage(enemy);
        equal(8, card.damage, "expired statuses must not be cached");

        ThreadOrb orb = bare(ThreadOrb.class);
        Field function = ThreadOrb.class.getDeclaredField("function");
        function.setAccessible(true);
        function.set(orb, new CalcificationThreadEffect(false));
        player.orbs.add(orb);
        int[] block = {5};
        CalcificationBlockPatch.Prefix(player, block);
        equal(6, block[0], "one Calcification");
        ThreadOrb upgraded = bare(ThreadOrb.class);
        function.set(upgraded, new CalcificationThreadEffect(true));
        player.orbs.add(upgraded);
        block[0] = 5;
        CalcificationBlockPatch.Prefix(player, block);
        equal(7, block[0], "upgraded bonus remains one, multiple orbs stack");
        block[0] = 0;
        CalcificationBlockPatch.Prefix(player, block);
        equal(0, block[0], "zero Block does not trigger");
        block[0] = 5;
        CalcificationBlockPatch.Prefix(enemy, block);
        equal(5, block[0], "enemies receive no bonus");
        player.orbs.clear();
        CalcificationBlockPatch.Prefix(player, block);
        equal(5, block[0], "no lingering bonus after removal");

        ProbeFunction probe = new ProbeFunction();
        function.set(orb, probe);
        orb.triggerDestructEffect(player);
        orb.triggerDestructEffect(player);
        equal(2, probe.destructEffects, "both edges trigger independently");
        equal(0, probe.functions, "destruction effect is not the turn-end function");
        if (orb.isEmpty()) {
            throw new AssertionError("triggering destruction effects removed the function");
        }
        orb.onEndOfTurn();
        equal(1, probe.functions, "normal function still runs");
        orb.destroy(player);
        equal(3, probe.destructEffects, "actual removal triggers destruction once");
        if (!orb.isEmpty()) {
            throw new AssertionError("actual removal did not clear the function");
        }
        testEndOfTurnCommand(player, function);
        testThreadLifecycle(player);
        testThreadQueue(player);
        testNativeChannel(player);
        testReentrantLifecycle(player);
        SilenceTest.run(player);
        CardInteractionTest.run(player);
        FFTTest.run(player);
        testStun();
        testBloodBrainBarrier();
        System.out.println("thread_combat_ok");
    }

    private static void testThreadLifecycle(AbstractPlayer player) {
        player.maxOrbs = 3;
        player.orbs.clear();
        for (int i = 0; i < player.maxOrbs; i++) {
            player.orbs.add(new EmptyOrbSlot());
        }
        ThreadNetwork network = ThreadNetwork.forPlayer(player);
        ProbeFunction oldest = new ProbeFunction();
        network.installReadOnly(oldest);
        network.createEmptyMutableSlot();
        ProbeFunction mounted = new ProbeFunction();
        network.mountMutable(mounted);
        network.createEmptyMutableSlot();
        equal(3, ThreadNetwork.threadCount(player), "three native orb slots hold three threads");
        equal(2, ThreadNetwork.activeThreadCount(player), "only mounted functions count as active");
        network.createEmptyMutableSlot();
        drainOrbActions();
        equal(1, oldest.destructEffects, "overflow destructs the oldest thread");
        equal(3, ThreadNetwork.threadCount(player), "overflow preserves capacity");
        equal(3, ThreadNetwork.forPlayer(player).destructAllThreads(), "all threads can be removed without a power");
        equal(1, mounted.destructEffects, "mounted thread destruction remains functional");
        equal(0, ThreadNetwork.threadCount(player), "removal leaves native empty orb slots");
        equal(0, player.powers.size(), "thread lifecycle never adds a player power");
        player.orbs.clear();
    }

    private static void testThreadQueue(AbstractPlayer player) {
        player.maxOrbs = 3;
        player.orbs.clear();
        for (int i = 0; i < player.maxOrbs; i++) {
            player.orbs.add(new EmptyOrbSlot());
        }
        ThreadNetwork network = ThreadNetwork.forPlayer(player);
        List<String> destructOrder = new ArrayList<>();
        ThreadOrb a = appendProbe(network, player, "A", destructOrder);
        ThreadOrb b = appendProbe(network, player, "B", destructOrder);
        ThreadOrb c = appendProbe(network, player, "C", destructOrder);
        assertQueue(player, a, b, c);

        network.destructThread(a);
        assertQueue(player, b, c);
        ThreadOrb d = appendProbe(network, player, "D", destructOrder);
        assertQueue(player, b, c, d);
        ThreadOrb e = appendProbe(network, player, "E", destructOrder);
        assertQueue(player, c, d, e);
        if (!destructOrder.equals(java.util.Arrays.asList("A", "B"))) {
            throw new AssertionError("overflow after removal must destruct B, not the newly added D");
        }

        // Expiration can remove a middle orb during the player's orb update loop.
        d.onEndOfTurn();
        for (AbstractOrb orb : player.orbs) {
            if (orb == d) {
                orb.update();
            }
        }
        assertQueue(player, c, e);
        network.destructThread(d);
        equal(3, destructOrder.size(), "an expired thread cannot be destructed twice");
        ThreadOrb f = appendProbe(network, player, "F", destructOrder);
        assertQueue(player, c, e, f);
        network.triggerEdgeDestructEffects();
        assertQueue(player, c, e, f);
        if (!destructOrder.equals(java.util.Arrays.asList("A", "B", "D", "C", "F"))) {
            throw new AssertionError("edge effects must use the updated queue head and tail");
        }
        equal(3, network.destructAllThreads(), "remove all threads in queue order");
        assertQueue(player);
        if (!destructOrder.equals(java.util.Arrays.asList("A", "B", "D", "C", "F", "C", "E", "F"))) {
            throw new AssertionError("mass destruction must run from head to tail");
        }

        player.maxOrbs = 4;
        player.orbs.add(new EmptyOrbSlot());
        network.createEmptyMutableSlot();
        ThreadOrb empty = (ThreadOrb) player.orbs.get(0);
        a = appendProbe(network, player, "A2", destructOrder);
        b = appendProbe(network, player, "B2", destructOrder);
        c = appendProbe(network, player, "C2", destructOrder);
        assertQueue(player, empty, a, b, c);
        d = appendProbe(network, player, "D2", destructOrder);
        assertQueue(player, a, b, c, d);
        equal(8, destructOrder.size(), "an empty mutable thread still occupies the queue head");
        network.destructThread(d);
        assertQueue(player, a, b, c);
        e = appendProbe(network, player, "E2", destructOrder);
        assertQueue(player, a, b, c, e);
        network.destructAllThreads();
        assertQueue(player);
        player.orbs.clear();
        player.maxOrbs = 3;
    }

    private static ThreadOrb appendProbe(ThreadNetwork network, AbstractPlayer player,
            final String name, final List<String> destructOrder) {
        network.installNewMutable(new ProbeFunction() {
            @Override public void triggerDestructEffect(AbstractPlayer owner) {
                super.triggerDestructEffect(owner);
                destructOrder.add(name);
            }
        });
        drainOrbActions();
        return (ThreadOrb) player.orbs.get(ThreadNetwork.threadCount(player) - 1);
    }

    private static void testNativeChannel(AbstractPlayer player) throws Exception {
        AbstractDungeon.actionManager = new GameActionManager();
        player.maxOrbs = 1;
        player.orbs.clear();
        player.orbs.add(new EmptyOrbSlot());
        ThreadNetwork network = ThreadNetwork.forPlayer(player);
        ProbeChannelPower channelPower = bare(ProbeChannelPower.class);
        channelPower.ID = "probe-channel";
        player.powers.add(channelPower);
        List<String> events = new ArrayList<>();
        ProbeFunction first = new ProbeFunction() {
            @Override public void onConstruct(AbstractPlayer owner) { events.add("construct first"); }
            @Override public void triggerDestructEffect(AbstractPlayer owner) { events.add("destruct first"); }
        };
        ProbeFunction second = new ProbeFunction() {
            @Override public void onConstruct(AbstractPlayer owner) { events.add("construct second"); }
        };
        network.installReadOnly(first);
        ThreadOrb head = (ThreadOrb) player.orbs.get(0);
        equal(1, AbstractDungeon.actionManager.orbsChanneledThisCombat.size(), "native combat channel history");
        equal(1, AbstractDungeon.actionManager.orbsChanneledThisTurn.size(), "native turn channel history");
        network.installNewMutable(second);
        assertQueue(player, head);
        equal(1, events.size(), "a waiting thread must not construct before native insertion");
        List<AbstractGameAction> actions = AbstractDungeon.actionManager.actions;
        if (actions.size() != 3 || !(actions.get(0) instanceof AnimateOrbAction)
                || !(actions.get(1) instanceof EvokeOrbAction) || !(actions.get(2) instanceof ChannelAction)) {
            throw new AssertionError("overflow must use the native animation, evoke, and channel actions");
        }
        drainOrbActions();
        ThreadOrb replacement = (ThreadOrb) player.orbs.get(0);
        if (replacement == head || !head.isEmpty()) {
            throw new AssertionError("native evoke must destroy and replace the queue head");
        }
        if (!events.equals(java.util.Arrays.asList("construct first", "destruct first", "construct second"))) {
            throw new AssertionError("construction must follow the old thread's native destruction");
        }
        ThreadOrbPatches.ChannelOrb.Postfix(player, replacement);
        equal(3, events.size(), "construction hook runs only once");
        equal(2, AbstractDungeon.actionManager.orbsChanneledThisCombat.size(), "deferred channel recorded exactly once");
        equal(2, channelPower.channels, "native onChannel hooks run exactly once per inserted thread");

        player.orbs.clear();
        player.maxOrbs = 0;
        network.installReadOnly(first);
        network.createEmptyMutableSlot();
        assertQueue(player);
        equal(3, events.size(), "zero native slots must not run construction effects");
        equal(0, AbstractDungeon.actionManager.actions.size(), "zero slots must not queue an overflow loop");
        equal(2, channelPower.channels, "failed insertion must not trigger onChannel");
        player.powers.remove(channelPower);

        player.maxOrbs = 2;
        player.orbs.add(new EmptyOrbSlot());
        player.orbs.add(new EmptyOrbSlot());
        ProbeNativeOrb nativeOrb = new ProbeNativeOrb();
        player.channelOrb(nativeOrb);
        network.installReadOnly(first);
        head = (ThreadOrb) player.orbs.get(1);
        network.installNewMutable(second);
        equal(0, nativeOrb.evokes, "full mixed queue waits for native actions");
        drainOrbActions();
        equal(1, nativeOrb.evokes, "native orbs and threads share the same FIFO capacity");
        replacement = (ThreadOrb) player.orbs.get(1);
        assertQueue(player, head, replacement);
        network.destructAllThreads();
        assertQueue(player);
        player.orbs.clear();
        player.maxOrbs = 3;
        AbstractDungeon.actionManager = new GameActionManager();
    }

    private static void testReentrantLifecycle(AbstractPlayer player) {
        player.maxOrbs = 3;
        player.orbs.clear();
        for (int i = 0; i < player.maxOrbs; i++) {
            player.orbs.add(new EmptyOrbSlot());
        }
        ThreadNetwork network = ThreadNetwork.forPlayer(player);
        ThreadOrb[] slots = new ThreadOrb[2];
        int[] callbacks = {0};
        network.installReadOnly(new ProbeFunction() {
            @Override public void onDestruct(AbstractPlayer owner) {
                callbacks[0]++;
                if (callbacks[0] > 1 || !slots[0].isEmpty() || owner.orbs.contains(slots[0])) {
                    throw new AssertionError("destruction callback must see the old function and slot detached");
                }
                slots[0].destroy(owner);
                network.destructThread(slots[0]);
                network.destructThread(slots[1]);
            }
        });
        slots[0] = (ThreadOrb) player.orbs.get(0);
        ProbeFunction sibling = new ProbeFunction();
        network.installReadOnly(sibling);
        slots[1] = (ThreadOrb) player.orbs.get(1);
        network.createEmptyMutableSlot();
        ThreadOrb survivor = (ThreadOrb) player.orbs.get(2);
        network.destructThread(slots[0]);
        equal(1, callbacks[0], "reentrant destruction invokes the old function once");
        equal(1, sibling.destructEffects, "nested removal destructs the sibling once");
        assertQueue(player, survivor);

        survivor.mount(new ProbeFunction() {
            @Override public void atEndOfTurn(AbstractPlayer owner) {
                network.destructThread(survivor);
            }
        }, player);
        survivor.onEndOfTurn();
        assertQueue(player);
        player.orbs.clear();
    }

    private static void drainOrbActions() {
        int remaining = 100;
        while (!AbstractDungeon.actionManager.actions.isEmpty()) {
            AbstractGameAction action = AbstractDungeon.actionManager.actions.remove(0);
            if (!(action instanceof AnimateOrbAction) && !(action instanceof EvokeOrbAction)
                    && !(action instanceof ChannelAction)) {
                throw new AssertionError("unexpected action in native orb queue: " + action.getClass().getName());
            }
            while (!action.isDone && remaining-- > 0) {
                action.update();
            }
            if (!action.isDone) {
                throw new AssertionError("native orb action did not complete");
            }
        }
    }

    private static void assertQueue(AbstractPlayer player, ThreadOrb... expected) {
        equal(player.maxOrbs, player.orbs.size(), "queue retains its native orb slots");
        equal(expected.length, ThreadNetwork.threadCount(player), "queue thread count");
        for (int i = 0; i < player.orbs.size(); i++) {
            if (i < expected.length) {
                if (player.orbs.get(i) != expected[i]) {
                    throw new AssertionError("wrong thread at queue position " + i);
                }
            } else if (!(player.orbs.get(i) instanceof EmptyOrbSlot)) {
                throw new AssertionError("unused slots must follow the queue");
            }
        }
    }

    private static void testBloodBrainBarrier() throws Exception {
        ProbeBlockPlayer player = bare(ProbeBlockPlayer.class);
        player.powers = new ArrayList<>();
        player.isPlayer = true;
        AbstractDungeon.player = player;
        AbstractDungeon.actionManager = new GameActionManager();
        ProbeDamageMonster first = damageMonster(30, 30);
        ProbeDamageMonster second = damageMonster(30, 1);
        ProbeDamageMonster escaped = damageMonster(30, 30);
        escaped.isEscaping = true;
        ProbeDamageMonster unmarked = damageMonster(30, 30);
        ProbeTheorem firstTheorem = theorem(first, player, 7);
        ProbeTheorem secondTheorem = theorem(second, player, 4);
        theorem(escaped, player, 10);
        AbstractDungeon.getCurrRoom().monsters = new MonsterGroup(
                new AbstractMonster[] {first, second, escaped, unmarked});
        BloodBrainBarrierAction action = new BloodBrainBarrierAction(player);
        action.update();
        equal(8, player.currentBlock, "Block uses resolved total damage, including reduction");
        equal(1, player.blockGains, "Block is granted once for all enemies");
        equal(4, firstTheorem.amount, "odd theorem stacks round up after resolution");
        equal(2, secondTheorem.amount, "even theorem stacks halve after resolution");
        equal(0, escaped.hits, "escaped enemy must not be hit");
        equal(0, unmarked.hits, "enemy without theorem must not be hit");
        action.update();
        equal(1, first.hits, "completed action cannot resolve twice");

        ExtremeDataConstructionPower protection = bare(ExtremeDataConstructionPower.class);
        protection.ID = ExtremeDataConstructionPower.POWER_ID;
        player.powers.add(protection);
        new BloodBrainBarrierAction(player).update();
        equal(4, firstTheorem.amount, "Hack it! prevents immediate decay");
        equal(2, secondTheorem.amount, "all protected stacks remain unchanged");
        equal(13, player.currentBlock, "protected resolution still deals damage and grants Block");
        player.powers.clear();

        firstTheorem.atEndOfTurn(true);
        equal(4, firstTheorem.amount, "normal decay waits for the enemy turn");
        firstTheorem.atEndOfTurn(false);
        equal(2, firstTheorem.amount, "normal enemy-turn decay still works");
        firstTheorem.decay();
        firstTheorem.decay();
        equal(1, firstTheorem.amount, "final theorem stack remains one");

        ProbeDamageMonster lethal = damageMonster(2, 2);
        theorem(lethal, player, 9);
        AbstractDungeon.getCurrRoom().monsters = new MonsterGroup(new AbstractMonster[] {lethal});
        new BloodBrainBarrierAction(player).update();
        equal(15, player.currentBlock, "lethal resolution grants the reported damage as Block");
        ProbeDamageMonster immune = damageMonster(30, 0);
        ProbeTheorem immuneTheorem = theorem(immune, player, 5);
        AbstractDungeon.getCurrRoom().monsters = new MonsterGroup(new AbstractMonster[] {immune, unmarked});
        new BloodBrainBarrierAction(player).update();
        equal(15, player.currentBlock, "zero resolved damage grants no Block");
        equal(3, immuneTheorem.amount, "zero damage still resolves the theorem decay");
    }

    private static ProbeDamageMonster damageMonster(int health, int damageCap) throws Exception {
        ProbeDamageMonster monster = bare(ProbeDamageMonster.class);
        monster.currentHealth = health;
        monster.damageCap = damageCap;
        monster.powers = new ArrayList<>();
        monster.hb = new Hitbox(10, 10);
        return monster;
    }

    private static ProbeTheorem theorem(AbstractMonster owner, AbstractPlayer player, int amount) throws Exception {
        ProbeTheorem power = bare(ProbeTheorem.class);
        power.owner = owner;
        power.ID = MasterTheoremPower.POWER_ID;
        power.amount = amount;
        Field source = MasterTheoremPower.class.getDeclaredField("source");
        source.setAccessible(true);
        source.set(power, player);
        owner.powers.add(power);
        return power;
    }

    private static class SilentSound extends SoundMaster {
        @Override public long play(String key) { return 0L; }
        @Override public long play(String key, float pitchVariation) { return 0L; }
    }

    private static class ProbeOrbPlayer extends PtilopsisCharacter {
        private ProbeOrbPlayer() { super("probe"); }
        @Override public void channelOrb(AbstractOrb orb) {
            super.channelOrb(orb);
            // The headless runner does not inject ModTheSpire patches.
            ThreadOrbPatches.ChannelOrb.Postfix(this, orb);
        }
    }

    private static class ProbeChannelPower extends AbstractPower {
        int channels;
        @Override public void onChannel(AbstractOrb orb) { channels++; }
    }

    private static class ProbeNativeOrb extends AbstractOrb {
        int evokes;
        private ProbeNativeOrb() { this.ID = "probe-orb"; }
        public void onEvoke() { evokes++; }
        public void updateDescription() { }
        public void render(SpriteBatch sb) { }
        public void playChannelSFX() { }
        public AbstractOrb makeCopy() { return new ProbeNativeOrb(); }
    }

    private static class ProbeTheorem extends MasterTheoremPower {
        private ProbeTheorem() { super(null, null, 0); }
        @Override public void flashWithoutSound() { }
        @Override public void updateDescription() { }
    }

    private static class ProbeBlockPlayer extends PtilopsisCharacter {
        int blockGains;
        private ProbeBlockPlayer() { super("probe"); }
        @Override public void addBlock(int amount) {
            this.currentBlock += amount;
            this.blockGains++;
        }
    }

    private static class ProbeDamageMonster extends JawWorm {
        int damageCap;
        int hits;
        private ProbeDamageMonster() { super(0, 0); }
        @Override public void damage(DamageInfo info) {
            if (info.type != DamageInfo.DamageType.HP_LOSS) {
                throw new AssertionError("theorem must resolve as HP loss");
            }
            this.hits++;
            this.lastDamageTaken = Math.min(this.currentHealth, Math.min(this.damageCap, info.output));
            this.currentHealth -= this.lastDamageTaken;
            this.isDying = this.currentHealth == 0;
        }
    }

    private static void testEndOfTurnCommand(AbstractPlayer player, Field function) throws Exception {
        ThreadNetwork network = ThreadNetwork.forPlayer(player);
        ThreadOrb first = bare(ThreadOrb.class);
        ThreadOrb second = bare(ThreadOrb.class);
        ProbeFunction firstFunction = new ProbeFunction();
        ProbeFunction secondFunction = new ProbeFunction();
        function.set(first, firstFunction);
        function.set(second, secondFunction);
        Field mutable = ThreadOrb.class.getDeclaredField("mutable");
        mutable.setAccessible(true);
        mutable.setBoolean(first, true);
        player.orbs.add(first);
        player.orbs.add(second);
        player.orbs.add(bare(ThreadOrb.class));
        ThreadCommandAction action = new ThreadCommandAction(player,
                ThreadCommandAction.Command.TRIGGER_ALL_END_OF_TURN_EFFECTS);
        action.update();
        equal(0, player.powers.size(), "thread commands must not add a status icon");
        equal(1, firstFunction.functions, "first thread end-of-turn effect");
        equal(1, secondFunction.functions, "second thread end-of-turn effect");
        equal(0, firstFunction.destructEffects, "end-of-turn command must not destruct");
        Field expiring = ThreadOrb.class.getDeclaredField("expiringFunction");
        expiring.setAccessible(true);
        if (!action.isDone || expiring.get(first) != null || first.isEmpty()) {
            throw new AssertionError("extra function trigger prematurely expired its thread");
        }
        first.onEndOfTurn();
        equal(2, firstFunction.functions, "actual turn end still triggers normally");
        if (expiring.get(first) != firstFunction) {
            throw new AssertionError("actual turn end did not mark an expiring function");
        }
        player.orbs.clear();
        network.triggerAllEndOfTurnEffects();
        player.powers.clear();
    }

    private static void testStun() throws Exception {
        ProbeMonster monster = bare(ProbeMonster.class);
        monster.powers = new ArrayList<>();
        monster.moveHistory = new ArrayList<>();
        monster.nextMove = 3;
        EnemyMoveInfo original = new EnemyMoveInfo((byte) 3, AbstractMonster.Intent.ATTACK, 10, 0, false);
        ReflectionHacks.setPrivate(monster, AbstractMonster.class, "move", original);
        ProbeStun stun = bare(ProbeStun.class);
        stun.owner = monster;
        stun.ID = StunPower.POWER_ID;
        stun.amount = 1;
        monster.powers.add(stun);
        stun.onInitialApplication();
        if (monster.intent != AbstractMonster.Intent.STUN) {
            throw new AssertionError("stun intent was not set");
        }
        equal(0, monster.moveHistory.size(), "stun must not alter AI history");
        AbstractDungeon.actionManager = new GameActionManager();
        if (StunPower.allowTurn(monster)) {
            throw new AssertionError("stunned enemy was allowed to act");
        }
        equal(1, AbstractDungeon.actionManager.actions.size(), "stun duration reduction queued");
        if (!(AbstractDungeon.actionManager.actions.get(0) instanceof ReducePowerAction)) {
            throw new AssertionError("stun did not queue a duration reduction");
        }
        stun.onRemove();
        if (ReflectionHacks.getPrivate(monster, AbstractMonster.class, "move") != original) {
            throw new AssertionError("stun removal did not restore the original move");
        }
        stun.onInitialApplication();
        EnemyMoveInfo phaseMove = new EnemyMoveInfo((byte) 4, AbstractMonster.Intent.BUFF, -1, 0, false);
        ReflectionHacks.setPrivate(monster, AbstractMonster.class, "move", phaseMove);
        stun.onRemove();
        if (ReflectionHacks.getPrivate(monster, AbstractMonster.class, "move") != phaseMove) {
            throw new AssertionError("stun removal overwrote a phase transition");
        }
        monster.powers.clear();
        if (!StunPower.allowTurn(monster)) {
            throw new AssertionError("enemy without stun could not act");
        }
    }

    private static class ProbeMonster extends JawWorm {
        private ProbeMonster() { super(0, 0); }
        @Override public void createIntent() {
            EnemyMoveInfo move = ReflectionHacks.getPrivate(this, AbstractMonster.class, "move");
            this.intent = move.intent;
            this.nextMove = move.nextMove;
        }
    }

    private static class ProbeStun extends StunPower {
        private ProbeStun() { super(null); }
        @Override public void flash() { }
    }

    private static class ProbeFunction implements ThreadFunction {
        int functions;
        int destructEffects;
        public String getDisplayName() { return "probe"; }
        public List<AbstractCard> makePreviewCards() { return Collections.emptyList(); }
        public boolean expiresAtEndOfTurn() { return true; }
        public void atEndOfTurn(AbstractPlayer player) { functions++; }
        public void triggerDestructEffect(AbstractPlayer player) { destructEffects++; }
    }

    private static class ProbeCard extends AbstractCard {
        private ProbeCard() {
            super("probe", "probe", (String) null, 0, "", CardType.ATTACK,
                    CardColor.RED, CardRarity.SPECIAL, CardTarget.ENEMY);
        }
        public void use(AbstractPlayer player, AbstractMonster monster) { }
        void setMultiDamage(boolean value) { this.isMultiDamage = value; }
        public void upgrade() { }
        public AbstractCard makeCopy() { throw new UnsupportedOperationException(); }
    }

    static <T> T bare(Class<T> type) throws InstantiationException {
        return type.cast(UNSAFE.allocateInstance(type));
    }

    private static <T> T graphicsStub(Class<T> type) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type},
                (proxy, method, arguments) -> {
                    if (method.getName().equals("getDeltaTime")) { return 0.1F; }
                    if (method.getName().equals("getType")) { return Application.ApplicationType.Desktop; }
                    if (method.getName().equals("getDisplayMode")) {
                        return new Graphics.DisplayMode(1920, 1080, 60, 32) { };
                    }
                    if (method.getName().equals("getDisplayModes")) {
                        return new Graphics.DisplayMode[] {new Graphics.DisplayMode(1920, 1080, 60, 32) { }};
                    }
                    if (method.getName().equals("getPrimaryMonitor")) {
                        return new Graphics.Monitor(0, 0, "headless") { };
                    }
                    if (method.getName().equals("getMonitors")) {
                        return new Graphics.Monitor[] {new Graphics.Monitor(0, 0, "headless") { }};
                    }
                    Class<?> result = method.getReturnType();
                    if (result == boolean.class) { return false; }
                    if (result == int.class) { return 0; }
                    if (result == long.class) { return 0L; }
                    if (result == float.class) { return 0.0F; }
                    if (result == String.class) { return ""; }
                    return null;
                }));
    }

    private static Unsafe unsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (ReflectiveOperationException error) {
            throw new AssertionError(error);
        }
    }

    private static void equal(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + ": expected " + expected + ", got " + actual);
        }
    }
}
