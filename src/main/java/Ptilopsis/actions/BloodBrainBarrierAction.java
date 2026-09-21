package Ptilopsis.actions;

import Ptilopsis.powers.MasterTheoremPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import java.util.ArrayList;

public class BloodBrainBarrierAction extends AbstractGameAction {
    private final AbstractPlayer player;

    public BloodBrainBarrierAction(AbstractPlayer player) {
        this.player = player;
        this.source = player;
        this.actionType = ActionType.DAMAGE;
    }

    @Override
    public void update() {
        if (this.isDone) {
            return;
        }
        this.isDone = true;
        if (this.player.isDeadOrEscaped()) {
            return;
        }

        int totalDamage = 0;
        for (AbstractMonster enemy : new ArrayList<>(AbstractDungeon.getMonsters().monsters)) {
            AbstractPower theorem = enemy.getPower(MasterTheoremPower.POWER_ID);
            if (enemy.isDeadOrEscaped() || enemy.currentHealth <= 0
                    || !(theorem instanceof MasterTheoremPower) || theorem.amount <= 0) {
                continue;
            }
            theorem.flashWithoutSound();
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(enemy.hb.cX, enemy.hb.cY, AttackEffect.POISON));
            enemy.damage(new DamageInfo(this.player, theorem.amount, DamageInfo.DamageType.HP_LOSS));
            totalDamage += Math.max(0, enemy.lastDamageTaken);
            ((MasterTheoremPower) theorem).decay();
        }
        // Use the resolved damage, not the stack count; grant Block once, even on a lethal hit.
        if (totalDamage > 0 && !this.player.isDeadOrEscaped()) {
            this.player.addBlock(totalDamage);
        }
        if (AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
            AbstractDungeon.actionManager.clearPostCombatActions();
        }
    }
}
