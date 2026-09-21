package Ptilopsis.actions;

import Ptilopsis.cards.Silence;
import Ptilopsis.character.PtilopsisCharacter;
import Ptilopsis.threads.ThreadNetwork;
import Ptilopsis.threads.effects.CardThreadFunction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.watcher.MasterRealityPower;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SilenceDraftAction extends AbstractGameAction {
    private final AbstractPlayer player;
    private final List<AbstractCard> selectedCards = new ArrayList<>();
    private boolean opened;

    public SilenceDraftAction(AbstractPlayer player) {
        this.player = player;
        this.actionType = ActionType.CARD_MANIPULATION;
    }

    @Override
    public void update() {
        if (this.isDone) {
            return;
        }
        if (this.player != AbstractDungeon.player || this.player.isDeadOrEscaped()
                || AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT
                || AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            if (this.opened) {
                AbstractDungeon.cardRewardScreen.discoveryCard = null;
            }
            this.isDone = true;
            return;
        }
        if (AbstractDungeon.isScreenUp) {
            return;
        }
        if (this.opened) {
            AbstractCard selected = AbstractDungeon.cardRewardScreen.discoveryCard;
            AbstractDungeon.cardRewardScreen.discoveryCard = null;
            this.opened = false;
            if (selected == null) {
                this.isDone = true;
                return;
            }
            this.selectedCards.add(selected.makeStatEquivalentCopy());
            if (this.selectedCards.size() == 2) {
                ThreadNetwork.forPlayer(this.player).installNewMutable(new CardThreadFunction(this.selectedCards));
                this.isDone = true;
                return;
            }
        }

        ArrayList<AbstractCard> choices = rollChoices(combatClassPool(), AbstractDungeon.cardRandomRng);
        if (choices.isEmpty()) {
            this.isDone = true;
            return;
        }
        if (this.player.hasPower(MasterRealityPower.POWER_ID)) {
            for (AbstractCard card : choices) {
                card.upgrade();
            }
        }
        String prompt = CardCrawlGame.languagePack.getCardStrings(Silence.ID).EXTENDED_DESCRIPTION[0]
                + " (" + (this.selectedCards.size() + 1) + "/2)";
        AbstractDungeon.cardRewardScreen.customCombatOpen(choices, prompt, false);
        this.opened = true;
    }

    private static List<AbstractCard> combatClassPool() {
        List<AbstractCard> cards = new ArrayList<>();
        for (CardGroup pool : new CardGroup[] {AbstractDungeon.srcCommonCardPool,
                AbstractDungeon.srcUncommonCardPool, AbstractDungeon.srcRareCardPool}) {
            if (pool != null) {
                for (AbstractCard card : pool.group) {
                    if (card.color == PtilopsisCharacter.Enums.PTILOPSIS_COLOR) {
                        cards.add(card);
                    }
                }
            }
        }
        return cards;
    }

    private static ArrayList<AbstractCard> rollChoices(List<AbstractCard> pool, Random random) {
        Map<String, AbstractCard> unique = new LinkedHashMap<>();
        if (pool != null) {
            for (AbstractCard card : pool) {
                if ((card.rarity == AbstractCard.CardRarity.COMMON
                        || card.rarity == AbstractCard.CardRarity.UNCOMMON
                        || card.rarity == AbstractCard.CardRarity.RARE)
                        && (card.type == AbstractCard.CardType.ATTACK || card.type == AbstractCard.CardType.SKILL
                        || card.type == AbstractCard.CardType.POWER)
                        && !card.hasTag(AbstractCard.CardTags.HEALING)) {
                    unique.putIfAbsent(card.cardID, card);
                }
            }
        }
        List<AbstractCard> candidates = new ArrayList<>(unique.values());
        ArrayList<AbstractCard> choices = new ArrayList<>();
        while (choices.size() < 3 && !candidates.isEmpty()) {
            choices.add(candidates.remove(random.random(candidates.size() - 1)).makeCopy());
        }
        return choices;
    }
}
