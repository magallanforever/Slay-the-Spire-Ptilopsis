package Ptilopsis.potions;

import Ptilopsis.potions.SourcePrivateKeyEvaluator.Candidate;
import Ptilopsis.potions.SourcePrivateKeyEvaluator.Context;
import Ptilopsis.potions.SourcePrivateKeyEvaluator.ScoredCard;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SourcePrivateKeyEvaluatorTest {
    public static void main(String[] args) {
        Context lethal = baseline();
        lethal.health = 12;
        lethal.incomingDamage = 48;
        lethal.attackHits = 4;
        expectFirst(lethal, Candidate.WRAITH_FORM, "lethal multi-hit attack");

        Context draw = baseline();
        draw.futureTurns = 0;
        draw.handSpace = 8;
        expectFirst(draw, Candidate.SCRAWL, "empty hand with energy");
        draw.noDraw = true;
        expectExcluded(draw, Candidate.SCRAWL, "draw disabled");
        draw.noDraw = false;
        draw.handSpace = 0;
        expectExcluded(draw, Candidate.SCRAWL, "full hand");
        draw.handSpace = 8;
        draw.drawableCards = 0;
        expectExcluded(draw, Candidate.SCRAWL, "empty draw and discard piles");

        Context retain = baseline();
        retain.pyramid = true;
        expectExcluded(retain, Candidate.WELL_LAID_PLANS, "Runic Pyramid");

        Context attacks = baseline();
        attacks.futureTurns = 6;
        attacks.attackDensity = 0.9;
        expectFirst(attacks, Candidate.DEMON_FORM, "long attack-heavy combat");
        attacks.attackDensity = 0;
        expectExcluded(attacks, Candidate.DEMON_FORM, "no attacks in circulation");

        Context expensive = baseline();
        expensive.futureTurns = 6;
        expensive.expensiveDensity = 0.9;
        expensive.unaffordableCards = 4;
        expectFirst(expensive, Candidate.DEVA_FORM, "expensive deck in long combat");

        Context echo = baseline();
        echo.futureTurns = 6;
        echo.attackDensity = 0.1;
        expectFirst(echo, Candidate.ECHO_FORM, "long combat with few attacks");

        double unprotected = score(lethal, Candidate.WRAITH_FORM);
        lethal.intangibleTurns = 2;
        check(score(lethal, Candidate.WRAITH_FORM) < unprotected,
                "existing Intangible should reduce Wraith Form value");
        expectExcluded(lethal, Candidate.WRAITH_FORM, "already protected by Intangible");

        Context blocked = baseline();
        blocked.incomingDamage = 30;
        blocked.attackHits = 3;
        double exposed = score(blocked, Candidate.WRAITH_FORM);
        blocked.block = 30;
        check(score(blocked, Candidate.WRAITH_FORM) < exposed, "Block should reduce immediate danger");

        Context energy = baseline();
        double canSpendDraw = score(energy, Candidate.SCRAWL);
        energy.energy = 0;
        check(score(energy, Candidate.SCRAWL) < canSpendDraw, "no energy should reduce immediate draw value");

        Context stacked = baseline();
        double originalEcho = score(stacked, Candidate.ECHO_FORM);
        stacked.echoForms = 1;
        check(score(stacked, Candidate.ECHO_FORM) < originalEcho, "existing Echo Form should reduce marginal value");

        for (Context context : new Context[]{lethal, draw, retain, attacks, expensive, echo, blocked, energy}) {
            List<ScoredCard> first = SourcePrivateKeyEvaluator.rank(context);
            List<ScoredCard> second = SourcePrivateKeyEvaluator.rank(context);
            Set<String> ids = new HashSet<>();
            check(first.size() == 6, "all six candidates must be evaluated");
            for (int i = 0; i < first.size(); i++) {
                ScoredCard card = first.get(i);
                check(ids.add(card.cardId), "duplicate candidate");
                check(Double.isFinite(card.score), "non-finite score");
                check(card.cardId.equals(second.get(i).cardId), "ranking must be deterministic");
                if (i > 0) {
                    check(first.get(i - 1).score >= card.score, "offers must be ranked by descending score");
                }
            }
        }
        System.out.println("source_private_key_evaluator_ok");
    }

    private static Context baseline() {
        Context c = new Context();
        c.health = 60;
        c.handSpace = 3;
        c.drawableCards = 12;
        c.energy = 3;
        c.retainableCards = 3;
        c.attackDensity = 0.4;
        c.expensiveDensity = 0.2;
        c.futureTurns = 2;
        return c;
    }

    private static double score(Context c, Candidate candidate) {
        return SourcePrivateKeyEvaluator.rank(c).stream()
                .filter(card -> card.cardId.equals(candidate.cardId)).findFirst().get().score;
    }

    private static void expectFirst(Context c, Candidate candidate, String scenario) {
        check(SourcePrivateKeyEvaluator.rank(c).get(0).cardId.equals(candidate.cardId), scenario);
    }

    private static void expectExcluded(Context c, Candidate candidate, String scenario) {
        check(SourcePrivateKeyEvaluator.rank(c).subList(0, 3).stream()
                .noneMatch(card -> card.cardId.equals(candidate.cardId)), scenario);
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
