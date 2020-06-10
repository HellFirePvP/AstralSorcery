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
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BuiltInEffectTraitFocusCircle
 * Created by HellFirePvP
 * Date: 27.09.2019 / 21:37
 */
public class BuiltInEffectTraitFocusCircle extends AltarRecipeEffect {

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTick(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state) {
        if (state == ActiveSimpleAltarRecipe.CraftingState.ACTIVE) {
            Vector3 altarPos = new Vector3(altar);
            double scale = getRandomPillarOffset(altar.getAltarType()).getX();
            double edgeScale = (scale * 2 + 1);

            if (rand.nextInt(4) == 0) {
                Vector3 at = altarPos.clone().add(-scale + rand.nextFloat() * edgeScale, 0.01, -scale + rand.nextFloat() * edgeScale);

                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(at)
                        .color(VFXColorFunction.WHITE)
                        .alpha(VFXAlphaFunction.FADE_OUT)
                        .setScaleMultiplier(0.15F + rand.nextFloat() * 0.2F);
            }
            for (int i = 0; i < 1; i++) {
                Vector3 r = Vector3.random()
                        .setY(0)
                        .normalize()
                        .multiply(1.3F + rand.nextFloat() * 0.5F)
                        .add(new Vector3(altarPos).add(0.5, 2F + rand.nextFloat() * 0.4F, 0.5));
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(r)
                        .color(VFXColorFunction.WHITE)
                        .alpha(VFXAlphaFunction.FADE_OUT)
                        .setScaleMultiplier(0.1F + rand.nextFloat() * 0.2F);
            }
            for (int i = 0; i < 2; i++) {
                Vector3 r = Vector3.random()
                        .setY(0)
                        .normalize()
                        .multiply(2F + rand.nextFloat() * 0.5F)
                        .add(new Vector3(altarPos).add(0.5, 1.1F + rand.nextFloat() * 0.4F, 0.5));
                EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                        .spawn(r)
                        .color(VFXColorFunction.WHITE)
                        .alpha(VFXAlphaFunction.FADE_OUT)
                        .setScaleMultiplier(0.1F + rand.nextFloat() * 0.2F);
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
