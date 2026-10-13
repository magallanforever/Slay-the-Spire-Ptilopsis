package Ptilopsis.actions;

import Ptilopsis.threads.ThreadOrb;
import Ptilopsis.threads.effects.CardThreadFunction;
import Ptilopsis.ui.ThreadUiText;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import java.util.ArrayList;
import java.util.List;

/** The action queue owns a manual selection, even if its target orb is removed. */
public final class ManualMountAction extends AbstractGameAction {
    private final AbstractPlayer player;
    private final ThreadOrb orb;
    private final AbstractRoom room;
    private HandCardSelectScreen selection;

    public ManualMountAction(AbstractPlayer player, ThreadOrb orb) {
        this.player = player;
        this.orb = orb;
        this.room = AbstractDungeon.getCurrRoom();
        this.actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        if (this.isDone) {
            return;
        }
        if (!isSameCombat()) {
            this.isDone = true;
            cancelSelection();
            return;
        }
        if (AbstractDungeon.isScreenUp) {
            return;
        }
        if (this.selection == null) {
            if (!canMount() || this.player.hand.isEmpty()) {
                this.isDone = true;
                return;
            }
            this.selection = AbstractDungeon.handCardSelectScreen;
            this.selection.open(ThreadUiText.selectCardPrompt(ThreadOrb.MANUAL_MOUNT_COST),
                    1, false, true, false, false, true);
            CardCrawlGame.sound.play("UI_CLICK_1");
            return;
        }

        this.isDone = true;
        if (this.selection != AbstractDungeon.handCardSelectScreen || this.selection.wereCardsRetrieved) {
            return;
        }
        List<AbstractCard> selected = new ArrayList<>(this.selection.selectedCards.group);
        this.selection.selectedCards.clear();
        this.selection.wereCardsRetrieved = true;
        for (AbstractCard card : selected) {
            if (!this.player.hand.contains(card)) {
                this.player.hand.addToTop(card);
            }
        }
        if (selected.size() == 1 && canMount()) {
            CardThreadFunction function = new CardThreadFunction(selected.get(0));
            this.player.energy.use(ThreadOrb.MANUAL_MOUNT_COST);
            this.orb.mount(function, this.player);
        }
        this.player.hand.refreshHandLayout();
        this.player.hand.applyPowers();
    }

    private boolean isSameCombat() {
        return this.player != null && this.player == AbstractDungeon.player && !this.player.isDeadOrEscaped()
                && AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom() == this.room
                && this.room.phase == AbstractRoom.RoomPhase.COMBAT
                && !this.room.monsters.areMonstersBasicallyDead();
    }

    private boolean canMount() {
        return !this.player.endTurnQueued && !this.player.isEndingTurn
                && this.player.orbs.contains(this.orb) && this.orb.isMutable() && this.orb.isEmpty()
                && EnergyPanel.totalCount >= ThreadOrb.MANUAL_MOUNT_COST;
    }

    private void cancelSelection() {
        // Never consume a selection belonging to a later room or another player.
        if (this.selection == null || this.selection != AbstractDungeon.handCardSelectScreen
                || this.player != AbstractDungeon.player || AbstractDungeon.currMapNode == null
                || AbstractDungeon.getCurrRoom() != this.room || this.selection.wereCardsRetrieved) {
            return;
        }
        this.selection.selectedCards.clear();
        this.selection.wereCardsRetrieved = true;
        if (AbstractDungeon.isScreenUp && AbstractDungeon.screen == AbstractDungeon.CurrentScreen.HAND_SELECT) {
            AbstractDungeon.closeCurrentScreen();
        }
    }
}
