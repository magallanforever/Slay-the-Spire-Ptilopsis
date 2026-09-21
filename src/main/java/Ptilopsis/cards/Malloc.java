package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.actions.MallocAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Malloc extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(Malloc.class.getSimpleName());

    public Malloc() {
        super(ID, -1, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF);
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new MallocAction(player, this.energyOnUse, this.upgraded, this.freeToPlayOnce));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = CardCrawlGame.languagePack.getCardStrings(ID).UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}
