package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FFT extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID("FFT");

    public FFT() {
        super(ID, 1, CardType.ATTACK, CardRarity.UNCOMMON, CardTarget.ALL_ENEMY);
        this.magicNumber = this.baseMagicNumber = 5;
        this.baseDamage = this.damage = this.magicNumber;
        this.isMultiDamage = true;
    }

    @Override public void applyPowers() { calculateIndexedDamage(); }

    @Override public void calculateCardDamage(AbstractMonster monster) { calculateIndexedDamage(); }

    private void calculateIndexedDamage() {
        this.baseDamage = this.magicNumber;
        if (AbstractDungeon.player == null || AbstractDungeon.currMapNode == null
                || AbstractDungeon.getCurrRoom().monsters == null) {
            this.damage = this.baseDamage;
            this.isDamageModified = false;
            this.multiDamage = new int[0];
            return;
        }
        List<AbstractMonster> monsters = AbstractDungeon.getMonsters().monsters;
        List<AbstractMonster> living = new ArrayList<>();
        for (AbstractMonster enemy : monsters) {
            if (!enemy.isDeadOrEscaped() && !enemy.isDying && !enemy.halfDead && enemy.currentHealth > 0) {
                living.add(enemy);
            }
        }
        living.sort(Comparator.comparingDouble(enemy -> enemy.hb == null ? enemy.drawX : enemy.hb.cX));
        int[] values = new int[monsters.size()];
        try {
            this.isMultiDamage = false;
            for (int i = 0; i < living.size(); i++) {
                AbstractMonster enemy = living.get(i);
                // Multiply the base first: Strength is added once per enemy, and rounding happens last.
                this.baseDamage = this.magicNumber * (i + 1);
                super.calculateCardDamage(enemy);
                values[monsters.indexOf(enemy)] = this.damage;
            }
        } finally {
            this.baseDamage = this.magicNumber;
            this.isMultiDamage = true;
        }
        this.multiDamage = values;
        this.damage = living.isEmpty() ? this.baseDamage : values[monsters.indexOf(living.get(0))];
        this.isDamageModified = this.damage != this.baseDamage;
    }

    @Override public void use(AbstractPlayer player, AbstractMonster monster) {
        calculateIndexedDamage();
        addToBot(new DamageAllEnemiesAction(player, this.multiDamage.clone(), this.damageTypeForTurn,
                AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
    }

    @Override public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(3);
            this.baseDamage = this.damage = this.magicNumber;
        }
    }
}
