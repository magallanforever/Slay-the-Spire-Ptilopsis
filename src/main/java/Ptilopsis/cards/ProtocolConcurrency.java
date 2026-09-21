package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.actions.ThreadCommandAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class ProtocolConcurrency extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(ProtocolConcurrency.class.getSimpleName());

    public ProtocolConcurrency() {
        super(ID, 1, CardType.SKILL, CardRarity.COMMON, CardTarget.SELF);
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new ThreadCommandAction(player, ThreadCommandAction.Command.TRIGGER_ALL_END_OF_TURN_EFFECTS));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeBaseCost(0);
        }
    }
}
