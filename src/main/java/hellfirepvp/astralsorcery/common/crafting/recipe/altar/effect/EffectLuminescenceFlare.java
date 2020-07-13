/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.recipe.altar.effect;

import com.mojang.blaze3d.matrix.MatrixStack;
import hellfirepvp.astralsorcery.client.effect.function.RefreshFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXSpritePlane;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectLuminescenceFlare
 * Created by HellFirePvP
 * Date: 27.09.2019 / 21:56
 */
public class EffectLuminescenceFlare extends AltarRecipeEffect {

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTick(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state) {
        ActiveSimpleAltarRecipe recipe = altar.getActiveRecipe();
        if (recipe != null && state == ActiveSimpleAltarRecipe.CraftingState.ACTIVE) {
            ResourceLocation recipeName = recipe.getRecipeToCraft().getId();

            FXSpritePlane spr = recipe.getEffectContained(INDEX_CRAFT_FLARE, i -> {
                return EffectHelper.of(EffectTemplatesAS.TEXTURE_SPRITE)
                        .spawn(new Vector3(altar).add(0.5, 0.04, 0.5))
                        .setSprite(SpritesAS.SPR_CRAFT_FLARE)
                        .setAxis(Vector3.RotAxis.Y_AXIS)
                        .setNoRotation(0)
                        .color(VFXColorFunction.constant(ColorsAS.EFFECT_CRAFT_FLARE))
                        .alpha(VFXAlphaFunction.fadeIn(30))
                        .setScaleMultiplier(9F)
                        .setAlphaMultiplier(0.65F)
                        .refresh(RefreshFunction.tileExistsAnd(altar,
                                (tAltar, fx) -> tAltar.getActiveRecipe() != null &&
                                        tAltar.getActiveRecipe().getState() == ActiveSimpleAltarRecipe.CraftingState.ACTIVE &&
                                        recipeName.equals(tAltar.getActiveRecipe().getRecipeToCraft().getId())));
            });

            EffectHelper.refresh(spr, EffectTemplatesAS.TEXTURE_SPRITE);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onTESR(TileAltar altar, ActiveSimpleAltarRecipe.CraftingState state, MatrixStack renderStack, IRenderTypeBuffer buffer, float pTicks, int combinedLight) {}

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onCraftingFinish(TileAltar altar, boolean isChaining) {}
}
