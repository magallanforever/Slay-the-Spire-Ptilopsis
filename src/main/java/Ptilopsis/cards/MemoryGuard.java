package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MemoryGuard extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(MemoryGuard.class.getSimpleName());

    public MemoryGuard() {
        super(ID, 1, CardType.SKILL, CardRarity.BASIC, CardTarget.SELF);
        this.block = this.baseBlock = 5;
        this.tags.add(CardTags.STARTER_DEFEND);
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        gainBlock(player);
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBlock(3);
        }
    }
}
