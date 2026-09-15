package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

/** Temporary effect cards are not registered in the reward or generation pools. */
public final class ThreadEffectCard extends AbstractPtilopsisCard {
    public enum Effect {
        GUARD("CacheBarrier"), DRAW("WakeSignal"), RECOVER("EnergyRecovery"), PULSE("MedicalPulse");

        private final String id;

        Effect(String id) {
            this.id = id;
        }
    }

    private final Effect effect;

    public ThreadEffectCard(Effect effect, int amount) {
        super(Ptilopsis.makeID(effect.id), 0,
                effect == Effect.PULSE ? CardType.ATTACK : CardType.SKILL, CardRarity.SPECIAL,
                effect == Effect.PULSE ? CardTarget.ALL_ENEMY : CardTarget.SELF);
        this.effect = effect;
        this.baseBlock = this.block = effect == Effect.GUARD ? amount : 0;
        this.baseMagicNumber = this.magicNumber = amount;
        this.baseDamage = this.damage = effect == Effect.PULSE ? amount : 0;
        this.isMultiDamage = effect == Effect.PULSE;
        this.purgeOnUse = true;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        if (this.effect == Effect.PULSE) {
            addToBot(new DamageAllEnemiesAction(player, this.multiDamage, this.damageTypeForTurn,
                    AbstractGameAction.AttackEffect.FIRE));
        } else if (this.effect == Effect.GUARD) {
            gainBlock(player);
        } else {
            if (this.effect == Effect.RECOVER) {
                gainEnergy(1);
            }
            drawCards(player);
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
