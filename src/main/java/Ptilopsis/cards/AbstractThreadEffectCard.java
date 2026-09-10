package Ptilopsis.cards;

import Ptilopsis.threads.ThreadFunction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public abstract class AbstractThreadEffectCard extends AbstractPtilopsisCard {
    private final InstallMode installMode;

    protected AbstractThreadEffectCard(String id, int cost, CardRarity rarity, InstallMode installMode) {
        super(id, cost, CardType.SKILL, rarity, CardTarget.SELF);
        this.installMode = installMode;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        beforeInstall(player, monster);
        if (this.installMode == InstallMode.READ_ONLY) {
            installReadOnlyThread(player, createThreadEffect());
        } else {
            mountMutableThread(player, createThreadEffect());
        }
    }

    protected void beforeInstall(AbstractPlayer player, AbstractMonster monster) {
    }

    protected abstract ThreadFunction createThreadEffect();

    protected enum InstallMode {
        READ_ONLY,
        MUTABLE
    }
}
