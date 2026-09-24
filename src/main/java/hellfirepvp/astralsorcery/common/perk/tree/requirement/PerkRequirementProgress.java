/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree.requirement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchTier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkRequirementProgress
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkRequirementProgress extends PerkRequirement {

    public static final MapCodec<PerkRequirementProgress> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            StringRepresentable.fromEnum(ResearchTier::values).fieldOf("needed_tier").forGetter(PerkRequirementProgress::getNeededTier)
    ).apply(inst, PerkRequirementProgress::new));
    public static final Type<PerkRequirementProgress> TYPE = new Type<>(PerkRequirementProgress.CODEC);

    private final ResearchTier neededTier;

    private PerkRequirementProgress(ResearchTier neededTier) {
        this.neededTier = neededTier;
    }

    public static PerkRequirementProgress of(ResearchTier neededTier) {
        return new PerkRequirementProgress(neededTier);
    }

    public ResearchTier getNeededTier() {
        return this.neededTier;
    }

    @Override
    public Type<?> getType() {
        return TYPE;
    }

    @Override
    public Predicate<PlayerProgress> createProgressCondition() {
        return prog -> prog.getTierReached().isThisLaterOrEqual(this.getNeededTier());
    }
}
