/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar.effect;

import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import hellfirepvp.astralsorcery.common.recipe.altar.ActiveAltarRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DefaultAltarEffectLumenInput
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DefaultAltarEffectLumenInput extends AltarEffect {

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tick(Level level, TileAltar altar, RandomSource rand, ActiveAltarRecipe activeRecipe, int progressTick, int tick, ActiveAltarRecipe.State state, CompoundTag effectData) {
        activeRecipe.getRecipe(level).ifPresent(recipe -> {
            activeRecipe.getDrawnLumen().forEach(lumen -> {
                if (lumen.isEmpty() || rand.nextBoolean()) return;

                LumenStack required = recipe.getRequiredLumen().stream()
                        .filter(ls -> ls.is(lumen.getLumen()))
                        .findFirst()
                        .orElse(LumenStack.EMPTY);
                if (required.isEmpty() || lumen.getAmount() < required.getAmount()) return;

                Vector3 motion = Vector3.positiveYRandom(rand).multiply(0.04F);
                Vector3 pos = VectorUtil.withRandomOffset(Vector3.atBottomCenter(altar), rand, 0.2F);
                Vector3 grav = Vector3.y(0.002F + rand.nextFloat() * 0.001F);
                float scale = 0.5F + rand.nextFloat() * 0.2F;
                long lumenTick = ClientProxy.getClientTick() / 60;

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(pos)
                        .color(FXColorFunction.constant(lumen.getLumen().getColor(lumenTick)))
                        .setScale(scale)
                        .alpha(FXAlphaFunction.FADE_OUT)
                        .setMotion(motion)
                        .setGravity(grav);

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(pos)
                        .color(FXColorFunction.WHITE)
                        .setScale(scale * 0.3F)
                        .alpha(FXAlphaFunction.FADE_OUT)
                        .setMotion(motion)
                        .setGravity(grav);

                if (rand.nextBoolean()) {
                    lumen.getLumen().getRegistryKey().ifPresent(key -> {
                        EffectHelper.of(EffectTemplatesAS.LUMEN_PARTICLE)
                                .spawn(pos)
                                .setSprite(TexturesAS.ATLAS_LUMEN, key.location())
                                .alpha(FXAlphaFunction.FADE_OUT)
                                .setAlpha(0.7F)
                                .color(FXColorFunction.constant(lumen.getLumen().getColor(lumenTick)))
                                .setMotion(Vector3.positiveYRandom(rand).multiply(0.06F))
                                .setGravity(Vector3.y(0.001F))
                                .setMaxAge(30 + rand.nextInt(15));
                    });
                }
            });
        });
    }
}
