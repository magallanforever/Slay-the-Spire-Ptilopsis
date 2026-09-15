package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.powers.EnkephalinPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.defect.IncreaseMaxOrbAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Enkephalin extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(Enkephalin.class.getSimpleName());

    public Enkephalin() {
        super(ID, 3, CardType.POWER, CardRarity.COMMON, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new ApplyPowerAction(player, player, new EnkephalinPower(player, 1), 1));
        if (this.upgraded) {
            addToBot(new IncreaseMaxOrbAction(1));
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
