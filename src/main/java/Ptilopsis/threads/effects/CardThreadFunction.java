package Ptilopsis.threads.effects;

import Ptilopsis.threads.ThreadFunction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import java.util.ArrayList;

public class CardThreadFunction implements ThreadFunction {
    private final AbstractCard storedCard;
    private final ArrayList<AbstractCard> queuedCards = new ArrayList<>();

    public CardThreadFunction(AbstractCard card) {
        this.storedCard = card.makeStatEquivalentCopy();
    }

    @Override
    public String getDisplayName() {
        return this.storedCard.name;
    }

    @Override
    public void atEndOfTurn(AbstractPlayer player) {
        if (player.isDeadOrEscaped()
                || isReplayPending(player)
                || AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            return;
        }

        AbstractCard card = this.storedCard.makeStatEquivalentCopy();
        card.freeToPlayOnce = true;
        card.purgeOnUse = true;
        card.current_x = player.drawX;
        card.current_y = player.drawY;
        card.target_x = player.drawX;
        card.target_y = player.drawY;
        this.queuedCards.add(card);

        CardQueueItem item = new CardQueueItem(card, true, EnergyPanel.totalCount, true, true);
        item.isEndTurnAutoPlay = player.endTurnQueued;
        AbstractDungeon.actionManager.addCardQueueItem(item);
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
        this.queuedCards.clear();
        return false;
    }
}
