package Ptilopsis.threads;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import java.util.List;
import java.util.Collections;

public interface ThreadFunction {
    String getDisplayName();

    // Return fresh templates for display/playback; these factories must not execute effects.
    List<AbstractCard> makePreviewCards();

    default List<AbstractCard> makeConstructPreviewCards() {
        return Collections.emptyList();
    }

    default List<AbstractCard> makeDestructPreviewCards() {
        return Collections.emptyList();
    }

    // Arbitrary stored cards keep their native card view; fixed effects use compact text.
    default boolean showsCardPreview() {
        return false;
    }

    default boolean runsAtStartOfTurn() {
        return false;
    }

    default boolean expiresAtEndOfTurn() {
        return false;
    }

    default boolean isReplayPending(AbstractPlayer player) {
        return false;
    }

    default void onConstruct(AbstractPlayer player) {
    }

    default void atStartOfTurn(AbstractPlayer player) {
    }

    default void atEndOfTurn(AbstractPlayer player) {
    }

    default int getBlockGainBonus() {
        return 0;
    }

    default String getPreviewNote() {
        return "";
    }

    default void triggerDestructEffect(AbstractPlayer player) {
    }

    default void onDestruct(AbstractPlayer player) {
        triggerDestructEffect(player);
    }
}
