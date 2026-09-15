package Ptilopsis.threads.effects;

import Ptilopsis.threads.ThreadFunction;
import Ptilopsis.threads.ThreadCardPlayback;
import Ptilopsis.cards.ThreadEffectCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import java.util.Collections;
import java.util.List;

public class GuardThreadEffect implements ThreadFunction {
    private final String displayName;
    private final int block;
    private final int drawOnDestruct;
    private final ThreadCardPlayback playback = new ThreadCardPlayback();

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
        this.playback.play(player, makePreviewCards());
    }

    @Override
    public void atEndOfTurn(AbstractPlayer player) {
        this.playback.play(player, makePreviewCards());
    }

    @Override
    public List<AbstractCard> makePreviewCards() {
        return Collections.singletonList(new ThreadEffectCard(ThreadEffectCard.Effect.GUARD, this.block));
    }

    @Override
    public void onDestruct(AbstractPlayer player) {
        if (this.drawOnDestruct > 0) {
            this.playback.playDestruction(player, Collections.singletonList(
                    new ThreadEffectCard(ThreadEffectCard.Effect.DRAW, this.drawOnDestruct)));
        }
    }
}
