/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.ColorizationHelper;
import hellfirepvp.astralsorcery.client.util.RenderingConstellationUtils;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.crafting.helper.WrappedIngredient;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderAltar
 * Created by HellFirePvP
 * Date: 28.09.2019 / 22:05
 */
public class RenderAltar extends CustomTileEntityRenderer<TileAltar> {

    public RenderAltar(TileEntityRendererDispatcher tileRenderer) {
        super(tileRenderer);
    }

    @Override
    public void render(TileAltar tile, float pTicks, MatrixStack renderStack, IRenderTypeBuffer renderTypeBuffer, int combinedLight, int combinedOverlay) {
        if (tile.getAltarType().isThisGEThan(AltarType.RADIANCE) && tile.hasMultiblock()) {
            IConstellation cst = tile.getFocusedConstellation();
            if (cst != null) {
                float dayAlpha = DayTimeHelper.getCurrentDaytimeDistribution(tile.getWorld()) * 0.6F;

                int max = 3000;
                int t = (int) (ClientScheduler.getClientTick() % max);
                float halfAge = max / 2F;
                float tr = 1F - (Math.abs(halfAge - t) / halfAge);
                tr *= 1.3;

                RenderingConstellationUtils.renderConstellationIntoWorldFlat(
                        cst, renderStack, renderTypeBuffer,
                        new Vector3(0.5, 0.03, 0.5),
                        5.5 + tr,
                        2,
                        0.1F + dayAlpha);
            }
        }

        ActiveSimpleAltarRecipe recipe = tile.getActiveRecipe();
        if (recipe != null) {
            recipe.getRecipeToCraft().getCraftingEffects()
                    .forEach(effect -> effect.onTESR(tile, recipe.getState(), renderStack, renderTypeBuffer, pTicks, combinedLight));
        }

        if (tile.getAltarType().isThisGEThan(AltarType.RADIANCE)) {
            renderStack.push();
            renderStack.translate(0.5F, 4.5F, 0.5F);

            long id = tile.getPos().toLong();
            if (recipe != null) {
                List<WrappedIngredient> traitInputs = recipe.getRecipeToCraft().getRelayInputs();
                if (!traitInputs.isEmpty()) {
                    int amount = 60 / traitInputs.size();
                    for (int i = 0; i < traitInputs.size(); i++) {
                        WrappedIngredient ingredient = traitInputs.get(i);
                        ItemStack traitInput = ingredient.getRandomMatchingStack(ClientScheduler.getClientTick());
                        Color color = ColorizationHelper.getColor(traitInput).orElse(ColorsAS.CELESTIAL_CRYSTAL);

                        RenderingDrawUtils.renderLightRayFan(renderStack, renderTypeBuffer, color, 0x1231943167156902L | id | (i * 0x5151L), 20, 2F, amount);
                    }
                } else {
                    RenderingDrawUtils.renderLightRayFan(renderStack, renderTypeBuffer, Color.WHITE, id * 31L, 15, 1.5F, 35);
                    RenderingDrawUtils.renderLightRayFan(renderStack, renderTypeBuffer, ColorsAS.CELESTIAL_CRYSTAL, id * 16L, 10, 1F, 25);
                }
                RenderingDrawUtils.renderLightRayFan(renderStack, renderTypeBuffer, Color.WHITE, id * 31L, 10, 1F, 10);
            } else {
                RenderingDrawUtils.renderLightRayFan(renderStack, renderTypeBuffer, Color.WHITE, id * 31L, 15, 1.5F, 35);
                RenderingDrawUtils.renderLightRayFan(renderStack, renderTypeBuffer, ColorsAS.CELESTIAL_CRYSTAL, id * 16L, 10, 1F, 25);
            }

            renderStack.pop();
        }
    }
}
