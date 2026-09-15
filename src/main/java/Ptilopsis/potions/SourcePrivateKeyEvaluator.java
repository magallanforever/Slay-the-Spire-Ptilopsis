package Ptilopsis.potions;

import basemod.BaseMod;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.blue.EchoForm;
import com.megacrit.cardcrawl.cards.green.WellLaidPlans;
import com.megacrit.cardcrawl.cards.green.WraithForm;
import com.megacrit.cardcrawl.cards.purple.DevaForm;
import com.megacrit.cardcrawl.cards.purple.Scrawl;
import com.megacrit.cardcrawl.cards.red.DemonForm;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.DemonFormPower;
import com.megacrit.cardcrawl.powers.EchoPower;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import com.megacrit.cardcrawl.powers.NoDrawPower;
import com.megacrit.cardcrawl.powers.RetainCardPower;
import com.megacrit.cardcrawl.powers.watcher.DevaPower;
import com.megacrit.cardcrawl.relics.RunicDome;
import com.megacrit.cardcrawl.relics.RunicPyramid;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class SourcePrivateKeyEvaluator {
    private SourcePrivateKeyEvaluator() {
    }

    // One entry owns each candidate's ID and valuation. Ties keep this catalog order.
    enum Candidate {
        SCRAWL(Scrawl.ID) {
            double score(Context c) {
                int draw = Math.min(c.handSpace, c.drawableCards);
                return c.noDraw || draw == 0 ? -100 : draw * (c.energy > 0 ? 9 + 2 * Math.min(c.energy, 4) : 2);
            }
        },
        WRAITH_FORM(WraithForm.ID) {
            double score(Context c) {
                int incoming = c.intangibleTurns > 0 ? c.attackHits : c.incomingDamage;
                double loss = Math.max(0, incoming - c.block);
                double prevented = Math.max(0, loss - Math.max(0, c.attackHits - c.block));
                double danger = loss / Math.max(1, c.health);
                return 12 + 2 * prevented + 45 * Math.min(2, danger)
                        + (loss >= c.health ? 100 : 0) + (c.intentsHidden ? 15 : 0)
                        - (c.intangibleTurns >= 2 ? 100 : c.intangibleTurns > 0 ? 60 : 0);
            }
        },
        WELL_LAID_PLANS(WellLaidPlans.ID) {
            double score(Context c) {
                if (c.pyramid) {
                    return -100;
                }
                int retainable = Math.max(0, c.retainableCards - c.retention);
                return 5 + c.futureTurns * 3 + Math.min(4, retainable) * 5
                        + Math.min(3, c.unaffordableCards) * 4 - c.retention * 20;
            }
        },
        DEMON_FORM(DemonForm.ID) {
            double score(Context c) {
                return c.attackDensity == 0 ? -80
                        : 6 + c.futureTurns * (6 + 18 * c.attackDensity) - c.demonStrength * 3;
            }
        },
        DEVA_FORM(DevaForm.ID) {
            double score(Context c) {
                return 5 + c.futureTurns * (5 + 20 * c.expensiveDensity)
                        + Math.min(4, c.unaffordableCards) * 3 - c.devaForms * 25;
            }
        },
        ECHO_FORM(EchoForm.ID) {
            double score(Context c) {
                return 10 + c.futureTurns * (10 + 8 * c.expensiveDensity) - c.echoForms * 20;
            }
        };

        final String cardId;

        Candidate(String cardId) {
            this.cardId = cardId;
        }

        abstract double score(Context context);
    }

    public static final class ScoredCard {
        public final String cardId;
        public final double score;

        private ScoredCard(String cardId, double score) {
            this.cardId = cardId;
            this.score = score;
        }
    }

    // A use-time snapshot; scoring itself does not access the dungeon or mutate cards.
    static final class Context {
        int health;
        int block;
        int incomingDamage;
        int attackHits;
        int handSpace;
        int drawableCards;
        int energy;
        int retainableCards;
        int unaffordableCards;
        int retention;
        int intangibleTurns;
        int demonStrength;
        int devaForms;
        int echoForms;
        double futureTurns;
        double attackDensity;
        double expensiveDensity;
        boolean noDraw;
        boolean pyramid;
        boolean intentsHidden;
    }

    public static List<ScoredCard> evaluate(AbstractPlayer player) {
        return rank(capture(player));
    }

    static List<ScoredCard> rank(Context context) {
        List<ScoredCard> scores = new ArrayList<>();
        for (Candidate candidate : Candidate.values()) {
            scores.add(new ScoredCard(candidate.cardId, candidate.score(context)));
        }
        scores.sort(Comparator.comparingDouble((ScoredCard c) -> c.score).reversed());
        return scores;
    }

    private static Context capture(AbstractPlayer player) {
        Context c = new Context();
        c.health = player.currentHealth;
        c.block = player.currentBlock;
        c.energy = EnergyPanel.totalCount;
        c.handSpace = Math.max(0, BaseMod.MAX_HAND_SIZE - player.hand.size());
        c.drawableCards = player.drawPile.size() + player.discardPile.size();
        c.noDraw = player.hasPower(NoDrawPower.POWER_ID);
        c.pyramid = player.hasRelic(RunicPyramid.ID);
        c.intentsHidden = player.hasRelic(RunicDome.ID);
        c.retention = powerAmount(player, RetainCardPower.POWER_ID);
        c.intangibleTurns = powerAmount(player, IntangiblePlayerPower.POWER_ID);
        c.demonStrength = powerAmount(player, DemonFormPower.POWER_ID);
        c.devaForms = powerAmount(player, DevaPower.POWER_ID);
        c.echoForms = powerAmount(player, EchoPower.POWER_ID);

        List<AbstractCard> cards = new ArrayList<>();
        cards.addAll(player.hand.group);
        cards.addAll(player.drawPile.group);
        cards.addAll(player.discardPile.group);
        int attacks = 0;
        int expensive = 0;
        int attackDamage = 0;
        for (AbstractCard card : cards) {
            if (card.type == AbstractCard.CardType.ATTACK) {
                attacks++;
                attackDamage += Math.max(0, card.baseDamage);
            }
            if (card.cost >= 2) {
                expensive++;
            }
        }
        for (AbstractCard card : player.hand.group) {
            if (card.costForTurn >= 0 && !card.isEthereal && !card.selfRetain && !card.retain) {
                c.retainableCards++;
                if (card.costForTurn > c.energy && !card.freeToPlayOnce) {
                    c.unaffordableCards++;
                }
            }
        }
        c.attackDensity = attacks / (double) Math.max(1, cards.size());
        c.expensiveDensity = expensive / (double) Math.max(1, cards.size());
        int enemyHealth = 0;
        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            if (monster.isDeadOrEscaped() || monster.isDying) {
                continue;
            }
            enemyHealth += monster.currentHealth + monster.currentBlock;
            if (!c.intentsHidden && (monster.intent == AbstractMonster.Intent.ATTACK
                    || monster.intent == AbstractMonster.Intent.ATTACK_BUFF
                    || monster.intent == AbstractMonster.Intent.ATTACK_DEBUFF
                    || monster.intent == AbstractMonster.Intent.ATTACK_DEFEND)) {
                // The public damage getter is per hit; the engine keeps hit count private.
                boolean multi = ReflectionHacks.getPrivate(monster, AbstractMonster.class, "isMultiDmg");
                int hits = multi ? ReflectionHacks.<Integer>getPrivate(monster, AbstractMonster.class, "intentMultiAmt") : 1;
                hits = Math.max(1, hits);
                c.attackHits += hits;
                c.incomingDamage += Math.max(0, monster.getIntentDmg()) * hits;
            }
        }
        double damagePerTurn = Math.max(8, attackDamage * 5.0 / Math.max(1, cards.size()));
        // Long-term powers only pay off after this turn; this is a bounded estimate, not a simulation.
        c.futureTurns = Math.max(0, Math.min(6, Math.ceil(enemyHealth / damagePerTurn) - 1));
        return c;
    }

    private static int powerAmount(AbstractPlayer player, String id) {
        return player.hasPower(id) ? Math.max(0, player.getPower(id).amount) : 0;
    }
}
