package Ptilopsis.powers;

import Ptilopsis.Ptilopsis;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HyperthreadingPower extends AbstractPower {
    public static final String POWER_ID = Ptilopsis.makeID("HyperthreadingPower");

    private boolean bothEdges;

    public HyperthreadingPower(AbstractPlayer owner, int amount, boolean bothEdges) {
        this.ID = POWER_ID;
        this.name = getPowerStrings().NAME;
        this.owner = owner;
        this.amount = amount;
        this.bothEdges = bothEdges;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        loadRegion("loop");
        updateDescription();
    }

    public static HyperthreadingPower get(AbstractPlayer player) {
        AbstractPower power = player.getPower(POWER_ID);
        if (power instanceof HyperthreadingPower) {
            return (HyperthreadingPower) power;
        }
        return null;
    }

    public void enableBothEdges() {
        this.bothEdges = true;
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

        ThreadNetworkPower threadNetwork = ThreadNetworkPower.get((AbstractPlayer) this.owner);
        if (threadNetwork == null) {
            return;
        }

        flash();
        for (int i = 0; i < this.amount; i++) {
            if (this.bothEdges) {
                threadNetwork.triggerLeftmostAndRightmostThreads();
            } else {
                threadNetwork.triggerLeftmostThread();
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
