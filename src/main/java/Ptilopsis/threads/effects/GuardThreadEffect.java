package Ptilopsis.threads.effects;

import Ptilopsis.threads.ThreadFunction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class GuardThreadEffect implements ThreadFunction {
    private final String displayName;
    private final int block;
    private final int drawOnDestruct;

    public GuardThreadEffect(String displayName, int block, int drawOnDestruct) {
        this.displayName = displayName;
        this.block = block;
        this.drawOnDestruct = drawOnDestruct;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public void onConstruct(AbstractPlayer player) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(player, player, this.block));
    }

    @Override
    public void atEndOfTurn(AbstractPlayer player) {
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(player, player, this.block));
    }

    @Override
    public void onDestruct(AbstractPlayer player) {
        if (this.drawOnDestruct > 0) {
            AbstractDungeon.actionManager.addToBottom(new DrawCardAction(player, this.drawOnDestruct));
        }
    }
}
