package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.powers.ThreadNetworkPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Rosmontis extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(Rosmontis.class.getSimpleName());
    private static final int BASE_DAMAGE = 12;
    private static final int UPGRADED_DAMAGE = 18;

    public Rosmontis() {
        super(ID, 1, CardType.ATTACK, CardRarity.RARE, CardTarget.ALL_ENEMY);
        this.damage = this.baseDamage = BASE_DAMAGE;
        this.isMultiDamage = true;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new DamageAllEnemiesAction(
                player,
                this.multiDamage,
                this.damageTypeForTurn,
                AbstractGameAction.AttackEffect.BLUNT_HEAVY
        ));

        int threadCount = ThreadNetworkPower.threadCount(player);
        for (int i = 0; i < threadCount; i++) {
            addToBot(new DamageAllEnemiesAction(
                    player,
                    halfDamage(this.multiDamage),
                    DamageInfo.DamageType.NORMAL,
                    AbstractGameAction.AttackEffect.BLUNT_LIGHT
            ));
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(UPGRADED_DAMAGE - BASE_DAMAGE);
        }
    }

    private int[] halfDamage(int[] values) {
        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = values[i] / 2;
        }
        return result;
    }
}
