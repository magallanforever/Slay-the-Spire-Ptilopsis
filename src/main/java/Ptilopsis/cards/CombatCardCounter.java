package Ptilopsis.cards;

import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

final class CombatCardCounter {
    private CombatCardCounter() {
    }

    static boolean isCombatActive() {
        return AbstractDungeon.actionManager != null
                && AbstractDungeon.currMapNode != null
                && AbstractDungeon.getCurrRoom() != null
                && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT;
    }

    static int playedThisCombat() {
        if (!isCombatActive()) {
            return 0;
        }
        return AbstractDungeon.actionManager.cardsPlayedThisCombat.size();
    }
}
