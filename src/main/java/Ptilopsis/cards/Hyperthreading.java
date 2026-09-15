package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.powers.HyperthreadingPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Hyperthreading extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(Hyperthreading.class.getSimpleName());

    public Hyperthreading() {
        super(ID, 1, CardType.POWER, CardRarity.UNCOMMON, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        if (this.upgraded) {
            HyperthreadingPower existing = HyperthreadingPower.get(player);
            if (existing != null) {
                existing.enableBothEdges();
            }
        }
        addToBot(new ApplyPowerAction(
                player,
                player,
                new HyperthreadingPower(player, this.magicNumber, this.upgraded),
                this.magicNumber
        ));
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
