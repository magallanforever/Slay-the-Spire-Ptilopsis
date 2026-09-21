package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.powers.MasterTheoremPower;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.unique.DoubleYourBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/** Temporary effect cards are not registered in the reward or generation pools. */
public final class ThreadEffectCard extends AbstractPtilopsisCard {
    public enum Effect {
        GUARD("CacheBarrier"), DRAW("WakeSignal"), RECOVER("EnergyRecovery"), PULSE("MedicalPulse"),
        CALCIFICATION("CalcificationGuard"), DOUBLE_BLOCK("CalcificationRelease"),
        THEOREM("GradientStep"), THEOREM_ALL("GradientRelease");

        private final String id;

        Effect(String id) {
            this.id = id;
        }
    }

    private final Effect effect;

    public ThreadEffectCard(Effect effect, int amount) {
        super(Ptilopsis.makeID(effect.id), 0,
                effect == Effect.PULSE ? CardType.ATTACK : CardType.SKILL, CardRarity.SPECIAL,
                targetFor(effect));
        this.effect = effect;
        this.baseBlock = this.block = effect == Effect.GUARD || effect == Effect.CALCIFICATION ? amount : 0;
        this.baseMagicNumber = this.magicNumber = amount;
        this.baseDamage = this.damage = effect == Effect.PULSE ? amount : 0;
        this.isMultiDamage = effect == Effect.PULSE;
        this.purgeOnUse = true;
        if (effect == Effect.CALCIFICATION && amount == 5) {
            upgradeName();
        }
    }

    private static CardTarget targetFor(Effect effect) {
        if (effect == Effect.THEOREM) {
            return CardTarget.ENEMY;
        }
        if (effect == Effect.PULSE || effect == Effect.THEOREM_ALL) {
            return CardTarget.ALL_ENEMY;
        }
        return CardTarget.SELF;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        if (this.effect == Effect.PULSE) {
            addToBot(new DamageAllEnemiesAction(player, this.multiDamage, this.damageTypeForTurn,
                    AbstractGameAction.AttackEffect.FIRE));
        } else if (this.effect == Effect.THEOREM) {
            applyTheorem(player, monster);
        } else if (this.effect == Effect.THEOREM_ALL) {
            for (AbstractMonster enemy : AbstractDungeon.getMonsters().monsters) {
                applyTheorem(player, enemy);
            }
        } else if (this.effect == Effect.DOUBLE_BLOCK) {
            addToBot(new DoubleYourBlockAction(player));
        } else if (this.effect == Effect.GUARD || this.effect == Effect.CALCIFICATION) {
            gainBlock(player);
        } else {
            if (this.effect == Effect.RECOVER) {
                gainEnergy(1);
            }
            drawCards(player);
        }
    }

    private void applyTheorem(AbstractPlayer player, AbstractMonster monster) {
        if (monster != null && !monster.isDeadOrEscaped()) {
            addToBot(new ApplyPowerAction(monster, player,
                    new MasterTheoremPower(monster, player, this.magicNumber), this.magicNumber));
        }
    }

    @Override
    public void upgrade() {
    }

    @Override
    public AbstractCard makeCopy() {
        return new ThreadEffectCard(this.effect, this.baseMagicNumber);
    }
}
