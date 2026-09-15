package Ptilopsis.threads.effects;

import Ptilopsis.threads.ThreadFunction;
import Ptilopsis.threads.ThreadCardPlayback;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import java.util.Collections;
import java.util.List;

public class CardThreadFunction implements ThreadFunction {
    private final AbstractCard storedCard;
    private final ThreadCardPlayback playback = new ThreadCardPlayback();

    public CardThreadFunction(AbstractCard card) {
        this.storedCard = card.makeStatEquivalentCopy();
    }

    @Override
    public String getDisplayName() {
        return this.storedCard.name;
    }

    @Override
    public List<AbstractCard> makePreviewCards() {
        return Collections.singletonList(this.storedCard.makeStatEquivalentCopy());
    }

    @Override
    public boolean expiresAtEndOfTurn() {
        return this.storedCard.exhaust || this.storedCard.type == AbstractCard.CardType.POWER;
    }

    @Override
    public void atEndOfTurn(AbstractPlayer player) {
        this.playback.play(player, Collections.singletonList(this.storedCard));
    }

    @Override
    public boolean isReplayPending(AbstractPlayer player) {
        return this.playback.isPending(player);
    }
}
