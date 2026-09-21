package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MemoryLeak extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(MemoryLeak.class.getSimpleName());

    public MemoryLeak() {
        super(ID, 1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = 3;
        this.selfRetain = true;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        drawCards(player);
        AbstractCard copy = makeStatEquivalentCopy();
        // A thread's free replay does not make the generated hand card free.
        copy.freeToPlayOnce = false;
        addToBot(new MakeTempCardInHandAction(copy, 1));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
            upgradeMagicNumber(-1);
        }
    }
}
