/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar.effect;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.recipe.altar.ActiveAltarRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DefaultAltarEffectAltarSparkle
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DefaultAltarEffectAltarSparkle extends AltarEffect {

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tick(Level level, TileAltar altar, RandomSource rand, ActiveAltarRecipe recipe, int progressTick, int tick, ActiveAltarRecipe.State state, CompoundTag effectData) {
        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(Vector3.atCenter(altar))
                .setScale(0.14F + rand.nextFloat() * 0.06F)
                .setMotion(Vector3.positiveYRandom(rand).multiply(0.05F))
                .setGravity(Vector3.y(0.0004F))
                .setMaxAge(25 + rand.nextInt(25));
    }
}
