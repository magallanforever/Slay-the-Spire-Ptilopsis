package Ptilopsis.threads.effects;

import Ptilopsis.threads.ThreadFunction;
import Ptilopsis.threads.ThreadCardPlayback;
import Ptilopsis.cards.ThreadEffectCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import java.util.Collections;
import java.util.List;

public class WakeRoutineThreadEffect implements ThreadFunction {
    private final String displayName;
    private final int drawAmount;
    private final ThreadCardPlayback playback = new ThreadCardPlayback();

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
        this.playback.play(player, makePreviewCards());
    }

    @Override
    public List<AbstractCard> makePreviewCards() {
        return Collections.singletonList(new ThreadEffectCard(ThreadEffectCard.Effect.DRAW, this.drawAmount));
    }

    @Override
    public boolean runsAtStartOfTurn() {
        return true;
    }

    @Override
    public void onManualTrigger(AbstractPlayer player) {
        gainEnergyAndDraw(player);
    }

    @Override
    public void onDestruct(AbstractPlayer player) {
        this.playback.playDestruction(player, recoveryCards());
    }

    private void gainEnergyAndDraw(AbstractPlayer player) {
        this.playback.play(player, recoveryCards());
    }

    private List<AbstractCard> recoveryCards() {
        return Collections.singletonList(new ThreadEffectCard(ThreadEffectCard.Effect.RECOVER, 1));
    }
}
