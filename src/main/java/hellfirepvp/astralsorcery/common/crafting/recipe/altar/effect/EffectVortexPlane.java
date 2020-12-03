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
import hellfirepvp.astralsorcery.client.effect.function.impl.RenderOffsetNoisePlane;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.tile.altar.TileAltar;
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
            Vector3 at = new Vector3(altar).add(0.5, 0, 0.5);
            Vector3 target = at.clone().add(getFocusRelayOffset(altar.getAltarType()));

            RenderOffsetNoisePlane plane = recipe.getEffectContained(INDEX_NOISE_PLANE_LAYER1,
                    i -> new RenderOffsetNoisePlane(1.2F));
            for (int i = 0; i < 3; i++) {
                FXFacingParticle p = plane.createParticle(target);
                p.alpha(VFXAlphaFunction.FADE_OUT)
                        .setMotion(new Vector3(
                                rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1),
                                rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1),
                                rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1)))
                        .setScaleMultiplier(0.15F + rand.nextFloat() * 0.1F)
                        .setMaxAge(20 + rand.nextInt(15));
            }

            plane = recipe.getEffectContained(INDEX_NOISE_PLANE_LAYER2,
                    i -> new RenderOffsetNoisePlane(1.6F));
            for (int i = 0; i < 3; i++) {
                FXFacingParticle p = plane.createParticle(target);
                p.alpha(VFXAlphaFunction.FADE_OUT)
                        .setMotion(new Vector3(
                                rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1),
                                rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1),
                                rand.nextFloat() * 0.005 * (rand.nextBoolean() ? 1 : -1)))
                        .setScaleMultiplier(0.15F + rand.nextFloat() * 0.1F)
                        .setMaxAge(20 + rand.nextInt(15));
            }

            double scale = getRandomPillarOffset(altar.getAltarType()).getX();
            double edgeScale = (scale * 2 + 1);
            for (int i = 0; i < 2; i++) {
                Vector3 pos = new Vector3(altar).add(-scale + rand.nextFloat() * edgeScale, 0.02, -scale + rand.nextFloat() * edgeScale);
                Vector3 mot = pos.vectorFromHereTo(target).normalize().multiply(0.1F);

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(pos)
                        .alpha(VFXAlphaFunction.FADE_OUT)
                        .color(VFXColorFunction.WHITE)
                        .setScaleMultiplier(0.2F + rand.nextFloat() * 0.1F)
                        .setMotion(mot);
            }

            for (int i = 0; i < 2; i++) {
                Vector3 pos = target.clone().add(Vector3.random().multiply(4F));
                Vector3 dst = pos.vectorFromHereTo(target);
                Vector3 mot = dst.clone().multiply(dst.length() / 250);

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(pos)
                        .alpha(VFXAlphaFunction.PYRAMID)
                        .color(VFXColorFunction.WHITE)
                        .setScaleMultiplier(0.2F + rand.nextFloat() * 0.1F)
                        .setMotion(mot);
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
