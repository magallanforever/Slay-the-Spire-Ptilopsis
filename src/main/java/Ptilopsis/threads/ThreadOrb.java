package Ptilopsis.threads;

import Ptilopsis.Ptilopsis;
import Ptilopsis.powers.ThreadNetworkPower;
import Ptilopsis.threads.effects.CardThreadFunction;
import Ptilopsis.ui.ThreadUiText;
import basemod.helpers.CardPowerTip;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.helpers.TipHelper;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadOrb extends AbstractOrb {
    public static final String ORB_ID = Ptilopsis.makeID("ThreadOrb");
    public static final int MANUAL_MOUNT_COST = 1;
    private static final Logger logger = LogManager.getLogger(ThreadOrb.class.getName());
    private static final float MENU_W = 176.0F;
    private static final float MENU_H = 42.0F;
    private static final float MENU_GAP = 6.0F;

    private static ThreadOrb menuOrb;
    private static ThreadOrb selectingOrb;
    private static ThreadOrb pressedOrb;
    private static long inputFrame = -1L;
    private static boolean wasLeftDown;
    private static boolean leftPressedThisFrame;
    private static boolean leftReleasedThisFrame;
    private static final Hitbox removeButtonHb = new Hitbox(1.0F, 1.0F);
    private static final Hitbox closeButtonHb = new Hitbox(1.0F, 1.0F);

    private final boolean mutable;
    private ThreadFunction function;
    private ThreadFunction expiringFunction;
    private List<AbstractCard> previewCards;
    private int previewIndex;

    public ThreadOrb(boolean mutable) {
        this.ID = ORB_ID;
        this.mutable = mutable;
        this.img = ImageMaster.ORB_PLASMA;
        this.basePassiveAmount = 0;
        this.passiveAmount = 0;
        this.baseEvokeAmount = 0;
        this.evokeAmount = 0;
        this.channelAnimTimer = 0.5F;
        updateDescription();
    }

    public boolean isMutable() {
        return this.mutable;
    }

    public boolean isEmpty() {
        return this.function == null;
    }

    public String getDisplayName(String emptyName) {
        return isEmpty() ? emptyName : this.function.getDisplayName();
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
        refreshPower(player);
    }

    public void destroy(AbstractPlayer player) {
        this.expiringFunction = null;
        this.previewCards = null;
        this.previewIndex = 0;
        if (menuOrb == this) {
            closeMenu();
        }
        if (pressedOrb == this) {
            pressedOrb = null;
        }
        if (this.function != null) {
            this.function.onDestruct(player);
            this.function = null;
        }
        updateDescription();
        refreshPower(player);
    }

    public void triggerManual(AbstractPlayer player) {
        if (this.function != null) {
            this.function.onManualTrigger(player);
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
        if (this.function != null) {
            this.function.atEndOfTurn(AbstractDungeon.player);
            if (this.mutable && this.function.expiresAtEndOfTurn()) {
                this.expiringFunction = this.function;
            }
        }
    }

    @Override
    public void onEvoke() {
        destroy(AbstractDungeon.player);
    }

    @Override
    public void update() {
        processSelection(AbstractDungeon.player);
        // Card effects are queued; keep the thread until its last replay finishes.
        if (this.expiringFunction != null && this.expiringFunction == this.function
                && !this.expiringFunction.isReplayPending(AbstractDungeon.player)) {
            ThreadNetworkPower.getOrCreate(AbstractDungeon.player).destructThread(this);
            return;
        }
        updateInputState();
        this.hb.update();
        this.fontScale = MathHelper.scaleLerpSnap(this.fontScale, 0.7F);
        updatePreview();

        if (menuOrb == this) {
            updateMenu();
        }

        if (!AbstractDungeon.isScreenUp && this.hb.hovered && leftPressedThisFrame) {
            pressedOrb = this;
        }
        if (!AbstractDungeon.isScreenUp && pressedOrb == this && this.hb.hovered && leftReleasedThisFrame) {
            pressedOrb = null;
            handleClick(AbstractDungeon.player);
        } else if (pressedOrb == this && leftReleasedThisFrame) {
            pressedOrb = null;
        }
    }

    private void updatePreview() {
        if (!this.hb.hovered || AbstractDungeon.isScreenUp || menuOrb != null) {
            this.previewCards = null;
            this.previewIndex = 0;
            return;
        }
        if (this.function == null) {
            TipHelper.renderGenericTip(this.cX + 96.0F * Settings.scale,
                    this.cY + 64.0F * Settings.scale, this.name, this.description);
            return;
        }
        if (this.previewCards == null) {
            this.previewCards = this.function.makePreviewCards();
        }
        if (this.previewCards.isEmpty()) {
            return;
        }
        int count = this.previewCards.size();
        if (InputHelper.scrolledDown) {
            this.previewIndex = (this.previewIndex + 1) % count;
        } else if (InputHelper.scrolledUp) {
            this.previewIndex = (this.previewIndex + count - 1) % count;
        }
        AbstractCard card = this.previewCards.get(this.previewIndex);
        card.applyPowers();
        card.freeToPlayOnce = true;
        CardPowerTip tip = new CardPowerTip(card, this.name,
                ThreadUiText.previewBody(this.function.runsAtStartOfTurn(),
                        this.mutable && this.function.expiresAtEndOfTurn(), this.previewIndex, count));
        tip.cardScale = 0.8F;
        ArrayList<PowerTip> tips = new ArrayList<>();
        tips.add(tip);
        float width = 320.0F * Settings.scale;
        float x = this.cX + 80.0F * Settings.scale;
        if (x + width > Settings.WIDTH - 30.0F * Settings.scale) {
            x = this.cX - width - 80.0F * Settings.scale;
        }
        x = Math.max(30.0F * Settings.scale, x);
        float y = Math.min(Settings.HEIGHT - 64.0F * Settings.scale,
                Math.max(this.cY + 160.0F * Settings.scale, 600.0F * Settings.scale));
        TipHelper.queuePowerTips(x, y, tips);
    }

    @Override
    public void updateDescription() {
        if (isEmpty()) {
            this.name = ThreadUiText.emptyMutableTitle();
            this.description = ThreadUiText.emptyMutableBody(MANUAL_MOUNT_COST);
        } else if (this.mutable) {
            this.name = ThreadUiText.mutableTitle();
            this.description = ThreadUiText.functionBody(this.function.getDisplayName(),
                    this.function.expiresAtEndOfTurn());
        } else {
            this.name = ThreadUiText.fixedTitle();
            this.description = ThreadUiText.fixedBody(this.function.getDisplayName());
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        renderBase(sb);
        renderMarker(sb);
        this.hb.render(sb);
        if (menuOrb == this) {
            renderMenu(sb);
        }
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

        if (!this.mutable) {
            closeMenu();
            return;
        }

        if (isEmpty()) {
            closeMenu();
            if (EnergyPanel.totalCount < MANUAL_MOUNT_COST) {
                showInsufficientEnergy(player);
                return;
            }
            if (!AbstractDungeon.isScreenUp && !player.hand.isEmpty()) {
                logger.info("Opening mutable thread hand select. handSize={}", player.hand.size());
                selectingOrb = this;
                AbstractDungeon.handCardSelectScreen.open(ThreadUiText.selectCardPrompt(MANUAL_MOUNT_COST), 1, false, true, false, false, true);
                CardCrawlGame.sound.play("UI_CLICK_1");
            }
        } else {
            logger.info("Toggling mutable thread menu. function={}", this.function.getDisplayName());
            menuOrb = menuOrb == this ? null : this;
            CardCrawlGame.sound.play("UI_CLICK_1");
        }
    }

    private static void processSelection(AbstractPlayer player) {
        if (selectingOrb == null || AbstractDungeon.isScreenUp) {
            return;
        }

        HandCardSelectScreen screen = AbstractDungeon.handCardSelectScreen;
        ArrayList<AbstractCard> selectedCards = new ArrayList<>(screen.selectedCards.group);
        screen.selectedCards.clear();
        screen.wereCardsRetrieved = true;
        ThreadOrb target = selectingOrb;
        selectingOrb = null;
        for (AbstractCard card : selectedCards) {
            if (!player.hand.contains(card)) {
                player.hand.addToTop(card);
            }
        }
        if (selectedCards.size() == 1 && canInteract(player)
                && player.orbs.contains(target) && target.mutable && target.isEmpty()) {
            if (EnergyPanel.totalCount >= MANUAL_MOUNT_COST) {
                CardThreadFunction mountedFunction = new CardThreadFunction(selectedCards.get(0));
                player.energy.use(MANUAL_MOUNT_COST);
                target.mount(mountedFunction, player);
                logger.info("Mounted mutable thread function. card={}, energyCost={}",
                        selectedCards.get(0).name, MANUAL_MOUNT_COST);
            } else {
                showInsufficientEnergy(player);
            }
        }
        player.hand.refreshHandLayout();
        player.hand.applyPowers();
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
        if (this.hb.hovered || menuOrb == this) {
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

    private void renderMenu(SpriteBatch sb) {
        positionMenuHitboxes();
        drawMenuButton(sb, removeButtonHb, ThreadUiText.remove(), true);
        drawMenuButton(sb, closeButtonHb, ThreadUiText.close(), false);
    }

    private void updateMenu() {
        if (AbstractDungeon.isScreenUp || !canInteract(AbstractDungeon.player)) {
            closeMenu();
            return;
        }
        positionMenuHitboxes();
        removeButtonHb.update();
        closeButtonHb.update();

        if (removeButtonHb.hovered && leftPressedThisFrame) {
            logger.info("Removing mutable thread function from menu.");
            destroy(AbstractDungeon.player);
            closeMenu();
            CardCrawlGame.sound.play("UI_CLICK_1");
        } else if (closeButtonHb.hovered && leftPressedThisFrame) {
            closeMenu();
            CardCrawlGame.sound.play("UI_CLICK_1");
        }
    }

    private void positionMenuHitboxes() {
        float w = MENU_W * Settings.scale;
        float h = MENU_H * Settings.scale;
        float y = this.cY + 76.0F * Settings.scale;
        float removeX = this.cX - w - MENU_GAP * Settings.scale;
        float closeX = this.cX + MENU_GAP * Settings.scale;
        removeButtonHb.resize(w, h);
        closeButtonHb.resize(w, h);
        removeButtonHb.move(removeX + w / 2.0F, y + h / 2.0F);
        closeButtonHb.move(closeX + w / 2.0F, y + h / 2.0F);
    }

    private void drawMenuButton(SpriteBatch sb, Hitbox hitbox, String text, boolean removeButton) {
        Color color = removeButton ? new Color(0.12F, 0.24F, 0.26F, 0.92F) : new Color(0.08F, 0.10F, 0.12F, 0.92F);
        if (hitbox.hovered) {
            color.r += 0.10F;
            color.g += 0.10F;
            color.b += 0.10F;
        }
        sb.setColor(color);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        FontHelper.renderFontCentered(sb,
                FontHelper.topPanelInfoFont,
                text,
                hitbox.cX,
                hitbox.cY + 5.0F * Settings.scale,
                Color.WHITE);
        hitbox.render(sb);
    }

    private static void closeMenu() {
        menuOrb = null;
    }

    private static void updateInputState() {
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

    private static void refreshPower(AbstractPlayer player) {
        ThreadNetworkPower power = ThreadNetworkPower.get(player);
        if (power != null) {
            power.refreshFromOrbs();
        }
    }
}
