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
        this.displayName = displayName;
        for (AbstractCard card : selectedCards) {
            this.storedCards.add(ThreadCardPlayback.copyTemplate(card));
        }
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
    public boolean showsCardPreview() {
        return true;
    }

    @Override
    public List<AbstractCard> makePreviewCards() {
        List<AbstractCard> cards = new ArrayList<>();
        for (AbstractCard card : this.storedCards) {
            cards.add(ThreadCardPlayback.copyTemplate(card));
        }
        return cards;
    }

    @Override
    public void onDestruct(AbstractPlayer player) {
        if (this.destroyed) {
            return;
        }
        triggerDestructEffect(player);
        this.destroyed = true;
        this.storedCards.clear();
    }

    @Override
    public void triggerDestructEffect(AbstractPlayer player) {
        if (this.destroyed) {
            return;
        }
        this.playback.playDestruction(player, makeDestructPreviewCards());
    }

    @Override
    public List<AbstractCard> makeDestructPreviewCards() {
        return Collections.singletonList(new ThreadEffectCard(ThreadEffectCard.Effect.GUARD, this.blockOnDestruct));
    }

    @Override
    public boolean isReplayPending(AbstractPlayer player) {
        return this.playback.isPending(player);
    }
}
