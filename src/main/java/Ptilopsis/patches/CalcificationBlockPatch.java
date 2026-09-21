package Ptilopsis.patches;

import Ptilopsis.threads.ThreadOrb;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.orbs.AbstractOrb;

@SpirePatch(clz = AbstractCreature.class, method = "addBlock")
public class CalcificationBlockPatch {
    @SpirePrefixPatch
    public static void Prefix(AbstractCreature __instance, @ByRef int[] blockAmount) {
        if (!(__instance instanceof AbstractPlayer) || blockAmount[0] <= 0) {
            return;
        }
        // The orb owns the bonus, so removing it immediately removes its contribution.
        for (AbstractOrb orb : ((AbstractPlayer) __instance).orbs) {
            if (orb instanceof ThreadOrb) {
                blockAmount[0] += ((ThreadOrb) orb).getBlockGainBonus();
            }
        }
    }
}
