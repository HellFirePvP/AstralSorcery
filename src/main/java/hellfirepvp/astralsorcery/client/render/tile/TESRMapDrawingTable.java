/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.models.base.ASstarmapper;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.tile.TileMapDrawingTable;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRMapDrawingTable
 * Created by HellFirePvP
 * Date: 23.04.2017 / 22:39
 */
public class TESRMapDrawingTable extends TileEntitySpecialRenderer<TileMapDrawingTable> {

    private static List<BlockPos> requiredGlasses = new LinkedList<>();

    public static void renderRemainingGlasses(float pTicks) {
        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();

        //RenderHelper.disableStandardItemLighting();
        GlStateManager.pushMatrix();
        //GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(240.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
        GlStateManager.color(1F, 1F, 1F,1F);
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();
        GlStateManager.pushMatrix();
        RenderingUtils.removeStandartTranslationFromTESRMatrix(pTicks);

        texDrawingTable.bind();
        for (BlockPos p : requiredGlasses) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(p.getX() + 0.5, p.getY() + 1.5, p.getZ() + 0.5);
            GlStateManager.rotate(180, 1, 0, 0);
            GlStateManager.scale(0.0625, 0.0625, 0.0625);

            modelDrawingTable.treated_glass.render(1F);

            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
        requiredGlasses.clear();
        RenderHelper.disableStandardItemLighting();

        TextureHelper.refreshTextureBindState();
    }

    private static final ASstarmapper modelDrawingTable = new ASstarmapper();
    private static final BindableResource texDrawingTable = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "starmapper/astralsorcery_starmapper");

    @Override
    public void render(TileMapDrawingTable te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();

        if(!te.hasParchment() && !te.getSlotIn().isEmpty()) {
            ItemStack in = te.getSlotIn();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5, y + 0.85, z + 0.5);
            GlStateManager.scale(0.625F, 0.625F, 0.625F);

            Minecraft.getMinecraft().getRenderItem().renderItem(in, ItemCameraTransforms.TransformType.GROUND);
            GlStateManager.popMatrix();
            RenderHelper.disableStandardItemLighting();
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 1.5, z + 0.5);
        GlStateManager.rotate(180, 1, 0, 0);
        GlStateManager.scale(0.0625, 0.0625, 0.0625);
        GlStateManager.enableBlend();

        RenderHelper.disableStandardItemLighting();

        GlStateManager.pushMatrix();
        GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(165.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();

        texDrawingTable.bind();
        modelDrawingTable.render(null, te.hasParchment() ? 1 : 0, 0, 0, 0, 0, 1F);
        RenderHelper.disableStandardItemLighting();
        TextureHelper.refreshTextureBindState();
        GlStateManager.popMatrix();

        if(te.getPercRunning() > 1E-4) {
            GlStateManager.color(1F, 1F, 1F, te.getPercRunning());

            SpriteSheetResource halo = SpriteLibrary.spriteHalo2;
            halo.getResource().bind();
            Tuple<Double, Double> uvFrame = halo.getUVOffset(ClientScheduler.getClientTick());
            float rot = 360F - ((float) (ClientScheduler.getClientTick()     % 2000) / 2000F * 360F);

            GlStateManager.disableLighting();
            GlStateManager.pushMatrix();
            GlStateManager.disableCull();
            GlStateManager.disableAlpha();

            RenderingUtils.renderAngleRotatedTexturedRect(
                    new Vector3(0.5, 1.01, 0.5).add(te.getPos()),
                    Vector3.RotAxis.Y_AXIS, Math.toRadians(rot), 1F,
                    uvFrame.key, uvFrame.value, halo.getULength(), halo.getVLength(), partialTicks);

            GlStateManager.enableCull();
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.enableLighting();
        }

        if(!te.getSlotGlassLens().isEmpty()) {
            requiredGlasses.add(te.getPos());
        }

        TextureHelper.refreshTextureBindState();
    }

}
