package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CacheRefresh extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(CacheRefresh.class.getSimpleName());

    public CacheRefresh() {
        super(ID, 1, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = 1;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        drawCards(player);
        drawCards(player, activeThreadCount(player));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }
}
