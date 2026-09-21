package Ptilopsis.patches;

import Ptilopsis.character.PtilopsisCharacter;
import Ptilopsis.threads.ThreadOrb;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

public class ThreadOrbPatches {
    @SpirePatch(clz = AbstractPlayer.class, method = "channelOrb")
    public static class ChannelOrb {
        @SpirePostfixPatch
        public static void Postfix(AbstractPlayer __instance, AbstractOrb orbToSet) {
            // A full queue schedules another channel action; construct only after insertion.
            if (orbToSet instanceof ThreadOrb && __instance.orbs.contains(orbToSet)) {
                ((ThreadOrb) orbToSet).onChannel(__instance);
            }
        }
    }

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
            for (int i = 0; i < player.orbs.size(); i++) {
                AbstractOrb orb = player.orbs.get(i);
                orb.setSlot(i, player.maxOrbs);
            }
        }
    }

}
