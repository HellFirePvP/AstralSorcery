/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;

import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkRequirement
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class PerkRequirement {

    public static final Codec<PerkRequirement> CODEC = RegistriesAS.REGISTRY_PERK_REQUIREMENT_TYPES.byNameCodec()
            .dispatch(PerkRequirement::getType, PerkRequirement.Type::codec);

    public abstract Type<?> getType();

    public abstract Predicate<PlayerProgress> createProgressCondition();

    public record Type<T extends PerkRequirement>(MapCodec<T> codec) {
    }
}
