package Ptilopsis.actions;

import Ptilopsis.cards.Offline;
import Ptilopsis.threads.effects.OfflineThreadEffect;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import java.util.ArrayList;

public class OfflineAction extends AbstractGameAction {
    private final AbstractPlayer player;
    private final int maxCards;
    private final String threadName;
    private final int blockOnDestruct;
    private boolean selectionOpened;

    public OfflineAction(AbstractPlayer player, int maxCards, String threadName, int blockOnDestruct) {
        this.player = player;
        this.maxCards = maxCards;
        this.threadName = threadName;
        this.blockOnDestruct = blockOnDestruct;
        this.target = player;
        this.source = player;
        this.actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        if (this.player == null || this.player.isDeadOrEscaped()) {
            this.isDone = true;
            return;
        }

        HandCardSelectScreen screen = AbstractDungeon.handCardSelectScreen;
        if (!this.selectionOpened) {
            if (this.player.hand.isEmpty() || this.maxCards <= 0) {
                this.isDone = true;
                return;
            }

            screen.open(
                    CardCrawlGame.languagePack.getCardStrings(Offline.ID).EXTENDED_DESCRIPTION[0],
                    Math.min(this.maxCards, this.player.hand.size()),
                    false,
                    true,
                    false,
                    false,
                    true
            );
            this.selectionOpened = true;
            return;
        }

        if (AbstractDungeon.isScreenUp || screen.wereCardsRetrieved) {
            return;
        }

        ArrayList<AbstractCard> selectedCards = new ArrayList<>(screen.selectedCards.group);
        screen.selectedCards.clear();
        screen.wereCardsRetrieved = true;

        if (!selectedCards.isEmpty()) {
            OfflineThreadEffect effect = new OfflineThreadEffect(this.threadName, selectedCards, this.blockOnDestruct);
            for (AbstractCard card : selectedCards) {
                this.player.hand.moveToExhaustPile(card);
            }
            CardCrawlGame.dungeon.checkForPactAchievement();
            addToBot(new ThreadCommandAction(this.player, ThreadCommandAction.Command.INSTALL_READ_ONLY, effect));
        }

        this.player.hand.refreshHandLayout();
        this.player.hand.applyPowers();
        this.isDone = true;
    }
}
