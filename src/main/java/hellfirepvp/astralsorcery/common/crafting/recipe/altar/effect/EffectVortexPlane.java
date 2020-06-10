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
import hellfirepvp.astralsorcery.client.effect.function.impl.RenderOffsetNoisePlane;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectVortexPlane
 * Created by HellFirePvP
 * Date: 26.09.2019 / 06:59
 */
public class EffectVortexPlane extends AltarRecipeEffect {

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTick(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state) {
        ActiveSimpleAltarRecipe recipe = altar.getActiveRecipe();
        if (recipe != null && state == ActiveSimpleAltarRecipe.CraftingState.ACTIVE) {
            Vector3 at = new Vector3(altar).add(0.5, 0.5, 0.5);

            RenderOffsetNoisePlane plane = recipe.getEffectContained(INDEX_NOISE_PLANE_LAYER1,
                    i -> new RenderOffsetNoisePlane(1.2F));
            for (int i = 0; i < 2; i++) {
                FXFacingParticle p = plane.createParticle(at.clone().add(getFocusRelayOffset(altar.getAltarType())));
                p.alpha(VFXAlphaFunction.FADE_OUT)
                        .setMotion(new Vector3(
                                rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1),
                                rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1),
                                rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1)))
                        .setScaleMultiplier(0.15F + rand.nextFloat() * 0.1F)
                        .setMaxAge(30 + rand.nextInt(15));
            }

            plane = recipe.getEffectContained(INDEX_NOISE_PLANE_LAYER2,
                    i -> new RenderOffsetNoisePlane(1.8F));
            for (int i = 0; i < 2; i++) {
                FXFacingParticle p = plane.createParticle(at.clone().add(getFocusRelayOffset(altar.getAltarType())));
                p.alpha(VFXAlphaFunction.FADE_OUT)
                        .setMotion(new Vector3(
                                rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1),
                                rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1),
                                rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1)))
                        .setScaleMultiplier(0.15F + rand.nextFloat() * 0.1F)
                        .setMaxAge(30 + rand.nextInt(15));
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTESR(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state, MatrixStack renderStack, IRenderTypeBuffer buffer, float pTicks, int combinedLight) {}

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onCraftingFinish(TileAltar altar, boolean isChaining) {}
}
