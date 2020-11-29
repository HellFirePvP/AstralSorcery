/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile.fountain;

import hellfirepvp.astralsorcery.common.crafting.nojson.FountainEffectRegistry;
import hellfirepvp.astralsorcery.common.crafting.nojson.fountain.FountainEffect;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockFountainPrimeOre
 * Created by HellFirePvP
 * Date: 31.10.2020 / 17:36
 */
public class BlockFountainPrimeOre extends BlockFountainPrime {

    @Nonnull
    @Override
    public FountainEffect<?> provideEffect() {
        return FountainEffectRegistry.EFFECT_LIQUID;
    }
}
