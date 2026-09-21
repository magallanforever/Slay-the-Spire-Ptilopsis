package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.actions.BloodBrainBarrierAction;
import Ptilopsis.powers.MasterTheoremPower;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class BloodBrainBarrier extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(BloodBrainBarrier.class.getSimpleName());

    public BloodBrainBarrier() {
        super(ID, 2, CardType.SKILL, CardRarity.UNCOMMON, CardTarget.ALL_ENEMY);
        this.magicNumber = this.baseMagicNumber = 3;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        if (this.upgraded) {
            for (AbstractMonster enemy : AbstractDungeon.getMonsters().monsters) {
                if (!enemy.isDeadOrEscaped()) {
                    addToBot(new ApplyPowerAction(enemy, player,
                            new MasterTheoremPower(enemy, player, this.magicNumber), this.magicNumber));
                }
            }
        }
        // Read the stacks only after every application (including Artifact) has resolved.
        addToBot(new BloodBrainBarrierAction(player));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            this.rawDescription = CardCrawlGame.languagePack.getCardStrings(ID).UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}
