package Ptilopsis.threads.effects;

import Ptilopsis.threads.ThreadFunction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class WakeRoutineThreadEffect implements ThreadFunction {
    private final String displayName;
    private final int drawAmount;

    public WakeRoutineThreadEffect(String displayName, int drawAmount) {
        this.displayName = displayName;
        this.drawAmount = drawAmount;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public void atStartOfTurn(AbstractPlayer player) {
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(player, this.drawAmount));
    }

    @Override
    public void onManualTrigger(AbstractPlayer player) {
        gainEnergyAndDraw(player);
    }

    @Override
    public void onDestruct(AbstractPlayer player) {
        gainEnergyAndDraw(player);
    }

    private void gainEnergyAndDraw(AbstractPlayer player) {
        AbstractDungeon.actionManager.addToBottom(new GainEnergyAction(1));
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(player, 1));
    }
}
