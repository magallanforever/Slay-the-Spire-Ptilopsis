package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.powers.MasterTheoremPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class OverflowCheck extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(OverflowCheck.class.getSimpleName());

    public OverflowCheck() {
        super(ID, 2, CardType.SKILL, CardRarity.COMMON, CardTarget.ENEMY);
        this.block = this.baseBlock = 14;
        this.magicNumber = this.baseMagicNumber = 8;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        gainBlock(player);
        addToBot(new ApplyPowerAction(monster, player,
                new MasterTheoremPower(monster, player, this.magicNumber), this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(3);
            upgradeMagicNumber(2);
        }
    }
}
