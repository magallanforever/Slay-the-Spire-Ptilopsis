package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.powers.StunPower;
import Ptilopsis.threads.effects.CalcificationThreadEffect;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Saria extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(Saria.class.getSimpleName());

    public Saria() {
        super(ID, 2, CardType.ATTACK, CardRarity.RARE, CardTarget.ENEMY);
        this.damage = this.baseDamage = 7;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        for (int i = 0; i < 3; i++) {
            dealDamage(player, monster, AbstractGameAction.AttackEffect.BLUNT_HEAVY);
        }
        addToBot(new ApplyPowerAction(monster, player, new StunPower(monster), 1));
        installReadOnlyThread(player, new CalcificationThreadEffect(this.upgraded));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(2);
            this.rawDescription = CardCrawlGame.languagePack.getCardStrings(ID).UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}
