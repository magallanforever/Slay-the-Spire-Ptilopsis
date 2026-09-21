package Ptilopsis.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class MallocAction extends AbstractGameAction {
    private final AbstractPlayer player;
    private final int energyOnUse;
    private final boolean upgraded;
    private final boolean freeToPlayOnce;

    public MallocAction(AbstractPlayer player, int energyOnUse, boolean upgraded, boolean freeToPlayOnce) {
        this.player = player;
        this.energyOnUse = energyOnUse;
        this.upgraded = upgraded;
        this.freeToPlayOnce = freeToPlayOnce;
        this.actionType = ActionType.SPECIAL;
    }

    @Override
    public void update() {
        if (this.isDone) {
            return;
        }
        int x = this.energyOnUse == -1 ? EnergyPanel.totalCount : this.energyOnUse;
        int count = x;
        if (this.player.hasRelic(ChemicalX.ID)) {
            count += 2;
            this.player.getRelic(ChemicalX.ID).flash();
        }
        for (int i = 0; i < count; i++) {
            addToBot(new ThreadCommandAction(this.player, ThreadCommandAction.Command.CREATE_EMPTY_MUTABLE));
        }
        if (count > 0 && !this.freeToPlayOnce) {
            this.player.energy.use(EnergyPanel.totalCount);
        }
        if (this.upgraded && x >= 1) {
            addToBot(new GainEnergyAction(1));
        }
        this.isDone = true;
    }
}
