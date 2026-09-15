package Ptilopsis.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import Ptilopsis.Ptilopsis;
import Ptilopsis.powers.ExtremeDataConstructionPower;

public class ExtremeDataConstruction extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(ExtremeDataConstruction.class.getSimpleName());

    public ExtremeDataConstruction() {
        super(ID, 1, CardType.POWER, CardRarity.UNCOMMON, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new ApplyPowerAction(
                player,
                player,
                new ExtremeDataConstructionPower(player),
                1
        ));
        if (this.upgraded) {
            drawCards(player);
        }
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
