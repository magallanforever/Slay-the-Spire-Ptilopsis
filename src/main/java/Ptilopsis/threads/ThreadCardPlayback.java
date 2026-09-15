package Ptilopsis.threads;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ThreadCardPlayback {
    private final List<AbstractCard> queuedCards = new ArrayList<>();

    public void play(AbstractPlayer player, List<AbstractCard> templates) {
        queue(player, templates, false);
    }

    public void playDestruction(AbstractPlayer player, List<AbstractCard> templates) {
        queue(player, templates, true);
    }

    private void queue(AbstractPlayer player, List<AbstractCard> templates, boolean destruction) {
        if (player == null || player.isDeadOrEscaped()
                || AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            return;
        }
        Set<ThreadCardPlayback> ancestors = player.cardInUse == null
                ? new HashSet<>() : new HashSet<>(Origin.ancestors.get(player.cardInUse));
        // Allow separate triggers, but stop a replay chain from calling itself recursively.
        if (!ancestors.add(this) && !destruction) {
            return;
        }
        this.queuedCards.removeIf(card -> !isPending(player, card));
        for (AbstractCard template : templates) {
            AbstractCard card = template.makeStatEquivalentCopy();
            card.freeToPlayOnce = true;
            card.purgeOnUse = true;
            card.current_x = card.target_x = player.drawX;
            card.current_y = card.target_y = player.drawY;
            Origin.ancestors.set(card, new HashSet<>(ancestors));
            this.queuedCards.add(card);
            CardQueueItem item = new CardQueueItem(card, true, EnergyPanel.totalCount, true, true);
            item.isEndTurnAutoPlay = player.endTurnQueued;
            AbstractDungeon.actionManager.addCardQueueItem(item);
        }
    }

    public boolean isPending(AbstractPlayer player) {
        this.queuedCards.removeIf(card -> !isPending(player, card));
        return !this.queuedCards.isEmpty();
    }

    private static boolean isPending(AbstractPlayer player, AbstractCard card) {
        if (player.cardInUse == card) {
            return true;
        }
        for (CardQueueItem item : AbstractDungeon.actionManager.cardQueue) {
            if (item.card == card) {
                return true;
            }
        }
        return false;
    }

    @SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
    public static class Origin {
        public static final SpireField<Set<ThreadCardPlayback>> ancestors = new SpireField<>(HashSet::new);
    }
}
