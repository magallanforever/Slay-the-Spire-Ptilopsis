package Ptilopsis.actions;

import Ptilopsis.powers.ThreadNetworkPower;
import Ptilopsis.threads.ThreadFunction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class ThreadCommandAction extends AbstractGameAction {
    private final Command command;
    private final ThreadFunction function;

    public ThreadCommandAction(AbstractPlayer player, Command command) {
        this(player, command, null);
    }

    public ThreadCommandAction(AbstractPlayer player, Command command, ThreadFunction function) {
        this.target = player;
        this.source = player;
        this.command = command;
        this.function = function;
    }

    @Override
    public void update() {
        if (!(this.target instanceof AbstractPlayer) || this.target.isDeadOrEscaped()) {
            this.isDone = true;
            return;
        }

        AbstractPlayer player = (AbstractPlayer) this.target;
        if (this.command == Command.TRIGGER_ALL) {
            ThreadNetworkPower.getOrCreate(player).triggerAllThreads();
            this.isDone = true;
            return;
        }

        ThreadNetworkPower power = ThreadNetworkPower.getOrCreate(player);
        switch (this.command) {
            case CREATE_EMPTY_MUTABLE:
                power.createEmptyMutableSlot();
                break;
            case INSTALL_READ_ONLY:
                power.installReadOnly(this.function);
                break;
            case MOUNT_MUTABLE:
                power.mountMutable(this.function);
                break;
            default:
                break;
        }

        this.isDone = true;
    }

    public enum Command {
        CREATE_EMPTY_MUTABLE,
        INSTALL_READ_ONLY,
        MOUNT_MUTABLE,
        TRIGGER_ALL
    }
}
