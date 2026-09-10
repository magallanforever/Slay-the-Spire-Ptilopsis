package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.threads.ThreadFunction;
import Ptilopsis.threads.effects.WakeRoutineThreadEffect;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class WakeRoutine extends AbstractThreadEffectCard {
    public static final String ID = Ptilopsis.makeID(WakeRoutine.class.getSimpleName());

    public WakeRoutine() {
        super(ID, 1, CardRarity.COMMON, InstallMode.READ_ONLY);
        this.magicNumber = this.baseMagicNumber = 2;
        this.exhaust = false;
    }

    @Override
    protected void beforeInstall(AbstractPlayer player, AbstractMonster monster) {
        drawCards(player);
    }

    @Override
    protected ThreadFunction createThreadEffect() {
        return new WakeRoutineThreadEffect(this.name, this.magicNumber);
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.isInnate = true;
            this.rawDescription = CardCrawlGame.languagePack.getCardStrings(ID).UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}
