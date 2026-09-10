package Ptilopsis.patches;

import Ptilopsis.character.PtilopsisCharacter;
import Ptilopsis.threads.ThreadOrb;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

public class ThreadOrbPatches {
    @SpirePatch(clz = AbstractPlayer.class, method = "preBattlePrep")
    public static class PreBattlePrep {
        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer player) {
            if (!(player instanceof PtilopsisCharacter)) {
                return;
            }

            while (player.maxOrbs < 3) {
                player.increaseMaxOrbSlots(1, false);
            }
            for (int i = 0; i < 3; i++) {
                if (i >= player.orbs.size()) {
                    player.orbs.add(new ThreadOrb(true));
                } else if (!(player.orbs.get(i) instanceof ThreadOrb)) {
                    player.orbs.set(i, new ThreadOrb(true));
                }
            }
            for (int i = 0; i < player.orbs.size(); i++) {
                AbstractOrb orb = player.orbs.get(i);
                orb.setSlot(i, player.maxOrbs);
            }
        }
    }

    @SpirePatch(clz = AbstractCreature.class, method = "applyEndOfTurnTriggers")
    public static class EndOfTurn {
        @SpirePostfixPatch
        public static void Postfix(AbstractCreature creature) {
            if (!(creature instanceof PtilopsisCharacter)) {
                return;
            }

            AbstractPlayer player = (AbstractPlayer) creature;
            for (AbstractOrb orb : player.orbs) {
                if (orb instanceof ThreadOrb) {
                    orb.onEndOfTurn();
                }
            }
        }
    }
}
