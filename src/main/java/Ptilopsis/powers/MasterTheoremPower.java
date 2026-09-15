package Ptilopsis.powers;

import Ptilopsis.Ptilopsis;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

public class MasterTheoremPower extends AbstractPower {
    public static final String POWER_ID = Ptilopsis.makeID("MasterTheoremPower");

    private final AbstractCreature source;

    public MasterTheoremPower(AbstractCreature owner, AbstractCreature source, int amount) {
        this.ID = POWER_ID;
        this.name = getPowerStrings().NAME;
        this.owner = owner;
        this.source = source;
        this.type = PowerType.DEBUFF;
        this.isTurnBased = true;
        this.amount = amount;
        loadRegion("poison");
        updateDescription();
    }

    @Override
    public void atStartOfTurn() {
        if (AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT
                || AbstractDungeon.getMonsters().areMonstersBasicallyDead()
                || this.owner.isDeadOrEscaped()) {
            return;
        }

        flashWithoutSound();
        addToBot(new LoseHPAction(
                this.owner,
                this.source,
                this.amount,
                AbstractGameAction.AttackEffect.POISON
        ));
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        if (isPlayer || this.owner.isPlayer || this.amount <= 1) {
            return;
        }

        if (ExtremeDataConstructionPower.isActive(this.source)) {
            updateDescription();
            return;
        }

        flashWithoutSound();
        this.amount = (this.amount + 1) / 2;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        PowerStrings strings = getPowerStrings();
        this.description = strings.DESCRIPTIONS[0]
                + this.amount
                + strings.DESCRIPTIONS[decaySuppressed() ? 2 : 1];
    }

    private boolean decaySuppressed() {
        return ExtremeDataConstructionPower.isActive(this.source);
    }

    private static PowerStrings getPowerStrings() {
        return CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    }
}
