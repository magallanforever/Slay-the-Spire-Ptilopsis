package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class NeuralStrike extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(NeuralStrike.class.getSimpleName());

    public NeuralStrike() {
        super(ID, 1, CardType.ATTACK, CardRarity.BASIC, CardTarget.ENEMY);
        this.damage = this.baseDamage = 6;
        this.tags.add(CardTags.STARTER_STRIKE);
        this.tags.add(CardTags.STRIKE);
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        dealDamage(player, monster, AbstractGameAction.AttackEffect.SLASH_DIAGONAL);
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(3);
        }
    }
}
