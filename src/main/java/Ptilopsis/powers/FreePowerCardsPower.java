package Ptilopsis.powers;

import Ptilopsis.Ptilopsis;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

/** Swivel-style free plays, limited to Power cards and the current turn. */
public class FreePowerCardsPower extends AbstractPower {
    public static final String POWER_ID = Ptilopsis.makeID("FreePowerCardsPower");

    public FreePowerCardsPower(AbstractPlayer owner, int amount) {
        this.ID = POWER_ID;
        this.name = CardCrawlGame.languagePack.getPowerStrings(POWER_ID).NAME;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;
        this.isTurnBased = true;
        loadRegion("swivel");
        updateDescription();
    }

    public static boolean grantsFreePlay(AbstractPlayer player, AbstractCard card) {
        if (player == null || card.type != AbstractCard.CardType.POWER) {
            return false;
        }
        AbstractPower power = player.getPower(POWER_ID);
        return power != null && power.amount > 0;
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        // Like Swivel, a naturally free card consumes a charge; purged replays do not.
        if (card.type != AbstractCard.CardType.POWER || card.purgeOnUse || this.amount <= 0) {
            return;
        }
        // onUseCard runs before the player pays energy, including on the last charge.
        card.freeToPlayOnce = true;
        flash();
        this.amount--;
        updateDescription();
        if (this.amount == 0) {
            addToTop(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        updateDescription();
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer) {
            this.amount = 0;
            addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
        }
    }

    @Override
    public void updateDescription() {
        PowerStrings strings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
        this.description = strings.DESCRIPTIONS[0] + this.amount + strings.DESCRIPTIONS[1];
    }
}
