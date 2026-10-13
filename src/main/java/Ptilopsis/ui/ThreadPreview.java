package Ptilopsis.ui;

import Ptilopsis.threads.ThreadFunction;
import basemod.helpers.CardPowerTip;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.PowerTip;
import java.util.ArrayList;
import java.util.List;

/** Owns hover-only copies; descriptions and playback use the same effect factories. */
public final class ThreadPreview {
    private final ThreadFunction function;
    private final boolean mutable;
    private final List<AbstractCard> constructCards;
    private final List<AbstractCard> functionCards;
    private final List<AbstractCard> destructCards;
    private int index;

    public ThreadPreview(ThreadFunction function, boolean mutable) {
        this.function = function;
        this.mutable = mutable;
        this.constructCards = function.makeConstructPreviewCards();
        this.functionCards = function.makePreviewCards();
        this.destructCards = function.makeDestructPreviewCards();
    }

    public ArrayList<PowerTip> makeTips(int scroll) {
        boolean showCard = this.function.showsCardPreview() && !this.functionCards.isEmpty();
        String effect = showCard ? ThreadUiText.replayCards(this.functionCards.size()) : describe(this.functionCards);
        if (this.mutable && this.function.expiresAtEndOfTurn()) {
            effect += ThreadUiText.expiresAfterPlayback();
        }
        String body = ThreadUiText.effectsBody(describe(this.constructCards), effect,
                describe(this.destructCards), this.function.runsAtStartOfTurn());
        String note = this.function.getPreviewNote();
        if (!note.isEmpty()) {
            body += " NL " + plainKeywords(note);
        }
        String title = this.mutable ? ThreadUiText.mutableTitle() : this.function.getDisplayName();
        ArrayList<PowerTip> tips = new ArrayList<>();
        if (showCard) {
            int count = this.functionCards.size();
            this.index = Math.floorMod(this.index + scroll, count);
            AbstractCard card = this.functionCards.get(this.index);
            card.applyPowers();
            card.freeToPlayOnce = true;
            if (count > 1) {
                body += " NL " + ThreadUiText.previewPosition(this.index, count);
            }
            tips.add(new CardPowerTip(card, title, body));
        } else {
            tips.add(new PowerTip(title, body));
        }
        return tips;
    }

    private static String describe(List<AbstractCard> cards) {
        List<String> descriptions = new ArrayList<>();
        for (AbstractCard card : cards) {
            // Only fixed effect cards are summarized; mounted cards retain their native card view.
            card.applyPowers();
            descriptions.add(plainKeywords(card.rawDescription)
                    .replace("!Ptilopsis:B!", "#y" + card.block)
                    .replace("!Ptilopsis:D!", "#y" + card.damage)
                    .replace("!Ptilopsis:M!", "#y" + card.magicNumber)
                    .replace(" NL ", " "));
        }
        return String.join(" ", descriptions);
    }

    private static String plainKeywords(String text) {
        return text.replace("ptilopsis:", "").replace('_', ' ');
    }
}
