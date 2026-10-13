package Ptilopsis.patches;

import Ptilopsis.powers.FreePowerCardsPower;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

@SpirePatch(clz = AbstractCard.class, method = "freeToPlay")
public class FreePowerCardsPatch {
    @SpirePostfixPatch
    public static boolean Postfix(boolean __result, AbstractCard __instance) {
        return __result || (AbstractDungeon.currMapNode != null
                && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT
                && FreePowerCardsPower.grantsFreePlay(AbstractDungeon.player, __instance));
    }
}
