package Ptilopsis.actions;

import Ptilopsis.threads.ThreadNetwork;
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
        ThreadNetwork network = ThreadNetwork.forPlayer(player);
        switch (this.command) {
            case TRIGGER_ALL_DESTRUCT_EFFECTS:
                network.triggerAllDestructEffects();
                break;
            case TRIGGER_ALL_END_OF_TURN_EFFECTS:
                network.triggerAllEndOfTurnEffects();
                break;
            case CREATE_EMPTY_MUTABLE:
                network.createEmptyMutableSlot();
                break;
            case INSTALL_READ_ONLY:
                network.installReadOnly(this.function);
                break;
            case MOUNT_MUTABLE:
                network.mountMutable(this.function);
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
        TRIGGER_ALL_DESTRUCT_EFFECTS,
        TRIGGER_ALL_END_OF_TURN_EFFECTS
    }
}
