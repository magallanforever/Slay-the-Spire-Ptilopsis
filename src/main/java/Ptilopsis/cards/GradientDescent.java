package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.threads.effects.GradientDescentThreadEffect;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class GradientDescent extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(GradientDescent.class.getSimpleName());

    public GradientDescent() {
        super(ID, 1, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = 4;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        installReadOnlyThread(player, new GradientDescentThreadEffect(this.name, this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(2);
        }
    }
}
