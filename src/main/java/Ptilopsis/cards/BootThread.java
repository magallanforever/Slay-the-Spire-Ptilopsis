package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BootThread extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(BootThread.class.getSimpleName());

    public BootThread() {
        super(ID, 1, CardType.SKILL, CardRarity.BASIC, CardTarget.SELF);
        this.block = this.baseBlock = 4;
        this.magicNumber = this.baseMagicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        createEmptyMutableThread(player);
        gainBlock(player);
        drawCards(player);
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(3);
        }
    }
}
