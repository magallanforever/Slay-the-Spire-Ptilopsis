package Ptilopsis.powers;

import Ptilopsis.Ptilopsis;
import Ptilopsis.threads.ThreadNetwork;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HyperthreadingPower extends AbstractPower {
    public static final String POWER_ID = Ptilopsis.makeID("HyperthreadingPower");
    public static final String UPGRADED_POWER_ID = Ptilopsis.makeID("HyperthreadingPlusPower");

    private final boolean bothEdges;

    public HyperthreadingPower(AbstractPlayer owner, int amount, boolean bothEdges) {
        this.ID = bothEdges ? UPGRADED_POWER_ID : POWER_ID;
        this.name = getPowerStrings().NAME + (bothEdges ? "+" : "");
        this.owner = owner;
        this.amount = amount;
        this.bothEdges = bothEdges;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        loadRegion("loop");
        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        updateDescription();
    }

    @Override
    public void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
        if (!isPlayer || !(this.owner instanceof AbstractPlayer)) {
            return;
        }

        ThreadNetwork threadNetwork = ThreadNetwork.forPlayer((AbstractPlayer) this.owner);

        flash();
        for (int i = 0; i < this.amount; i++) {
            if (this.bothEdges) {
                threadNetwork.triggerEdgeDestructEffects();
            } else {
                threadNetwork.triggerLeftmostDestructEffect();
            }
        }
    }

    @Override
    public void updateDescription() {
        PowerStrings strings = getPowerStrings();
        if (this.bothEdges) {
            this.description = strings.DESCRIPTIONS[2] + this.amount + strings.DESCRIPTIONS[3];
        } else {
            this.description = strings.DESCRIPTIONS[0] + this.amount + strings.DESCRIPTIONS[1];
        }
    }

    private static PowerStrings getPowerStrings() {
        return CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    }
}
