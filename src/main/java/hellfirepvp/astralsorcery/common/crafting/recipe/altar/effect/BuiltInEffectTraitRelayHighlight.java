/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.altar.effect;

import com.mojang.blaze3d.matrix.MatrixStack;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.ColorizationHelper;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.crafting.helper.CraftingFocusStack;
import hellfirepvp.astralsorcery.common.crafting.helper.WrappedIngredient;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.tile.TileSpectralRelay;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BuiltInEffectTraitRelayHighlight
 * Created by HellFirePvP
 * Date: 24.09.2019 / 19:56
 */
public class BuiltInEffectTraitRelayHighlight extends AltarRecipeEffect {

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTick(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state) {
        ActiveSimpleAltarRecipe recipe = altar.getActiveRecipe();
        if (recipe != null) {
            List<WrappedIngredient> additionalIngredients = recipe.getRecipeToCraft().getRelayInputs();
            for (CraftingFocusStack stack : recipe.getFocusStacks()) {
                if (stack.getStackIndex() < 0 || stack.getStackIndex() >= additionalIngredients.size()) {
                    continue;
                }
                WrappedIngredient match = additionalIngredients.get(stack.getStackIndex());

                TileSpectralRelay relay = MiscUtils.getTileAt(altar.getWorld(), stack.getRealPosition(), TileSpectralRelay.class, false);
                if (relay != null) {
                    ItemStack in = relay.getInventory().getStackInSlot(0);
                    if (!in.isEmpty() && match.getIngredient().test(in)) {
                        Color color = ColorizationHelper.getColor(in)
                                .orElse(ColorsAS.CELESTIAL_CRYSTAL);

                        playLightbeam(altar, relay, color);
                        playRelayHighlightParticles(relay, color);

                        if (rand.nextInt(4) == 0) {
                            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                                    .spawn(new Vector3(altar).add(
                                            -3 + rand.nextInt(7),
                                            0.02,
                                            -3 + rand.nextInt(7)))
                                    .color(VFXColorFunction.constant(color))
                                    .alpha(VFXAlphaFunction.FADE_OUT)
                                    .setScaleMultiplier(0.15F + rand.nextFloat() * 0.2F);
                        }
                    } else {
                        ItemStack chosen = match.getRandomMatchingStack(getClientTick());
                        Color color = ColorizationHelper.getColor(chosen)
                                .orElse(ColorsAS.CELESTIAL_CRYSTAL);

                        playLightbeam(altar, relay, color);
                        playRelayHighlightParticles(relay, color);
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playRelayHighlightParticles(TileSpectralRelay relay, Color color) {
        if (rand.nextBoolean()) {
            FXFacingParticle particle = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(new Vector3(relay).add(rand.nextFloat(), 0, rand.nextFloat()))
                    .setAlphaMultiplier(0.7F)
                    .setScaleMultiplier(0.2F + rand.nextFloat() * 0.1F)
                    .setMaxAge(30 + rand.nextInt(50));
            if (rand.nextInt(3) == 0) {
                particle.color(VFXColorFunction.WHITE)
                        .setScaleMultiplier(0.1F + rand.nextFloat() * 0.1F);
            } else {
                particle.color(VFXColorFunction.constant(color))
                        .setGravityStrength(-0.0015F);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void playLightbeam(TileAltar from, TileSpectralRelay to, Color color) {
        if (getClientTick() % 35 == 0) {
            EffectHelper.of(EffectTemplatesAS.LIGHTBEAM)
                    .spawn(new Vector3(from).add(0.5, 0, 0.5).add(getFocusRelayOffset(from.getAltarType())))
                    .setup(new Vector3(to).add(0.5, 0.1, 0.5), 0.8F, 0.8F)
                    .color(VFXColorFunction.constant(color));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTESR(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state, MatrixStack renderStack, IRenderTypeBuffer buffer, float pTicks, int combinedLight) {
        ActiveSimpleAltarRecipe activeRecipe = altar.getActiveRecipe();
        if (activeRecipe != null) {
            List<WrappedIngredient> additionalIngredients = activeRecipe.getRecipeToCraft().getRelayInputs();
            List<CraftingFocusStack> focusStacks = activeRecipe.getFocusStacks();
            for (CraftingFocusStack stack : focusStacks) {
                if (stack.getStackIndex() < 0 || stack.getStackIndex() >= additionalIngredients.size()) {
                    continue;
                }

                WrappedIngredient match = additionalIngredients.get(stack.getStackIndex());
                BlockPos offset = stack.getRealPosition().subtract(altar.getPos());

                TileSpectralRelay relay = MiscUtils.getTileAt(altar.getWorld(), stack.getRealPosition(), TileSpectralRelay.class, false);

                if (relay == null || (!match.getIngredient().test(relay.getInventory().getStackInSlot(0)))) {
                    ItemStack potential = match.getRandomMatchingStack(getClientTick());
                    renderStack.push();
                    renderStack.translate(0.5 + offset.getX(), 0.35 + offset.getY(), 0.5  + offset.getZ());
                    renderStack.scale(0.5F, 0.5F, 0.5F);
                    RenderingUtils.renderTranslucentItemStack(potential, renderStack, pTicks);
                    renderStack.pop();
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onCraftingFinish(TileAltar altar, boolean isChaining) {}
}
