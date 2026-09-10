package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.powers.ClosurePendingUpgradePower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.unique.ApotheosisAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Closure extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(Closure.class.getSimpleName());
    private static final int GOLD_COST = 30;
    private static final int UPGRADED_GOLD_COST = 15;

    public Closure() {
        super(ID, 0, CardType.SKILL, CardRarity.RARE, CardTarget.SELF);
        this.magicNumber = this.baseMagicNumber = GOLD_COST;
        this.isInnate = true;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        int goldSpent = Math.min(player.gold, this.magicNumber);
        if (goldSpent > 0) {
            player.loseGold(goldSpent);
        }
        addToBot(new ApotheosisAction());
        addToBot(new ApplyPowerAction(
                player,
                player,
                new ClosurePendingUpgradePower(player, 1),
                1
        ));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeMagicNumber(UPGRADED_GOLD_COST - GOLD_COST);
        }
    }
}
