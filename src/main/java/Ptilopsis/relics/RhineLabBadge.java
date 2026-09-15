package Ptilopsis.relics;

import Ptilopsis.Ptilopsis;
import Ptilopsis.actions.ThreadCommandAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class RhineLabBadge extends AbstractRelic {
    public static final String ID = Ptilopsis.makeID("RhineLabBadge");

    public RhineLabBadge() {
        // Reuse the existing icon until badge artwork is available.
        super(ID, "dataDisk.png", RelicTier.STARTER, LandingSound.FLAT);
    }

    @Override
    public String getUpdatedDescription() {
        return this.DESCRIPTIONS[0];
    }

    @Override
    public void atBattleStart() {
        flash();
        addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
        addToBot(new ThreadCommandAction(AbstractDungeon.player,
                ThreadCommandAction.Command.CREATE_EMPTY_MUTABLE));
    }

    @Override
    public AbstractRelic makeCopy() {
        return new RhineLabBadge();
    }
}
