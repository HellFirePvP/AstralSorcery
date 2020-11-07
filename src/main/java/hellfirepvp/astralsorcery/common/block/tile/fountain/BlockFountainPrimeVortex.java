package hellfirepvp.astralsorcery.common.block.tile.fountain;

import hellfirepvp.astralsorcery.common.crafting.nojson.FountainEffectRegistry;
import hellfirepvp.astralsorcery.common.crafting.nojson.fountain.FountainEffect;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockFountainPrimeVortex
 * Created by HellFirePvP
 * Date: 31.10.2020 / 17:36
 */
public class BlockFountainPrimeVortex extends BlockFountainPrime {

    @Nonnull
    @Override
    public FountainEffect provideEffect() {
        return FountainEffectRegistry.EFFECT_VORTEX;
    }
}
