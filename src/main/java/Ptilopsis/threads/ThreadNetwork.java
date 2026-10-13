package Ptilopsis.threads;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;
import java.util.ArrayList;
import java.util.List;

public final class ThreadNetwork {
    private final AbstractPlayer owner;

    private ThreadNetwork(AbstractPlayer player) {
        this.owner = player;
    }

    public static ThreadNetwork forPlayer(AbstractPlayer player) {
        return new ThreadNetwork(player);
    }

    public static int activeThreadCount(AbstractPlayer player) {
        int count = 0;
        for (AbstractOrb orb : player.orbs) {
            if (orb instanceof ThreadOrb && !((ThreadOrb) orb).isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public static int threadCount(AbstractPlayer player) {
        int count = 0;
        for (AbstractOrb orb : player.orbs) {
            if (orb instanceof ThreadOrb) {
                count++;
            }
        }
        return count;
    }

    public void createEmptyMutableSlot() {
        getOwner().channelOrb(new ThreadOrb(true));
    }

    public void installReadOnly(ThreadFunction function) {
        getOwner().channelOrb(new ThreadOrb(false, function));
    }

    public void mountMutable(ThreadFunction function) {
        ThreadOrb orb = findEmptyMutableOrb();
        if (orb == null) {
            orb = findOccupiedMutableOrb();
        }
        if (orb == null) {
            installNewMutable(function);
            return;
        }

        orb.mount(function, getOwner());
    }

    public void installNewMutable(ThreadFunction function) {
        getOwner().channelOrb(new ThreadOrb(true, function));
    }

    public void triggerAllEndOfTurnEffects() {
        List<ThreadOrb> orbs = threadOrbs();
        if (orbs.isEmpty()) {
            return;
        }
        for (ThreadOrb orb : orbs) {
            orb.triggerEndOfTurnEffect(getOwner());
        }
    }

    public void triggerAllDestructEffects() {
        List<ThreadOrb> orbs = threadOrbs();
        if (orbs.isEmpty()) {
            return;
        }
        for (ThreadOrb orb : orbs) {
            orb.triggerDestructEffect(getOwner());
        }
    }

    public void triggerLeftmostDestructEffect() {
        List<ThreadOrb> orbs = threadOrbs();
        if (orbs.isEmpty()) {
            return;
        }
        orbs.get(0).triggerDestructEffect(getOwner());
    }

    public void triggerEdgeDestructEffects() {
        List<ThreadOrb> orbs = threadOrbs();
        if (orbs.isEmpty()) {
            return;
        }

        ThreadOrb left = orbs.get(0);
        ThreadOrb right = orbs.get(orbs.size() - 1);
        left.triggerDestructEffect(getOwner());
        right.triggerDestructEffect(getOwner());
    }

    public int destructAllThreads() {
        List<ThreadOrb> orbs = threadOrbs();
        int count = 0;
        for (ThreadOrb orb : orbs) {
            if (removeThreadOrb(orb)) {
                count++;
            }
        }
        relayoutOrbs();
        refreshCards();
        return count;
    }

    public void destructThread(ThreadOrb orb) {
        if (removeThreadOrb(orb)) {
            relayoutOrbs();
            refreshCards();
        }
    }

    private boolean removeThreadOrb(ThreadOrb orb) {
        AbstractPlayer player = getOwner();
        int index = player.orbs.indexOf(orb);
        if (index < 0) {
            return false;
        }
        // Orb updates iterate this list, so shift slots without changing its size.
        for (int i = index; i < player.orbs.size() - 1; i++) {
            player.orbs.set(i, player.orbs.get(i + 1));
        }
        player.orbs.set(player.orbs.size() - 1, new EmptyOrbSlot());
        // The slot must already be absent if a destruction callback changes the network.
        orb.destroy(player);
        return true;
    }

    private ThreadOrb findEmptyMutableOrb() {
        for (ThreadOrb orb : threadOrbs()) {
            if (orb.isMutable() && orb.isEmpty()) {
                return orb;
            }
        }
        return null;
    }

    private ThreadOrb findOccupiedMutableOrb() {
        for (ThreadOrb orb : threadOrbs()) {
            if (orb.isMutable() && !orb.isEmpty()) {
                return orb;
            }
        }
        return null;
    }

    private List<ThreadOrb> threadOrbs() {
        ArrayList<ThreadOrb> result = new ArrayList<>();
        for (AbstractOrb orb : getOwner().orbs) {
            if (orb instanceof ThreadOrb) {
                result.add((ThreadOrb) orb);
            }
        }
        return result;
    }

    private void relayoutOrbs() {
        AbstractPlayer player = getOwner();
        for (int i = 0; i < player.orbs.size(); i++) {
            player.orbs.get(i).setSlot(i, player.maxOrbs);
        }
    }

    private AbstractPlayer getOwner() {
        return this.owner;
    }

    private void refreshCards() {
        this.owner.hand.applyPowers();
    }
}
