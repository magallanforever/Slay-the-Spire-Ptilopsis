package Ptilopsis.powers;

import Ptilopsis.Ptilopsis;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class ExtremeDataConstructionPower extends AbstractPower {
    public static final String POWER_ID = Ptilopsis.makeID("ExtremeDataConstructionPower");

    public ExtremeDataConstructionPower(AbstractCreature owner) {
        this.ID = POWER_ID;
        this.name = getPowerStrings().NAME;
        this.owner = owner;
        this.type = PowerType.BUFF;
        this.isTurnBased = false;
        this.amount = -1;
        loadRegion("barricade");
        updateDescription();
    }

    public static boolean isActive(AbstractCreature creature) {
        return creature != null && creature.hasPower(POWER_ID);
    }

    @Override
    public void onInitialApplication() {
        refreshMasterTheoremDescriptions();
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;
        refreshMasterTheoremDescriptions();
    }

    @Override
    public void updateDescription() {
        this.description = getPowerStrings().DESCRIPTIONS[0];
    }

    private static void refreshMasterTheoremDescriptions() {
        if (AbstractDungeon.getCurrRoom() == null || AbstractDungeon.getMonsters() == null) {
            return;
        }

        for (AbstractMonster monster : AbstractDungeon.getMonsters().monsters) {
            AbstractPower power = monster.getPower(MasterTheoremPower.POWER_ID);
            if (power != null) {
                power.updateDescription();
            }
        }
    }

    private static PowerStrings getPowerStrings() {
        return CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    }
}
