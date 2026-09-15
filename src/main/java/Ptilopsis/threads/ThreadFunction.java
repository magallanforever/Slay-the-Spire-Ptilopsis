package Ptilopsis.threads;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.cards.AbstractCard;
import java.util.List;

public interface ThreadFunction {
    String getDisplayName();

    List<AbstractCard> makePreviewCards();

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

    default void onManualTrigger(AbstractPlayer player) {
        atEndOfTurn(player);
    }

    default void onDestruct(AbstractPlayer player) {
    }
}
