package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.powers.MasterTheoremPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class DivideAndConquer extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(DivideAndConquer.class.getSimpleName());

    public DivideAndConquer() {
        super(ID, 2, CardType.SKILL, CardRarity.COMMON, CardTarget.ALL_ENEMY);
        this.magicNumber = this.baseMagicNumber = 12;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        for (AbstractMonster enemy : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!enemy.isDeadOrEscaped()) {
                addToBot(new ApplyPowerAction(
                        enemy,
                        player,
                        new MasterTheoremPower(enemy, player, this.magicNumber),
                        this.magicNumber
                ));
            }
        }
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(1);
            this.exhaust = true;
            this.rawDescription = CardCrawlGame.languagePack.getCardStrings(ID).UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}
