/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.crafting.helper.WrappedIngredient;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.ActiveSimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
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

    public RenderAltar(TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
    }

    @Override
    public void render(TileAltar tile, float pTicks, MatrixStack renderStack, IRenderTypeBuffer renderTypeBuffer, int combinedLight, int combinedOverlay) {
        renderTypeBuffer.getBuffer()
    }

    @Override
    public void render(TileAltar altar, double x, double y, double z, float pTicks, int destroyStage) {
        if (altar.getAltarType().isThisGEThan(AltarType.RADIANCE) && altar.hasMultiblock()) {
            IConstellation cst = altar.getFocusedConstellation();
            if (cst != null) {
                float dayAlpha = DayTimeHelper.getCurrentDaytimeDistribution(altar.getWorld()) * 0.6F;

                int max = 3000;
                int t = (int) (ClientScheduler.getClientTick() % max);
                float halfAge = max / 2F;
                float tr = 1F - (Math.abs(halfAge - t) / halfAge);
                tr *= 1.3;

                GlStateManager.disableAlphaTest();
                GlStateManager.enableBlend();
                Blending.DEFAULT.applyStateManager();
                RenderHelper.enableGUIStandardItemLighting();

                RenderingConstellationUtils.renderConstellationIntoWorldFlat(
                        cst,
                        new Vector3(x, y, z).add(0.5, 0.03, 0.5),
                        5.5 + tr,
                        2,
                        0.1F + dayAlpha);

                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableBlend();
                GlStateManager.enableAlphaTest();
            }
        }

        ActiveSimpleAltarRecipe recipe = altar.getActiveRecipe();
        if (recipe != null) {
            recipe.getRecipeToCraft().getCraftingEffects()
                    .forEach(effect -> effect.onTESR(altar, recipe.getState(), x, y, z, pTicks));

        }
        if (altar.getAltarType().isThisGEThan(AltarType.RADIANCE)) {
            long id = altar.getPos().toLong();
            GlStateManager.disableAlphaTest();
            GlStateManager.enableBlend();
            if (recipe != null) {
                List<WrappedIngredient> traitInputs = recipe.getRecipeToCraft().getRelayInputs();
                if (!traitInputs.isEmpty()) {
                    int amount = 60 / traitInputs.size();
                    for (int i = 0; i < traitInputs.size(); i++) {
                        WrappedIngredient ingredient = traitInputs.get(i);
                        ItemStack traitInput = ingredient.getRandomMatchingStack(ClientScheduler.getClientTick());
                        Color color = ColorizationHelper.getColor(traitInput)
                                .orElse(ColorsAS.CELESTIAL_CRYSTAL);

                        RenderingDrawUtils.renderLightRayFan(x + 0.5, y + 4.5, z + 0.5, color, 0x1231943167156902L | id | (i * 0x5151L), 20, 2F, amount);
                    }
                } else {
                    RenderingDrawUtils.renderLightRayFan(x + 0.5, y + 4.5, z + 0.5, Color.WHITE, id * 31L, 15, 1.5F, 35);
                    RenderingDrawUtils.renderLightRayFan(x + 0.5, y + 4.5, z + 0.5, ColorsAS.CELESTIAL_CRYSTAL, id * 16L, 10, 1F, 25);
                }
                RenderingDrawUtils.renderLightRayFan(x + 0.5, y + 4.5, z + 0.5, Color.WHITE, id * 31L, 10, 1F, 10);
            } else {
                RenderingDrawUtils.renderLightRayFan(x + 0.5, y + 4.5, z + 0.5, Color.WHITE, id * 31L, 15, 1.5F, 35);
                RenderingDrawUtils.renderLightRayFan(x + 0.5, y + 4.5, z + 0.5, ColorsAS.CELESTIAL_CRYSTAL, id * 16L, 10, 1F, 25);
            }
            GlStateManager.enableAlphaTest();
        }
    }
}
