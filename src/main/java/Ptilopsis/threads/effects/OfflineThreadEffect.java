package Ptilopsis.threads.effects;

import Ptilopsis.threads.ThreadFunction;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import java.util.ArrayList;
import java.util.List;

public class OfflineThreadEffect implements ThreadFunction {
    private final String displayName;
    private final int blockOnDestruct;
    private final ArrayList<AbstractCard> storedCards = new ArrayList<>();
    private final ArrayList<AbstractCard> queuedCards = new ArrayList<>();
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
            stored.tags.remove(Enums.PTILOPSIS_OFFLINE_REPLAY);
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
        if (this.destroyed
                || player.isDeadOrEscaped()
                || isReplayPending(player)
                || AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            return;
        }

        this.queuedCards.clear();
        for (AbstractCard storedCard : this.storedCards) {
            AbstractCard card = storedCard.makeStatEquivalentCopy();
            card.tags.add(Enums.PTILOPSIS_OFFLINE_REPLAY);
            card.freeToPlayOnce = true;
            card.purgeOnUse = true;
            this.queuedCards.add(card);

            CardQueueItem item = new CardQueueItem(card, true, EnergyPanel.totalCount, true, true);
            item.isEndTurnAutoPlay = player.endTurnQueued;
            AbstractDungeon.actionManager.addCardQueueItem(item);
        }
    }

    @Override
    public void onManualTrigger(AbstractPlayer player) {
        if (player.cardInUse != null && player.cardInUse.hasTag(Enums.PTILOPSIS_OFFLINE_REPLAY)) {
            return;
        }
        atEndOfTurn(player);
    }

    @Override
    public void onDestruct(AbstractPlayer player) {
        if (this.destroyed) {
            return;
        }
        this.destroyed = true;
        this.storedCards.clear();
        AbstractDungeon.actionManager.addToBottom(new GainBlockAction(player, player, this.blockOnDestruct));
    }

    private boolean isReplayPending(AbstractPlayer player) {
        if (this.queuedCards.contains(player.cardInUse)) {
            return true;
        }

        for (CardQueueItem item : AbstractDungeon.actionManager.cardQueue) {
            if (this.queuedCards.contains(item.card)) {
                return true;
            }
        }
        return false;
    }

    public static class Enums {
        @SpireEnum
        public static AbstractCard.CardTags PTILOPSIS_OFFLINE_REPLAY;
    }
}
