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
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import net.minecraft.world.entity.player.Player;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkRequirementConstellation
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkRequirementConstellation extends PerkRequirement {

    public static final MapCodec<PerkRequirementConstellation> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec().fieldOf("constellation").forGetter(PerkRequirementConstellation::getConstellation)
    ).apply(inst, PerkRequirementConstellation::new));
    public static final Type<PerkRequirementConstellation> TYPE = new Type<>(PerkRequirementConstellation.CODEC);

    private final BaseConstellation constellation;

    private PerkRequirementConstellation(BaseConstellation constellation) {
        this.constellation = constellation;
    }

    public static PerkRequirementConstellation of(Supplier<? extends BaseConstellation> constellation) {
        return of(constellation.get());
    }

    public static PerkRequirementConstellation of(BaseConstellation constellation) {
        return new PerkRequirementConstellation(constellation);
    }

    public BaseConstellation getConstellation() {
        return this.constellation;
    }

    @Override
    public Type<?> getType() {
        return TYPE;
    }

    @Override
    public Predicate<PlayerProgress> createProgressCondition() {
        return prog -> prog.hasDiscoveredConstellation(this.getConstellation());
    }
}
