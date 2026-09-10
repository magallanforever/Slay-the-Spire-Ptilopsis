package Ptilopsis.powers;

import Ptilopsis.Ptilopsis;
import Ptilopsis.threads.ThreadFunction;
import Ptilopsis.threads.ThreadOrb;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import java.util.ArrayList;
import java.util.List;

public class ThreadNetworkPower extends AbstractPower {
    public static final String POWER_ID = Ptilopsis.makeID("ThreadNetworkPower");
    private static final int MAX_THREADS = 3;

    public ThreadNetworkPower(AbstractPlayer player) {
        this.ID = POWER_ID;
        this.name = getPowerStrings().NAME;
        this.owner = player;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        this.amount = 0;
        loadRegion("focus");
        updateDescription();
    }

    public static ThreadNetworkPower get(AbstractPlayer player) {
        AbstractPower power = player.getPower(POWER_ID);
        if (power instanceof ThreadNetworkPower) {
            return (ThreadNetworkPower) power;
        }
        return null;
    }

    public static ThreadNetworkPower getOrCreate(AbstractPlayer player) {
        ThreadNetworkPower power = get(player);
        if (power != null) {
            return power;
        }

        power = new ThreadNetworkPower(player);
        player.addPower(power);
        AbstractDungeon.onModifyPower();
        return power;
    }

    public static int activeThreadCount(AbstractPlayer player) {
        ThreadNetworkPower power = get(player);
        if (power != null) {
            return power.countActiveThreads();
        }

        int count = 0;
        for (AbstractOrb orb : player.orbs) {
            if (orb instanceof ThreadOrb && !((ThreadOrb) orb).isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public void createEmptyMutableSlot() {
        addThreadOrb(new ThreadOrb(true));
        enforceCapacity();
        refreshPowerState();
    }

    public void installReadOnly(ThreadFunction function) {
        ThreadOrb orb = new ThreadOrb(false);
        orb.mount(function, getOwner());
        addThreadOrb(orb);
        enforceCapacity();
        refreshPowerState();
    }

    public void mountMutable(ThreadFunction function) {
        ThreadOrb orb = findEmptyMutableOrb();
        if (orb == null) {
            orb = findOccupiedMutableOrb();
        }
        if (orb == null) {
            orb = new ThreadOrb(true);
            addThreadOrb(orb);
        }

        orb.mount(function, getOwner());
        enforceCapacity();
        refreshPowerState();
    }

    public void triggerAllThreads() {
        List<ThreadOrb> orbs = threadOrbs();
        if (orbs.isEmpty()) {
            return;
        }
        flash();
        for (ThreadOrb orb : orbs) {
            orb.triggerManual(getOwner());
        }
    }

    public void triggerStartOfTurnThreads() {
    }

    public void triggerEndOfTurnThreads() {
    }

    public void refreshFromOrbs() {
        refreshPowerState();
    }

    @Override
    public void atStartOfTurnPostDraw() {
        triggerStartOfTurnThreads();
    }

    @Override
    public void atEndOfTurnPreEndTurnCards(boolean isPlayer) {
        if (isPlayer) {
            triggerEndOfTurnThreads();
        }
    }

    @Override
    public void onRemove() {
        destroyAllThreads();
    }

    @Override
    public void onVictory() {
        refreshPowerState();
    }

    @Override
    public void updateDescription() {
        PowerStrings strings = getPowerStrings();
        List<ThreadOrb> orbs = threadOrbs();
        StringBuilder builder = new StringBuilder();
        builder.append(strings.DESCRIPTIONS[0])
                .append(orbs.size())
                .append("/")
                .append(MAX_THREADS)
                .append(strings.DESCRIPTIONS[1]);

        if (orbs.isEmpty()) {
            builder.append(strings.DESCRIPTIONS[2]);
        } else {
            for (int i = 0; i < orbs.size(); i++) {
                if (i > 0) {
                    builder.append(strings.DESCRIPTIONS[4]);
                }
                builder.append(orbs.get(i).getDisplayName(strings.DESCRIPTIONS[3]));
            }
        }
        builder.append(strings.DESCRIPTIONS[5]);
        this.description = builder.toString();
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

    private void enforceCapacity() {
        List<ThreadOrb> orbs = threadOrbs();
        while (orbs.size() > MAX_THREADS) {
            ThreadOrb removed = orbs.get(0);
            removed.destroy(getOwner());
            getOwner().orbs.remove(removed);
            relayoutOrbs();
            orbs = threadOrbs();
        }
    }

    private void addThreadOrb(ThreadOrb orb) {
        AbstractPlayer player = getOwner();
        List<ThreadOrb> orbs = threadOrbs();
        for (ThreadOrb existing : orbs) {
            if (existing.isEmpty()) {
                int index = player.orbs.indexOf(existing);
                player.orbs.set(index, orb);
                orb.setSlot(index, player.maxOrbs);
                return;
            }
        }

        player.orbs.add(orb);
        relayoutOrbs();
    }

    private void destroyAllThreads() {
        for (ThreadOrb orb : threadOrbs()) {
            orb.destroy(getOwner());
        }
        refreshPowerState();
    }

    private int countActiveThreads() {
        int count = 0;
        for (ThreadOrb orb : threadOrbs()) {
            if (!orb.isEmpty()) {
                count++;
            }
        }
        return count;
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
        return (AbstractPlayer) this.owner;
    }

    private void refreshPowerState() {
        this.amount = threadOrbs().size();
        updateDescription();
        AbstractDungeon.onModifyPower();
    }

    private static PowerStrings getPowerStrings() {
        return CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    }
}
