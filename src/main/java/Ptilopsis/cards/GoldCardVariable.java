package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import basemod.BaseMod;
import basemod.abstracts.DynamicVariable;
import basemod.helpers.dynamicvariables.BlockVariable;
import basemod.helpers.dynamicvariables.DamageVariable;
import basemod.helpers.dynamicvariables.MagicNumberVariable;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;

/** Mod-local aliases preserve native values and use gold in every display state. */
public final class GoldCardVariable extends DynamicVariable {
    private final DynamicVariable delegate;

    public GoldCardVariable(DynamicVariable delegate) {
        this.delegate = delegate;
    }

    public static void register() {
        BaseMod.addDynamicVariable(new GoldCardVariable(new DamageVariable()));
        BaseMod.addDynamicVariable(new GoldCardVariable(new BlockVariable()));
        BaseMod.addDynamicVariable(new GoldCardVariable(new MagicNumberVariable()));
    }

    @Override public String key() { return Ptilopsis.makeID(this.delegate.key()); }
    @Override public boolean isModified(AbstractCard card) { return this.delegate.isModified(card); }
    @Override public void setIsModified(AbstractCard card, boolean modified) { this.delegate.setIsModified(card, modified); }
    @Override public int value(AbstractCard card) { return this.delegate.value(card); }
    @Override public int baseValue(AbstractCard card) { return this.delegate.baseValue(card); }
    @Override public int modifiedBaseValue(AbstractCard card) { return this.delegate.modifiedBaseValue(card); }
    @Override public boolean upgraded(AbstractCard card) { return this.delegate.upgraded(card); }
    @Override public Color getNormalColor() { return Settings.GOLD_COLOR; }
    @Override public Color getUpgradedColor() { return Settings.GOLD_COLOR; }
    @Override public Color getIncreasedValueColor() { return Settings.GOLD_COLOR; }
    @Override public Color getDecreasedValueColor() { return Settings.GOLD_COLOR; }
}
