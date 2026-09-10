package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Muelsyse extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(Muelsyse.class.getSimpleName());
    private static final int BASE_COST_MASK = 15;
    private static final int UPGRADE_COST_MASK = 7;
    private static final int HEAL = 25;

    public Muelsyse() {
        super(ID, BASE_COST_MASK, CardType.SKILL, CardRarity.RARE, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = HEAL;
        this.exhaust = true;
        configureCostsOnNewCard();
        addRightMomentKeyword();
    }

    @Override
    public void initializeDescription() {
        super.initializeDescription();
        addRightMomentKeyword();
    }

    @Override
    public boolean canUpgrade() {
        return !this.upgraded;
    }

    @Override
    public void onPlayCard(AbstractCard card, AbstractMonster monster) {
        if (card != this && CombatCardCounter.isCombatActive()) {
            setCostForOperationCount(CombatCardCounter.playedThisCombat() + 1);
        }
    }

    @Override
    public void triggerWhenDrawn() {
        configureCostsOnNewCard();
    }

    @Override
    public void applyPowers() {
        super.applyPowers();
        configureCostsOnNewCard();
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new HealAction(player, player, this.magicNumber));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(UPGRADE_COST_MASK);
            configureCostsOnNewCard();
            initializeDescription();
        }
    }

    private void configureCostsOnNewCard() {
        setCostForOperationCount(CombatCardCounter.playedThisCombat());
    }

    private void setCostForOperationCount(int operationCount) {
        updateCost(costForOperationCount(operationCount, this.upgraded) - this.cost);
    }

    public static int costForOperationCount(int operationCount) {
        return costForOperationCount(operationCount, false);
    }

    public static int costForOperationCount(int operationCount, boolean upgraded) {
        int mask = upgraded ? UPGRADE_COST_MASK : BASE_COST_MASK;
        return (operationCount ^ mask) & mask;
    }

    private void addRightMomentKeyword() {
        if (this.keywords == null) {
            return;
        }

        String keyword = Settings.language == Settings.GameLanguage.ZHS
                ? Ptilopsis.keywordID("恰当的时候")
                : Ptilopsis.keywordID("right_moment");
        if (!this.keywords.contains(keyword)) {
            this.keywords.add(keyword);
        }
    }
}
