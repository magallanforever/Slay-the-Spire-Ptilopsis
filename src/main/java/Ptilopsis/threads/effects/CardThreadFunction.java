package Ptilopsis.threads.effects;

import Ptilopsis.threads.ThreadFunction;
import Ptilopsis.threads.ThreadCardPlayback;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public class CardThreadFunction implements ThreadFunction {
    private final List<AbstractCard> storedCards = new ArrayList<>();
    private final ThreadCardPlayback playback = new ThreadCardPlayback();

    public CardThreadFunction(AbstractCard card) {
        this(Collections.singletonList(card));
    }

    public CardThreadFunction(List<AbstractCard> cards) {
        for (AbstractCard card : cards) {
            this.storedCards.add(ThreadCardPlayback.copyTemplate(card));
        }
    }

    @Override
    public String getDisplayName() {
        List<String> names = new ArrayList<>();
        for (AbstractCard card : this.storedCards) {
            names.add(card.name);
        }
        return String.join(" / ", names);
    }

    @Override
    public List<AbstractCard> makePreviewCards() {
        List<AbstractCard> previews = new ArrayList<>();
        for (AbstractCard card : this.storedCards) {
            previews.add(ThreadCardPlayback.copyTemplate(card));
        }
        return previews;
    }

    @Override
    public boolean showsCardPreview() {
        return true;
    }

    @Override
    public boolean expiresAtEndOfTurn() {
        for (AbstractCard card : this.storedCards) {
            if (card.exhaust || card.type == AbstractCard.CardType.POWER) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void atEndOfTurn(AbstractPlayer player) {
        this.playback.play(player, this.storedCards);
    }

    @Override
    public boolean isReplayPending(AbstractPlayer player) {
        return this.playback.isPending(player);
    }

}
