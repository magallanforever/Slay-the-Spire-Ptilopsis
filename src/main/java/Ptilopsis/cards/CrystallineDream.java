package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.actions.DestructThreadsGainBlockAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CrystallineDream extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(CrystallineDream.class.getSimpleName());
    private static final int BLOCK_PER_THREAD = 6;

    public CrystallineDream() {
        super(ID, 2, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.SELF);
        this.block = this.baseBlock = BLOCK_PER_THREAD;
        this.exhaust = true;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new DestructThreadsGainBlockAction(player, this.block));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.exhaust = false;
            this.rawDescription = CardCrawlGame.languagePack.getCardStrings(ID).UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}
