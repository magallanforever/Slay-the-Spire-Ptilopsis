package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.threads.ThreadFunction;
import Ptilopsis.threads.effects.GuardThreadEffect;

public class ReadOnlyThread extends AbstractThreadEffectCard {
    public static final String ID = Ptilopsis.makeID(ReadOnlyThread.class.getSimpleName());

    public ReadOnlyThread() {
        super(ID, 1, CardRarity.BASIC, InstallMode.READ_ONLY);
        this.block = this.baseBlock = 4;
        this.magicNumber = this.baseMagicNumber = 1;
        this.selfRetain = true;
    }

    @Override
    protected ThreadFunction createThreadEffect() {
        return new GuardThreadEffect(this.name, this.block, this.magicNumber);
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(3);
        }
    }
}
