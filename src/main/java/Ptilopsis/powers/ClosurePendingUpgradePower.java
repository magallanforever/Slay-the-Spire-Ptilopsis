package Ptilopsis.powers;

import Ptilopsis.Ptilopsis;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import java.util.ArrayList;
import java.util.List;

public class ClosurePendingUpgradePower extends AbstractPower {
    public static final String POWER_ID = Ptilopsis.makeID("ClosurePendingUpgradePower");

    public ClosurePendingUpgradePower(AbstractPlayer owner, int amount) {
        this.ID = POWER_ID;
        this.name = getPowerStrings().NAME;
        this.owner = owner;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        this.amount = amount;
        loadRegion("master_reality");
        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        this.amount += stackAmount;
        updateDescription();
    }

    @Override
    public void onVictory() {
        for (int i = 0; i < this.amount; i++) {
            AbstractCard card = randomUpgradeableCardInMasterDeck();
            if (card == null) {
                return;
            }
            card.upgrade();
            AbstractDungeon.player.bottledCardUpgradeCheck(card);
            card.superFlash();
            if (AbstractDungeon.topLevelEffects != null) {
                AbstractDungeon.topLevelEffects.add(new ShowCardBrieflyEffect(card.makeStatEquivalentCopy()));
            }
        }
    }

    @Override
    public void updateDescription() {
        PowerStrings strings = getPowerStrings();
        this.description = strings.DESCRIPTIONS[0] + this.amount + strings.DESCRIPTIONS[1];
    }

    private AbstractCard randomUpgradeableCardInMasterDeck() {
        if (!(this.owner instanceof AbstractPlayer)) {
            return null;
        }

        AbstractPlayer player = (AbstractPlayer) this.owner;
        if (player.masterDeck == null) {
            return null;
        }

        List<AbstractCard> candidates = new ArrayList<>();
        for (AbstractCard card : player.masterDeck.group) {
            if (card.canUpgrade()) {
                candidates.add(card);
            }
        }
        if (candidates.isEmpty()) {
            return null;
        }

        int index = AbstractDungeon.cardRandomRng == null
                ? MathUtils.random(candidates.size() - 1)
                : AbstractDungeon.cardRandomRng.random(candidates.size() - 1);
        return candidates.get(index);
    }

    private static PowerStrings getPowerStrings() {
        return CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    }
}
