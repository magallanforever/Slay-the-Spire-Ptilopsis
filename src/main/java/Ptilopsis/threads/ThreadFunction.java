package Ptilopsis.threads;

import com.megacrit.cardcrawl.characters.AbstractPlayer;

public interface ThreadFunction {
    String getDisplayName();

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
