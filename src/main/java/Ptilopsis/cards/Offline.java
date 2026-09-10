package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.actions.OfflineAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Offline extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(Offline.class.getSimpleName());

    public Offline() {
        super(ID, 2, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = 3;
        this.block = this.baseBlock = 5;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new OfflineAction(player, this.magicNumber, this.name, this.block));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }
}
