package Ptilopsis.threads.effects;

import Ptilopsis.cards.ThreadEffectCard;
import Ptilopsis.threads.ThreadCardPlayback;
import Ptilopsis.threads.ThreadFunction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import java.util.Collections;
import java.util.List;

public class MedicalPulseThreadEffect implements ThreadFunction {
    private final String displayName;
    private final int damage;
    private final int destructionDamage;
    private final ThreadCardPlayback playback = new ThreadCardPlayback();

    public MedicalPulseThreadEffect(String displayName, int damage, int destructionDamage) {
        this.displayName = displayName;
        this.damage = damage;
        this.destructionDamage = destructionDamage;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public List<AbstractCard> makePreviewCards() {
        return Collections.singletonList(new ThreadEffectCard(ThreadEffectCard.Effect.PULSE, this.damage));
    }

    @Override
    public void atEndOfTurn(AbstractPlayer player) {
        this.playback.play(player, makePreviewCards());
    }

    @Override
    public void onDestruct(AbstractPlayer player) {
        this.playback.playDestruction(player, Collections.singletonList(
                new ThreadEffectCard(ThreadEffectCard.Effect.PULSE, this.destructionDamage)));
    }
}
