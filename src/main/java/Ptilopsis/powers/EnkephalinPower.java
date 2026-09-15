package Ptilopsis.powers;

import Ptilopsis.Ptilopsis;
import Ptilopsis.actions.ThreadCommandAction;
import Ptilopsis.threads.effects.CardThreadFunction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class EnkephalinPower extends AbstractPower {
    public static final String POWER_ID = Ptilopsis.makeID("EnkephalinPower");

    private int remainingThisTurn;

    public EnkephalinPower(AbstractPlayer owner, int amount) {
        this.ID = POWER_ID;
        this.name = getPowerStrings().NAME;
        this.owner = owner;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        this.amount = amount;
        this.remainingThisTurn = amount;
        loadRegion("draw");
        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        super.stackPower(stackAmount);
        this.remainingThisTurn += stackAmount;
        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        this.remainingThisTurn = this.amount;
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        if (this.remainingThisTurn <= 0 || card.isInAutoplay || card.purgeOnUse) {
            return;
        }

        this.remainingThisTurn--;
        flash();
        AbstractPlayer player = (AbstractPlayer) this.owner;
        AbstractDungeon.actionManager.addToBottom(new ThreadCommandAction(
                player,
                ThreadCommandAction.Command.MOUNT_MUTABLE,
                new CardThreadFunction(card)
        ));
        AbstractDungeon.actionManager.addToBottom(new DrawCardAction(player, 1));
    }

    @Override
    public void updateDescription() {
        PowerStrings strings = getPowerStrings();
        this.description = strings.DESCRIPTIONS[0]
                + this.amount
                + strings.DESCRIPTIONS[1];
    }

    private static PowerStrings getPowerStrings() {
        return CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    }
}
