package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CoroutineYield extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(CoroutineYield.class.getSimpleName());

    public CoroutineYield() {
        super(ID, 1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        triggerAllThreads(player);
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }
}
