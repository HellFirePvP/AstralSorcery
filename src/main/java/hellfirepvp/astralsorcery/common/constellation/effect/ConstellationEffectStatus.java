/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect;

import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationEffectStatus
 * Created by HellFirePvP
 * Date: 14.01.2018 / 21:23
 */
//Interface for constellation effects that don't care how strong the starlight influx is, they just provide a certain status
public interface ConstellationEffectStatus {

    public abstract boolean runEffect(World world, BlockPos pos, int mirrorAmount, boolean mayDoTraitEffect, @Nullable IMinorConstellation possibleTraitEffect);

    public abstract boolean runTraitEffect(World world, BlockPos pos, int mirrorAmount, IMinorConstellation traitType);

}
