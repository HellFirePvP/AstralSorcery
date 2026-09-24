/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar.effect;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.ColorExtractUtil;
import hellfirepvp.astralsorcery.common.ingredient.IngredientBridge;
import hellfirepvp.astralsorcery.common.recipe.altar.ActiveAltarRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileFocusRelay;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DefaultAltarEffectRelayInput
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DefaultAltarEffectRelayInput extends AltarEffect {

    @Override
    @OnlyIn(Dist.CLIENT)
    public void tick(Level level, TileAltar altar, RandomSource rand, ActiveAltarRecipe recipe, int progressTick, int tick, ActiveAltarRecipe.State state, CompoundTag effectData) {
        recipe.getRecipe(level).ifPresent(r -> {
            for (int i = 0; i < 25; i++) {
                if (rand.nextBoolean()) continue;
                IngredientBridge relayInput = r.getGrid().getRelayInputs().get(i);
                if (relayInput.isEmpty()) continue;

                BlockPos offset = TileAltar.getRelayGridOffsets().get(i);
                if (offset == null) continue;
                BlockPos at = altar.getBlockPos().offset(offset);

                MiscUtil.getTileAt(level, at, TileFocusRelay.class, true).ifPresent(tile -> {
                    ItemStack input = tile.getTileData().getInventory().getStackInSlot(0);
                    if (input.isEmpty()) return;
                    if (!relayInput.test(input)) return;

                    ColorExtractUtil.getColor(input).ifPresent(color -> {
                        Vector3 pos = VectorUtil.withRandomOffset(Vector3.atBottomCenter(at), rand, 0.3F);
                        Vector3 grav = Vector3.y(0.0007F + rand.nextFloat() * 0.0004F);
                        float scale = 0.25F + rand.nextFloat() * 0.2F;

                        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                                .spawn(pos)
                                .color(FXColorFunction.constant(color))
                                .setScale(scale)
                                .alpha(FXAlphaFunction.FADE_OUT)
                                .setGravity(grav);

                        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                                .spawn(pos)
                                .color(FXColorFunction.WHITE)
                                .setScale(scale * 0.3F)
                                .alpha(FXAlphaFunction.FADE_OUT)
                                .setGravity(grav);
                    });
                });
            }
        });
    }
}
