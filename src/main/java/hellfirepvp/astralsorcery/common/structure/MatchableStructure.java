/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MatchableStructure
 * Created by HellFirePvP
 * Date: 02.12.2018 / 10:17
 */
public interface MatchableStructure {

    @Nonnull
    public ResourceLocation getRegistryName();

}
