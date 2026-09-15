package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MergeSort extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(MergeSort.class.getSimpleName());

    public MergeSort() {
        super(ID, 1, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF);
        this.block = this.baseBlock = 1;
        this.magicNumber = this.baseMagicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        int inversions = DrawPileOrder.sortAndCountInversions(player.drawPile.group, MergeSort::effectiveCost);
        player.drawPile.applyPowers();

        if (inversions > 0) {
            addToBot(new GainBlockAction(player, player, inversions * this.block));
        }
        if (this.upgraded) {
            drawCards(player, this.magicNumber);
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = CardCrawlGame.languagePack.getCardStrings(ID).UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }

    private static int effectiveCost(AbstractCard card) {
        if (card.costForTurn >= 0) {
            return card.costForTurn;
        }
        if (card.cost >= 0) {
            return card.cost;
        }
        return Integer.MAX_VALUE;
    }
}
