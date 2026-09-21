package Ptilopsis.powers;

import Ptilopsis.Ptilopsis;
import Ptilopsis.actions.SilenceDraftAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class SilencePower extends AbstractPower {
    public static final String POWER_ID = Ptilopsis.makeID("SilencePower");

    public SilencePower(AbstractPlayer owner, int amount) {
        this.ID = POWER_ID;
        this.name = CardCrawlGame.languagePack.getPowerStrings(POWER_ID).NAME;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;
        loadRegion("ai");
        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        flash();
        for (int i = 0; i < this.amount; i++) {
            addToBot(new SilenceDraftAction((AbstractPlayer) this.owner));
        }
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        updateDescription();
    }

    @Override
    public void updateDescription() {
        PowerStrings strings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        this.description = strings.DESCRIPTIONS[0] + this.amount + strings.DESCRIPTIONS[1];
    }
}
