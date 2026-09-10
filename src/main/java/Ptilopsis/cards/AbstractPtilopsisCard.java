package Ptilopsis.cards;

import Ptilopsis.Ptilopsis;
import Ptilopsis.actions.ThreadCommandAction;
import Ptilopsis.character.PtilopsisCharacter;
import Ptilopsis.powers.ThreadNetworkPower;
import Ptilopsis.threads.ThreadFunction;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public abstract class AbstractPtilopsisCard extends CustomCard {
    private static final String DEFAULT_CARD_IMAGE = Ptilopsis.resourcePath("img/cards/default.png");

    protected AbstractPtilopsisCard(String id, int cost, CardType type, CardRarity rarity, CardTarget target) {
        super(
                id,
                getCardStrings(id).NAME,
                DEFAULT_CARD_IMAGE,
                cost,
                getCardStrings(id).DESCRIPTION,
                type,
                PtilopsisCharacter.Enums.PTILOPSIS_COLOR,
                rarity,
                target
        );
    }

    protected void dealDamage(AbstractPlayer player, AbstractMonster monster, AbstractGameAction.AttackEffect effect) {
        dealDamage(player, monster, this.damage, effect);
    }

    protected void dealDamage(AbstractPlayer player, AbstractMonster monster, int amount, AbstractGameAction.AttackEffect effect) {
        addToBot(new DamageAction(monster, new DamageInfo(player, amount, this.damageTypeForTurn), effect));
    }

    protected void gainBlock(AbstractPlayer player) {
        addToBot(new GainBlockAction(player, player, this.block));
    }

    protected void drawCards(AbstractPlayer player) {
        drawCards(player, this.magicNumber);
    }

    protected void drawCards(AbstractPlayer player, int amount) {
        if (amount > 0) {
            addToBot(new DrawCardAction(player, amount));
        }
    }

    protected void createEmptyMutableThread(AbstractPlayer player) {
        addToBot(new ThreadCommandAction(player, ThreadCommandAction.Command.CREATE_EMPTY_MUTABLE));
    }

    protected void installReadOnlyThread(AbstractPlayer player, ThreadFunction function) {
        addToBot(new ThreadCommandAction(player, ThreadCommandAction.Command.INSTALL_READ_ONLY, function));
    }

    protected void mountMutableThread(AbstractPlayer player, ThreadFunction function) {
        addToBot(new ThreadCommandAction(player, ThreadCommandAction.Command.MOUNT_MUTABLE, function));
    }

    protected void triggerAllThreads(AbstractPlayer player) {
        addToBot(new ThreadCommandAction(player, ThreadCommandAction.Command.TRIGGER_ALL));
    }

    protected int activeThreadCount(AbstractPlayer player) {
        return ThreadNetworkPower.activeThreadCount(player);
    }

    protected void gainEnergy(int amount) {
        addToBot(new GainEnergyAction(amount));
    }

    private static CardStrings getCardStrings(String id) {
        return CardCrawlGame.languagePack.getCardStrings(id);
    }
}
