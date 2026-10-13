package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.powers.FreePowerCardsPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.DrawPileToHandAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BootWisdom extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID("BootWisdom");

    public BootWisdom() {
        super(ID, 3, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF);
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        gainEnergy(1);
        addToBot(new DrawPileToHandAction(2, CardType.POWER));
        addToBot(new ApplyPowerAction(player, player, new FreePowerCardsPower(player, 2), 2));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.isInnate = true;
            this.rawDescription = CardCrawlGame.languagePack.getCardStrings(ID).UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}
