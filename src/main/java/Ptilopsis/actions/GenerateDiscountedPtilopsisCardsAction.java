package Ptilopsis.actions;

import Ptilopsis.character.PtilopsisCharacter;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import java.util.ArrayList;

public class GenerateDiscountedPtilopsisCardsAction extends AbstractGameAction {
    private final int cardCount;

    public GenerateDiscountedPtilopsisCardsAction(AbstractCreature source, int cardCount) {
        this.source = source;
        this.cardCount = cardCount;
        this.actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        ArrayList<AbstractCard> cards = CardLibrary.getCardList(PtilopsisCharacter.LibraryEnums.PTILOPSIS_COLOR);
        if (cards == null || cards.isEmpty()) {
            this.isDone = true;
            return;
        }

        for (int i = 0; i < this.cardCount; i++) {
            AbstractCard card = cards.get(AbstractDungeon.cardRandomRng.random(cards.size() - 1)).makeStatEquivalentCopy();
            card.modifyCostForCombat(-card.cost);
            card.exhaust = true;
            addToTop(new MakeTempCardInHandAction(card, true));
        }

        this.isDone = true;
    }
}
