package Ptilopsis.powers;

import Ptilopsis.Ptilopsis;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class StunPower extends AbstractPower {
    public static final String POWER_ID = Ptilopsis.makeID("StunPower");
    private EnemyMoveInfo savedMove;
    private EnemyMoveInfo stunnedMove;

    public StunPower(AbstractMonster owner) {
        this.ID = POWER_ID;
        this.name = CardCrawlGame.languagePack.getPowerStrings(POWER_ID).NAME;
        this.owner = owner;
        this.amount = 1;
        this.type = PowerType.DEBUFF;
        this.isTurnBased = true;
        loadRegion("entangle");
        updateDescription();
    }

    @Override
    public void onInitialApplication() {
        AbstractMonster monster = (AbstractMonster) this.owner;
        this.savedMove = ReflectionHacks.getPrivate(monster, AbstractMonster.class, "move");
        this.stunnedMove = new EnemyMoveInfo(monster.nextMove, AbstractMonster.Intent.STUN, -1, 0, false);
        // Do not add a fake move to the monster's AI history.
        ReflectionHacks.setPrivate(monster, AbstractMonster.class, "move", this.stunnedMove);
        monster.createIntent();
    }

    public static boolean allowTurn(AbstractMonster monster) {
        AbstractPower power = monster.getPower(POWER_ID);
        if (!(power instanceof StunPower) || power.amount <= 0) {
            return true;
        }
        power.flash();
        AbstractDungeon.actionManager.addToBottom(new ReducePowerAction(monster, monster, POWER_ID, 1));
        return false;
    }

    @Override
    public void onRemove() {
        AbstractMonster monster = (AbstractMonster) this.owner;
        EnemyMoveInfo current = ReflectionHacks.getPrivate(monster, AbstractMonster.class, "move");
        // Preserve a new move chosen by a phase change or another effect during the stun.
        if (this.savedMove != null && current == this.stunnedMove && !monster.isDeadOrEscaped()) {
            ReflectionHacks.setPrivate(monster, AbstractMonster.class, "move", this.savedMove);
            monster.createIntent();
        }
    }

    @Override
    public void updateDescription() {
        String[] text = CardCrawlGame.languagePack.getPowerStrings(POWER_ID).DESCRIPTIONS;
        this.description = text[0] + this.amount + text[1];
    }
}
