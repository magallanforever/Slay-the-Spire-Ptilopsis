package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.character.PtilopsisCharacter;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;

/** The Defect's Capacitor, exposed in Ptilopsis's pool without replacing the original card. */
public class Capacitor extends com.megacrit.cardcrawl.cards.blue.Capacitor {
    public static final String ID = Ptilopsis.makeID(Capacitor.class.getSimpleName());

    public Capacitor() {
        super();
        CardStrings strings = CardCrawlGame.languagePack.getCardStrings(ID);
        this.cardID = ID;
        this.name = strings.NAME;
        this.rawDescription = strings.DESCRIPTION;
        this.color = PtilopsisCharacter.Enums.PTILOPSIS_COLOR;
        initializeDescription();
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(1);
        }
    }

    @Override
    public AbstractCard makeCopy() {
        return new Capacitor();
    }
}
