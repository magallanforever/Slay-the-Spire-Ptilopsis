package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import com.megacrit.cardcrawl.actions.watcher.SkipEnemiesTurnAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Magallan extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(Magallan.class.getSimpleName());
    private static final int COST = 7;
    private static final int UPGRADE_COST = 5;

    public Magallan() {
        super(ID, COST, CardType.SKILL, CardRarity.RARE, CardTarget.SELF);
        this.selfRetain = true;
        this.exhaust = true;
        configureCostsOnNewCard();
    }

    @Override
    public void onPlayCard(AbstractCard card, AbstractMonster monster) {
        if (card != this && CombatCardCounter.isCombatActive()) {
            updateCost(-1);
        }
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new SkipEnemiesTurnAction());
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(UPGRADE_COST);
            configureCostsOnNewCard();
        }
    }

    private void configureCostsOnNewCard() {
        if (CombatCardCounter.isCombatActive()) {
            updateCost(-CombatCardCounter.playedThisCombat());
        }
    }
}
