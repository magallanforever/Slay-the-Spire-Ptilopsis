package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.actions.SilenceDraftAction;
import Ptilopsis.powers.SilencePower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Silence extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(Silence.class.getSimpleName());

    public Silence() {
        super(ID, 2, CardType.POWER, CardRarity.RARE, CardTarget.SELF);
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new ApplyPowerAction(player, player, new SilencePower(player, 1), 1));
        if (this.upgraded) {
            addToBot(new SilenceDraftAction(player));
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
