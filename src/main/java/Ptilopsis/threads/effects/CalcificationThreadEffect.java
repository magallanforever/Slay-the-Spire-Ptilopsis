package Ptilopsis.threads.effects;

import Ptilopsis.Ptilopsis;
import Ptilopsis.cards.ThreadEffectCard;
import Ptilopsis.threads.ThreadCardPlayback;
import Ptilopsis.threads.ThreadFunction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import java.util.Collections;
import java.util.List;

public class CalcificationThreadEffect implements ThreadFunction {
    private final boolean upgraded;
    private final ThreadCardPlayback playback = new ThreadCardPlayback();

    public CalcificationThreadEffect(boolean upgraded) {
        this.upgraded = upgraded;
    }

    @Override
    public String getDisplayName() {
        return CardCrawlGame.languagePack.getCardStrings(Ptilopsis.makeID("CalcificationGuard")).NAME
                + (this.upgraded ? "+" : "");
    }

    @Override
    public int getBlockGainBonus() {
        return 1;
    }

    @Override
    public String getPreviewNote() {
        return CardCrawlGame.languagePack.getCardStrings(Ptilopsis.makeID("CalcificationGuard"))
                .EXTENDED_DESCRIPTION[0];
    }

    @Override
    public List<AbstractCard> makePreviewCards() {
        return Collections.singletonList(new ThreadEffectCard(
                ThreadEffectCard.Effect.CALCIFICATION, this.upgraded ? 5 : 3));
    }

    @Override
    public void atEndOfTurn(AbstractPlayer player) {
        this.playback.play(player, makePreviewCards());
    }

    @Override
    public void triggerDestructEffect(AbstractPlayer player) {
        this.playback.playDestruction(player, Collections.singletonList(
                new ThreadEffectCard(ThreadEffectCard.Effect.DOUBLE_BLOCK, 0)));
    }
}
