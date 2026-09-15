package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.threads.effects.MedicalPulseThreadEffect;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class MedicalInstruction extends AbstractPtilopsisCard {
    public static final String ID = Ptilopsis.makeID(MedicalInstruction.class.getSimpleName());

    public MedicalInstruction() {
        super(ID, 1, CardType.ATTACK, CardRarity.BASIC, CardTarget.ENEMY);
        this.damage = this.baseDamage = 4;
        this.magicNumber = this.baseMagicNumber = 1;
    }

    @Override
    public void use(AbstractPlayer player, AbstractMonster monster) {
        addToBot(new ApplyPowerAction(monster, player,
                new VulnerablePower(monster, this.magicNumber, false), this.magicNumber));
        AbstractCard hit = makeStatEquivalentCopy();
        // Calculate after Vulnerable resolves, including the case where Artifact blocks it.
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                if (monster != null && !monster.isDeadOrEscaped()) {
                    hit.calculateCardDamage(monster);
                    addToTop(new DamageAction(monster,
                            new DamageInfo(player, hit.damage, hit.damageTypeForTurn),
                            AttackEffect.SLASH_DIAGONAL));
                }
                this.isDone = true;
            }
        });
        installReadOnlyThread(player, new MedicalPulseThreadEffect(this.name,
                this.upgraded ? 4 : 3, this.upgraded ? 8 : 6));
    }

    @Override
    public void upgrade() {
        if (!this.upgraded) {
            upgradeName();
            upgradeDamage(1);
            upgradeMagicNumber(1);
            this.rawDescription = CardCrawlGame.languagePack.getCardStrings(ID).UPGRADE_DESCRIPTION;
            initializeDescription();
        }
    }
}
