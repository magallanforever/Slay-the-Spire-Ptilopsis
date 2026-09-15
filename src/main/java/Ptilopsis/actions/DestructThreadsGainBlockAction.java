package Ptilopsis.actions;

import Ptilopsis.powers.ThreadNetworkPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class DestructThreadsGainBlockAction extends AbstractGameAction {
    private final AbstractPlayer player;
    private final int blockPerThread;

    public DestructThreadsGainBlockAction(AbstractPlayer player, int blockPerThread) {
        this.player = player;
        this.target = player;
        this.source = player;
        this.blockPerThread = blockPerThread;
    }

    @Override
    public void update() {
        if (this.player == null || this.player.isDeadOrEscaped()) {
            this.isDone = true;
            return;
        }

        int destructed = ThreadNetworkPower.getOrCreate(this.player).destructAllThreads();
        if (destructed > 0 && this.blockPerThread > 0) {
            AbstractDungeon.actionManager.addToBottom(new GainBlockAction(
                    this.player,
                    this.player,
                    destructed * this.blockPerThread
            ));
        }
        this.isDone = true;
    }
}
