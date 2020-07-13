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
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectFocusEdge
 * Created by HellFirePvP
 * Date: 27.09.2019 / 21:06
 */
public class EffectFocusEdge extends AltarRecipeEffect implements IFocusEffect {

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTick(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state) {
        if (state == ActiveSimpleAltarRecipe.CraftingState.ACTIVE) {
            ActiveSimpleAltarRecipe recipe = altar.getActiveRecipe();
            if (recipe == null) {
                return; //Uh... what
            }
            IConstellation focus = recipe.getRecipeToCraft().getFocusConstellation();
            double offsetLength = getPillarOffset(altar.getAltarType(), 0).getX();
            double edgeLength = offsetLength * 2 + 1;

            float total = 200;
            float partDone = (getClientTick() % total) / total;

            float xSh = partDone >= 0.5F ? 1F : partDone / 0.5F;
            float zSh = partDone < 0.5F ? 1F : 1F - ((partDone - 0.5F) / 0.5F);

            Vector3 offset = new Vector3(altar).add(-offsetLength, 0.1, -offsetLength);
            offset.add(xSh * edgeLength, 0, zSh * edgeLength);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(offset)
                    .setScaleMultiplier(0.1F + rand.nextFloat() * 0.5F)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .color(VFXColorFunction.constant(getFocusColor(focus, rand)))
                    .setMaxAge(50);
            if (rand.nextInt(12) == 0) {
                EffectHelper.of(EffectTemplatesAS.LIGHTBEAM)
                        .spawn(offset)
                        .setup(offset.clone().addY(3 + rand.nextFloat() * 2), 1, 1)
                        .color(VFXColorFunction.constant(getFocusColor(focus, rand)))
                        .setMaxAge(48);
            }


            xSh = partDone < 0.5F ? partDone / 0.5F : 1F;
            zSh = partDone >= 0.5F ? 1F - ((partDone - 0.5F) / 0.5F) : 1F;

            offset = new Vector3(altar).add(offsetLength + 1, 0.1, offsetLength + 1);
            offset.add(-xSh * edgeLength, 0, -zSh * edgeLength);

            EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                    .spawn(offset)
                    .setScaleMultiplier(0.1F + rand.nextFloat() * 0.5F)
                    .alpha(VFXAlphaFunction.FADE_OUT)
                    .color(VFXColorFunction.constant(getFocusColor(focus, rand)))
                    .setMaxAge(50);
            if (rand.nextInt(12) == 0) {
                EffectHelper.of(EffectTemplatesAS.LIGHTBEAM)
                        .spawn(offset)
                        .setup(offset.clone().addY(3 + rand.nextFloat() * 2), 1, 1)
                        .color(VFXColorFunction.constant(getFocusColor(focus, rand)))
                        .setMaxAge(48);
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
