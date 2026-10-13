package Ptilopsis.threads.effects;

import Ptilopsis.cards.ThreadEffectCard;
import Ptilopsis.threads.ThreadCardPlayback;
import Ptilopsis.threads.ThreadFunction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import java.util.Collections;
import java.util.List;

public class GradientDescentThreadEffect implements ThreadFunction {
    private final String displayName;
    private final int amount;
    private final ThreadCardPlayback playback = new ThreadCardPlayback();

    public GradientDescentThreadEffect(String displayName, int amount) {
        this.displayName = displayName;
        this.amount = amount;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public List<AbstractCard> makePreviewCards() {
        return Collections.singletonList(new ThreadEffectCard(ThreadEffectCard.Effect.THEOREM, this.amount));
    }

    @Override
    public void atEndOfTurn(AbstractPlayer player) {
        this.playback.play(player, makePreviewCards());
    }

    @Override
    public void triggerDestructEffect(AbstractPlayer player) {
        this.playback.playDestruction(player, makeDestructPreviewCards());
    }

    @Override
    public List<AbstractCard> makeDestructPreviewCards() {
        return Collections.singletonList(new ThreadEffectCard(ThreadEffectCard.Effect.THEOREM_ALL, this.amount));
    }
}
