package Ptilopsis.actions;

import Ptilopsis.potions.SourcePrivateKey;
import Ptilopsis.potions.SourcePrivateKeyEvaluator;
import Ptilopsis.potions.SourcePrivateKeyEvaluator.ScoredCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.powers.watcher.MasterRealityPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SourcePrivateKeyAction extends AbstractGameAction {
    private static final Logger LOGGER = LogManager.getLogger(SourcePrivateKeyAction.class);
    private boolean opened;

    public SourcePrivateKeyAction(int copies) {
        amount = copies;
        actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        if (AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT
                || AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            isDone = true;
            return;
        }

        if (!opened) {
            List<ScoredCard> ranked = SourcePrivateKeyEvaluator.evaluate(AbstractDungeon.player);
            ArrayList<AbstractCard> choices = new ArrayList<>();
            for (ScoredCard offer : ranked) {
                LOGGER.info("SourcePrivateKey candidate=" + offer.cardId + " score=" + offer.score);
                if (choices.size() < 3) {
                    AbstractCard card = CardLibrary.getCard(offer.cardId).makeCopy();
                    if (AbstractDungeon.player.hasPower(MasterRealityPower.POWER_ID)) {
                        card.upgrade();
                    }
                    card.setCostForTurn(0);
                    choices.add(card);
                }
            }
            AbstractDungeon.cardRewardScreen.customCombatOpen(choices,
                    CardCrawlGame.languagePack.getPotionString(SourcePrivateKey.ID).DESCRIPTIONS[2], false);
            opened = true;
            return;
        }

        if (AbstractDungeon.isScreenUp) {
            return;
        }
        AbstractCard selected = AbstractDungeon.cardRewardScreen.discoveryCard;
        if (selected != null) {
            AbstractCard card = selected.makeStatEquivalentCopy();
            MakeTempCardInHandAction gainCard = new MakeTempCardInHandAction(card, amount);
            card.setCostForTurn(0);
            addToTop(gainCard);
            AbstractDungeon.cardRewardScreen.discoveryCard = null;
        }
        isDone = true;
    }
}
