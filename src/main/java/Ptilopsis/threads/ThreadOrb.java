package Ptilopsis.threads;

import Ptilopsis.Ptilopsis;
import Ptilopsis.actions.ManualMountAction;
import Ptilopsis.ui.ThreadUiText;
import Ptilopsis.ui.ThreadPreview;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import java.util.ArrayList;

public class ThreadOrb extends AbstractOrb {
    public static final String ORB_ID = Ptilopsis.makeID("ThreadOrb");
    public static final int MANUAL_MOUNT_COST = 1;
    private boolean pressed;
    private long inputFrame = -1L;
    private boolean wasLeftDown;
    private boolean leftPressedThisFrame;
    private boolean leftReleasedThisFrame;

    private final boolean mutable;
    private ThreadFunction function;
    private boolean pendingConstruct;
    private ThreadFunction expiringFunction;
    private ThreadPreview preview;

    public ThreadOrb(boolean mutable) {
        this(mutable, null);
    }

    public ThreadOrb(boolean mutable, ThreadFunction function) {
        this.ID = ORB_ID;
        this.mutable = mutable;
        this.function = function;
        this.pendingConstruct = function != null;
        this.img = ImageMaster.ORB_PLASMA;
        this.basePassiveAmount = 0;
        this.passiveAmount = 0;
        this.baseEvokeAmount = 0;
        this.evokeAmount = 0;
        this.channelAnimTimer = 0.5F;
        updateDescription();
    }

    public void onChannel(AbstractPlayer player) {
        if (this.pendingConstruct) {
            this.pendingConstruct = false;
            this.function.onConstruct(player);
        }
        player.hand.applyPowers();
    }

    public boolean isMutable() {
        return this.mutable;
    }

    public boolean isEmpty() {
        return this.function == null;
    }

    public void mount(ThreadFunction function, AbstractPlayer player) {
        if (!this.mutable && !isEmpty()) {
            throw new IllegalStateException("Fixed thread orbs cannot be modified.");
        }
        destroy(player);
        this.function = function;
        if (this.function != null) {
            this.function.onConstruct(player);
        }
        updateDescription();
        player.hand.applyPowers();
    }

    public void destroy(AbstractPlayer player) {
        this.pendingConstruct = false;
        this.expiringFunction = null;
        this.preview = null;
        this.pressed = false;
        // Detach first: callbacks must not see or destruct the old function again.
        ThreadFunction previous = this.function;
        this.function = null;
        if (previous != null) {
            previous.onDestruct(player);
        }
        updateDescription();
        player.hand.applyPowers();
    }

    public int getBlockGainBonus() {
        return this.function == null ? 0 : this.function.getBlockGainBonus();
    }

    public void triggerDestructEffect(AbstractPlayer player) {
        if (this.function != null) {
            this.function.triggerDestructEffect(player);
        }
    }

    @Override
    public void onStartOfTurn() {
        if (this.function != null) {
            this.function.atStartOfTurn(AbstractDungeon.player);
        }
    }

    @Override
    public void onEndOfTurn() {
        ThreadFunction current = this.function;
        if (current != null) {
            current.atEndOfTurn(AbstractDungeon.player);
            if (this.function == current && this.mutable && current.expiresAtEndOfTurn()) {
                this.expiringFunction = current;
            }
        }
    }

    public void triggerEndOfTurnEffect(AbstractPlayer player) {
        if (this.function != null) {
            this.function.atEndOfTurn(player);
        }
    }

    @Override
    public void onEvoke() {
        destroy(AbstractDungeon.player);
    }

    @Override
    public void update() {
        // Card effects are queued; keep the thread until its last replay finishes.
        if (this.expiringFunction != null && this.expiringFunction == this.function
                && !this.expiringFunction.isReplayPending(AbstractDungeon.player)) {
            ThreadNetwork.forPlayer(AbstractDungeon.player).destructThread(this);
            return;
        }
        updateInputState();
        this.hb.update();
        this.fontScale = MathHelper.scaleLerpSnap(this.fontScale, 0.7F);
        updatePreview();

        if (!AbstractDungeon.isScreenUp && this.hb.hovered && leftPressedThisFrame) {
            this.pressed = true;
        }
        if (!AbstractDungeon.isScreenUp && this.pressed && this.hb.hovered && leftReleasedThisFrame) {
            this.pressed = false;
            handleClick(AbstractDungeon.player);
        } else if (AbstractDungeon.isScreenUp || leftReleasedThisFrame) {
            this.pressed = false;
        }
    }

    private void updatePreview() {
        if (!this.hb.hovered || AbstractDungeon.isScreenUp) {
            this.preview = null;
            return;
        }
        ArrayList<PowerTip> tips;
        if (this.function == null) {
            tips = new ArrayList<>();
            tips.add(new PowerTip(this.name, this.description));
        } else {
            if (this.preview == null) {
                this.preview = new ThreadPreview(this.function, this.mutable);
            }
            int scroll = InputHelper.scrolledDown ? 1 : InputHelper.scrolledUp ? -1 : 0;
            tips = this.preview.makeTips(scroll);
        }
        this.description = tips.get(0).body;
        float width = 320.0F * Settings.scale;
        float x = this.cX + 80.0F * Settings.scale;
        if (x + width > Settings.WIDTH - 30.0F * Settings.scale) {
            x = this.cX - width - 80.0F * Settings.scale;
        }
        x = Math.max(30.0F * Settings.scale, x);
        float minY = this.function != null && this.function.showsCardPreview() ? 600.0F : 260.0F;
        float y = Math.min(Settings.HEIGHT - 64.0F * Settings.scale,
                Math.max(this.cY + 160.0F * Settings.scale, minY * Settings.scale));
        TipHelper.queuePowerTips(x, y, tips);
    }

    @Override
    public void updateDescription() {
        if (isEmpty()) {
            this.name = ThreadUiText.emptyMutableTitle();
            this.description = ThreadUiText.emptyMutableBody(MANUAL_MOUNT_COST);
        } else if (this.mutable) {
            this.name = ThreadUiText.mutableTitle();
            this.description = "";
        } else {
            this.name = this.function.getDisplayName();
            this.description = "";
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        renderBase(sb);
        renderMarker(sb);
        this.hb.render(sb);
    }

    @Override
    public AbstractOrb makeCopy() {
        return new ThreadOrb(this.mutable);
    }

    @Override
    public void playChannelSFX() {
        CardCrawlGame.sound.play("ORB_PLASMA_CHANNEL", 0.1F);
    }

    private void handleClick(AbstractPlayer player) {
        if (AbstractDungeon.isScreenUp || !canInteract(player)) {
            return;
        }

        if (!this.mutable || !isEmpty()) {
            return;
        }

        if (EnergyPanel.totalCount < MANUAL_MOUNT_COST) {
            showInsufficientEnergy(player);
            return;
        }
        if (!player.hand.isEmpty()) {
            AbstractDungeon.actionManager.addToBottom(new ManualMountAction(player, this));
        }
    }

    private static boolean canInteract(AbstractPlayer player) {
        return player != null && !player.isDeadOrEscaped()
                && !player.endTurnQueued && !player.isEndingTurn && !player.isDraggingCard
                && AbstractDungeon.currMapNode != null
                && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT
                && !AbstractDungeon.getMonsters().areMonstersBasicallyDead()
                && AbstractDungeon.actionManager != null
                && !AbstractDungeon.actionManager.turnHasEnded
                && AbstractDungeon.actionManager.phase == GameActionManager.Phase.WAITING_ON_USER
                && AbstractDungeon.actionManager.currentAction == null
                && AbstractDungeon.actionManager.cardQueue.isEmpty()
                && AbstractDungeon.actionManager.isEmpty();
    }

    private static void showInsufficientEnergy(AbstractPlayer player) {
        AbstractDungeon.effectList.add(new ThoughtBubble(player.dialogX, player.dialogY,
                2.0F, ThreadUiText.insufficientEnergy(MANUAL_MOUNT_COST), true));
    }

    private void renderBase(SpriteBatch sb) {
        Color fill = this.mutable ? Ptilopsis.PTILOPSIS_COLOR.cpy() : Color.SKY.cpy();
        fill.a = isEmpty() ? 0.35F : 0.90F;
        if (this.hb.hovered) {
            fill.a = Math.min(1.0F, fill.a + 0.10F);
        }

        sb.setColor(this.c);
        sb.draw(ImageMaster.ORB_SLOT_2,
                this.cX - 48.0F,
                this.cY - 48.0F + this.bobEffect.y / 8.0F,
                48.0F,
                48.0F,
                96.0F,
                96.0F,
                this.scale,
                this.scale,
                0.0F,
                0,
                0,
                96,
                96,
                false,
                false);
        sb.setColor(fill);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG,
                this.cX - 28.0F * Settings.scale,
                this.cY - 28.0F * Settings.scale + this.bobEffect.y,
                56.0F * Settings.scale,
                56.0F * Settings.scale);
        sb.setColor(this.c);
        sb.draw(ImageMaster.ORB_SLOT_1,
                this.cX - 48.0F,
                this.cY - 48.0F - this.bobEffect.y / 8.0F,
                48.0F,
                48.0F,
                96.0F,
                96.0F,
                this.scale,
                this.scale,
                this.angle,
                0,
                0,
                96,
                96,
                false,
                false);
    }

    private void renderMarker(SpriteBatch sb) {
        String marker;
        if (isEmpty()) {
            marker = ThreadUiText.emptyMarker();
        } else {
            marker = this.mutable ? ThreadUiText.mutableMarker() : ThreadUiText.fixedMarker();
        }
        FontHelper.renderFontCentered(sb, FontHelper.powerAmountFont, marker, this.cX, this.cY + 4.0F * Settings.scale, Color.WHITE);
    }

    private void updateInputState() {
        long frame = Gdx.graphics.getFrameId();
        if (inputFrame == frame) {
            return;
        }

        boolean leftDown = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        leftPressedThisFrame = leftDown && !wasLeftDown;
        leftReleasedThisFrame = !leftDown && wasLeftDown;
        wasLeftDown = leftDown;
        inputFrame = frame;
    }

}
