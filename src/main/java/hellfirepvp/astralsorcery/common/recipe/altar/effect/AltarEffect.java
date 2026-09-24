/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar.effect;

import hellfirepvp.astralsorcery.common.recipe.altar.ActiveAltarRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class AltarEffect {

    @OnlyIn(Dist.CLIENT)
    public abstract void tick(Level level, TileAltar altar, RandomSource rand, ActiveAltarRecipe recipe, int progressTick, int tick, ActiveAltarRecipe.State state, CompoundTag effectData);

    public void copyEffectData(CompoundTag from, CompoundTag to) {}

}
