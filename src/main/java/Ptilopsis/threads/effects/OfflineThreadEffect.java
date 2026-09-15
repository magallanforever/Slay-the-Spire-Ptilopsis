package Ptilopsis.threads.effects;

import Ptilopsis.threads.ThreadFunction;
import Ptilopsis.threads.ThreadCardPlayback;
import Ptilopsis.cards.ThreadEffectCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OfflineThreadEffect implements ThreadFunction {
    private final String displayName;
    private final int blockOnDestruct;
    private final ArrayList<AbstractCard> storedCards = new ArrayList<>();
    private final ThreadCardPlayback playback = new ThreadCardPlayback();
    private boolean destroyed;

    public OfflineThreadEffect(String displayName, List<AbstractCard> selectedCards, int blockOnDestruct) {
        this.blockOnDestruct = blockOnDestruct;
        StringBuilder builder = new StringBuilder(displayName).append(" [");
        for (AbstractCard card : selectedCards) {
            if (!this.storedCards.isEmpty()) {
                builder.append(" → ");
            }
            builder.append(card.name);

            AbstractCard stored = card.makeStatEquivalentCopy();
            this.storedCards.add(stored);
        }
        this.displayName = builder.append("]").toString();
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public void atEndOfTurn(AbstractPlayer player) {
        if (!this.destroyed) {
            this.playback.play(player, this.storedCards);
        }
    }

    @Override
    public List<AbstractCard> makePreviewCards() {
        List<AbstractCard> cards = new ArrayList<>();
        for (AbstractCard card : this.storedCards) {
            cards.add(card.makeStatEquivalentCopy());
        }
        return cards;
    }

    @Override
    public void onDestruct(AbstractPlayer player) {
        if (this.destroyed) {
            return;
        }
        this.destroyed = true;
        this.storedCards.clear();
        this.playback.playDestruction(player, Collections.singletonList(
                new ThreadEffectCard(ThreadEffectCard.Effect.GUARD, this.blockOnDestruct)));
    }

    @Override
    public boolean isReplayPending(AbstractPlayer player) {
        return this.playback.isPending(player);
    }
}
