package Ptilopsis.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import Ptilopsis.Ptilopsis;
import Ptilopsis.powers.MasterTheoremPower;

public class LIS extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(LIS.class.getSimpleName());

    public LIS() {
        super(ID, 1, CardType.ATTACK, CardRarity.COMMON, CardTarget.ENEMY);
        this.damage = this.baseDamage = 5;
        this.magicNumber = this.baseMagicNumber = 3;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        dealDamage(player, monster, AbstractGameAction.AttackEffect.SLASH_HORIZONTAL);
        addToBot(new ApplyPowerAction(
                monster,
                player,
                new MasterTheoremPower(monster, player, this.magicNumber),
                this.magicNumber
        ));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(3);
        }
    }
}
