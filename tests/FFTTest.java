package Ptilopsis.threads;

import Ptilopsis.cards.FFT;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.exordium.JawWorm;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class FFTTest {
    static void run(AbstractPlayer player) throws Exception {
        List<AbstractMonster> monsters = AbstractDungeon.getMonsters().monsters;
        List<AbstractMonster> saved = new ArrayList<>(monsters);
        try {
            player.powers.clear();
            AbstractMonster left = enemy(100);
            AbstractMonster middle = enemy(300);
            AbstractMonster right = enemy(500);
            monsters.clear();
            monsters.addAll(Arrays.asList(right, left, middle));
            FFT card = new FFT();
            check(card.cost == 1 && !card.exhaust && card.type == AbstractCard.CardType.ATTACK
                    && card.rarity == AbstractCard.CardRarity.UNCOMMON, "FFT card settings");
            card.applyPowers();
            damage(card, 15, 5, 10);
            card.calculateCardDamage(right);
            damage(card, 15, 5, 10);
            check(card.magicNumber == 5, "formula shows the coefficient, not the final damage to one target");

            player.powers.add(new StrengthPower(player, 2));
            card.applyPowers();
            damage(card, 17, 7, 12);
            player.powers.add(new WeakPower(player, 1, false));
            right.powers.add(new VulnerablePower(right, 1, false));
            card.calculateCardDamage(left);
            damage(card, 19, 5, 9);
            card.calculateCardDamage(middle);
            damage(card, 19, 5, 9);
            check(card.baseDamage == 5 && card.magicNumber == 5, "calculations cannot accumulate rank multipliers");

            player.powers.clear();
            right.powers.clear();
            left.isDying = true;
            card.applyPowers();
            damage(card, 10, 0, 5);
            left.isDying = false;
            left.halfDead = true;
            card.applyPowers();
            damage(card, 10, 0, 5);
            left.halfDead = false;
            left.isEscaping = true;
            card.applyPowers();
            damage(card, 10, 0, 5);
            left.isEscaping = false;

            card.upgrade();
            card.upgrade();
            card.applyPowers();
            damage(card, 24, 8, 16);
            check(card.cost == 1 && card.timesUpgraded == 1 && card.magicNumber == 8 && !card.exhaust,
                    "upgrade raises only the coefficient");
            FFT replay = (FFT) ThreadCardPlayback.copyTemplate(card);
            replay.calculateCardDamage(null);
            damage(replay, 24, 8, 16);
            check(replay.upgraded && replay.magicNumber == 8, "thread copy keeps the upgraded coefficient");

            AbstractDungeon.actionManager.actions.clear();
            card.use(player, null);
            check(AbstractDungeon.actionManager.actions.size() == 1
                    && AbstractDungeon.actionManager.actions.get(0) instanceof DamageAllEnemiesAction,
                    "one native area attack resolves the complete vector");
            DamageAllEnemiesAction action = (DamageAllEnemiesAction) AbstractDungeon.actionManager.actions.get(0);
            int[] queued = ReflectionHacks.getPrivate(action, DamageAllEnemiesAction.class, "damage");
            check(Arrays.equals(queued, new int[] {24, 8, 16}), "queued damage matches the preview");
            middle.isDying = true;
            card.applyPowers();
            damage(card, 16, 8, 0);
            check(Arrays.equals(queued, new int[] {24, 8, 16}), "later previews cannot change an already queued attack");
            monsters.clear();
            card.applyPowers();
            damage(card);
        } finally {
            player.powers.clear();
            monsters.clear();
            monsters.addAll(saved);
            AbstractDungeon.actionManager.actions.clear();
        }
        System.out.println("fft_ok");
    }

    private static AbstractMonster enemy(float x) throws Exception {
        AbstractMonster monster = ThreadCombatTest.bare(JawWorm.class);
        monster.currentHealth = monster.maxHealth = 40;
        monster.drawX = x;
        monster.hb = new Hitbox(50, 50);
        monster.hb.move(x, 100);
        monster.powers = new ArrayList<>();
        return monster;
    }

    private static void damage(FFT card, int... expected) {
        check(Arrays.equals(expected, card.multiDamage), "expected " + Arrays.toString(expected)
                + ", got " + Arrays.toString(card.multiDamage));
    }

    private static void check(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); }
    }
}
