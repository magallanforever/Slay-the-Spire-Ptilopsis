package Ptilopsis.potions;

import Ptilopsis.Ptilopsis;
import Ptilopsis.actions.SourcePrivateKeyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;

public class SourcePrivateKey extends AbstractPotion {
    public static final String ID = Ptilopsis.makeID("SourcePrivateKey");

    public SourcePrivateKey() {
        super(CardCrawlGame.languagePack.getPotionString(ID).NAME, ID,
                PotionRarity.RARE, PotionSize.CARD, PotionColor.WHITE);
        isThrown = false;
        targetRequired = false;
    }

    @Override
    public void initializeData() {
        PotionStrings strings = CardCrawlGame.languagePack.getPotionString(ID);
        name = strings.NAME;
        potency = getPotency();
        description = strings.DESCRIPTIONS[potency > 1 ? 1 : 0];
        tips.clear();
        tips.add(new PowerTip(name, description));
    }

    @Override
    public void use(AbstractCreature target) {
        addToBot(new SourcePrivateKeyAction(potency));
    }

    @Override
    public int getPotency(int ascensionLevel) {
        return 1;
    }

    @Override
    public AbstractPotion makeCopy() {
        return new SourcePrivateKey();
    }
}
