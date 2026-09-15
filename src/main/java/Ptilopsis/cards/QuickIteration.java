package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.actions.GenerateDiscountedPtilopsisCardsAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class QuickIteration extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(QuickIteration.class.getSimpleName());

    public QuickIteration() {
        super(ID, 1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = 2;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new GenerateDiscountedPtilopsisCardsAction(player, this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }
}
