/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import hellfirepvp.astralsorcery.client.model.builtin.ModelLens;
import hellfirepvp.astralsorcery.client.model.builtin.ModelLensColored;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.tile.TileLens;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderLens
 * Created by HellFirePvP
 * Date: 21.09.2019 / 15:01
 */
public class RenderLens extends CustomTileEntityRenderer<TileLens> {

    private static final ModelLens MODEL_LENS = new ModelLens();
    private static final ModelLensColored MODEL_LENS_COLORED = new ModelLensColored();

    public RenderLens(TileEntityRendererDispatcher tileRenderer) {
        super(tileRenderer);
    }

    @Override
    public void render(TileLens tile, float pTicks, MatrixStack renderStack, IRenderTypeBuffer renderTypeBuffer, int combinedLight, int combinedOverlay) {
        List<BlockPos> linked = tile.getLinkedPositions();
        float degYaw = 0;
        float degPitch = 0;

        renderStack.push();
        switch (tile.getPlacedAgainst()) {
            case DOWN:
                if (!linked.isEmpty() && linked.size() == 1) {
                    BlockPos to = linked.get(0);
                    BlockPos from = tile.getTrPos();
                    Vector3 dir = new Vector3(to).subtract(new Vector3(from));

                    degPitch = (float) Math.atan2(dir.getY(), Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ()));

                    degYaw = (float) Math.atan2(dir.getX(), dir.getZ());

                    degYaw = 180F + (float) Math.toDegrees(-degYaw);
                    degPitch = (float) Math.toDegrees(degPitch);
                }

                renderStack.translate(0.5F, 1.5F, 0.5F);

                renderStack.rotate(Vector3f.XP.rotationDegrees(180));
                renderStack.rotate(Vector3f.YP.rotationDegrees(degYaw % 360));

                if (tile.getColorType() != null) {
                    renderStack.push();
                    renderStack.rotate(Vector3f.YP.rotationDegrees(180));
                    renderLensColored(renderStack, renderTypeBuffer, combinedLight, combinedOverlay, tile.getColorType().getColor(), -degPitch);
                    renderStack.pop();
                }
                renderLens(renderStack, renderTypeBuffer, combinedLight, combinedOverlay, degPitch);
                break;
            case UP:
                if (!linked.isEmpty() && linked.size() == 1) {
                    BlockPos to = linked.get(0);
                    BlockPos from = tile.getTrPos();
                    Vector3 dir = new Vector3(to).subtract(new Vector3(from));

                    degPitch = (float) Math.atan2(dir.getY(), Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ()));

                    degYaw = (float) Math.atan2(dir.getX(), dir.getZ());

                    degYaw = 180F + (float) Math.toDegrees(-degYaw);
                    degPitch = (float) Math.toDegrees(degPitch);
                }

                renderStack.translate(0.5F, -0.5F, 0.5F);

                renderStack.rotate(Vector3f.YP.rotationDegrees((-degYaw + 180) % 360));

                if (tile.getColorType() != null) {
                    renderStack.push();
                    renderStack.rotate(Vector3f.YP.rotationDegrees(180));
                    renderLensColored(renderStack, renderTypeBuffer, combinedLight, combinedOverlay, tile.getColorType().getColor(), degPitch);
                    renderStack.pop();
                }
                renderLens(renderStack, renderTypeBuffer, combinedLight, combinedOverlay, -degPitch);
                break;
            case NORTH:
                if (!linked.isEmpty() && linked.size() == 1) {
                    BlockPos to = linked.get(0);
                    BlockPos from = tile.getTrPos();
                    Vector3 dir = new Vector3(to).subtract(new Vector3(from));

                    degPitch = (float) Math.atan2(dir.getZ(), Math.sqrt(dir.getX() * dir.getX() + dir.getY() * dir.getY()));

                    degYaw = (float) Math.atan2(dir.getX(), dir.getY());

                    degYaw = 180F + (float) Math.toDegrees(-degYaw);
                    degPitch = (float) Math.toDegrees(degPitch);
                }

                renderStack.translate(0.5F, 0.5F, 1.5F);

                renderStack.rotate(Vector3f.XP.rotationDegrees(270));
                renderStack.rotate(Vector3f.YP.rotationDegrees((-degYaw + 180) % 360));

                if (tile.getColorType() != null) {
                    renderStack.push();
                    renderStack.rotate(Vector3f.YP.rotationDegrees(180));
                    renderLensColored(renderStack, renderTypeBuffer, combinedLight, combinedOverlay, tile.getColorType().getColor(), -degPitch);
                    renderStack.pop();
                }
                renderLens(renderStack, renderTypeBuffer, combinedLight, combinedOverlay, degPitch);
                break;
            case SOUTH:
                if (!linked.isEmpty() && linked.size() == 1) {
                    BlockPos to = linked.get(0);
                    BlockPos from = tile.getTrPos();
                    Vector3 dir = new Vector3(to).subtract(new Vector3(from));

                    degPitch = (float) Math.atan2(dir.getZ(), Math.sqrt(dir.getX() * dir.getX() + dir.getY() * dir.getY()));

                    degYaw = (float) Math.atan2(dir.getX(), dir.getY());

                    degYaw = 180F + (float) Math.toDegrees(-degYaw);
                    degPitch = (float) Math.toDegrees(degPitch);
                }

                renderStack.translate(0.5F, 0.5F, -0.5F);

                renderStack.rotate(Vector3f.XP.rotationDegrees(90));
                renderStack.rotate(Vector3f.YP.rotationDegrees(degYaw % 360));

                if (tile.getColorType() != null) {
                    renderStack.push();
                    renderStack.rotate(Vector3f.YP.rotationDegrees(180));
                    renderLensColored(renderStack, renderTypeBuffer, combinedLight, combinedOverlay, tile.getColorType().getColor(), degPitch);
                    renderStack.pop();
                }
                renderLens(renderStack, renderTypeBuffer, combinedLight, combinedOverlay, -degPitch);
                break;
            case WEST:
                if (!linked.isEmpty() && linked.size() == 1) {
                    BlockPos to = linked.get(0);
                    BlockPos from = tile.getTrPos();
                    Vector3 dir = new Vector3(to).subtract(new Vector3(from));

                    degPitch = (float) Math.atan2(dir.getX(), Math.sqrt(dir.getZ() * dir.getZ() + dir.getY() * dir.getY()));

                    degYaw = (float) Math.atan2(dir.getZ(), dir.getY());

                    degYaw = 180F + (float) Math.toDegrees(-degYaw);
                    degPitch = (float) Math.toDegrees(degPitch);
                }

                renderStack.translate(1.5F, 0.5F, 0.5F);

                renderStack.rotate(Vector3f.ZP.rotationDegrees(90));
                renderStack.rotate(Vector3f.YP.rotationDegrees((degYaw + 270 % 360)));

                if (tile.getColorType() != null) {
                    renderStack.push();
                    renderStack.rotate(Vector3f.YP.rotationDegrees(180));
                    renderLensColored(renderStack, renderTypeBuffer, combinedLight, combinedOverlay, tile.getColorType().getColor(), -degPitch);
                    renderStack.pop();
                }
                renderLens(renderStack, renderTypeBuffer, combinedLight, combinedOverlay, degPitch);
                break;
            case EAST:
                if (!linked.isEmpty() && linked.size() == 1) {
                    BlockPos to = linked.get(0);
                    BlockPos from = tile.getTrPos();
                    Vector3 dir = new Vector3(to).subtract(new Vector3(from));

                    degPitch = (float) Math.atan2(dir.getX(), Math.sqrt(dir.getZ() * dir.getZ() + dir.getY() * dir.getY()));

                    degYaw = (float) Math.atan2(dir.getZ(), dir.getY());

                    degYaw = 180F + (float) Math.toDegrees(-degYaw);
                    degPitch = (float) Math.toDegrees(degPitch);
                }

                renderStack.translate(-0.5F, 0.5F, 0.5F);

                renderStack.rotate(Vector3f.ZP.rotationDegrees(270));
                renderStack.rotate(Vector3f.YP.rotationDegrees((-degYaw + 90 % 360)));

                if (tile.getColorType() != null) {
                    renderStack.push();
                    renderStack.rotate(Vector3f.YP.rotationDegrees(180));
                    renderLensColored(renderStack, renderTypeBuffer, combinedLight, combinedOverlay, tile.getColorType().getColor(), degPitch);
                    renderStack.pop();
                }
                renderLens(renderStack, renderTypeBuffer, combinedLight, combinedOverlay, -degPitch);
                break;
            default:
                break;
        }
        renderStack.pop();
    }

    private void renderLensColored(MatrixStack renderStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay, Color c, float pitch) {
        MODEL_LENS_COLORED.glass.    rotateAngleX = pitch * 0.017453292F;
        MODEL_LENS_COLORED.fitting1. rotateAngleX = pitch * 0.017453292F;
        MODEL_LENS_COLORED.fitting2. rotateAngleX = pitch * 0.017453292F;
        MODEL_LENS_COLORED.detail1_1.rotateAngleX = pitch * 0.017453292F;
        MODEL_LENS_COLORED.detail1.  rotateAngleX = pitch * 0.017453292F;

        RenderType type = MODEL_LENS_COLORED.getGeneralType();
        IVertexBuilder buf = buffer.getBuffer(type);
        MODEL_LENS_COLORED.renderGlass(renderStack, buf, combinedLight, combinedOverlay, c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, 1F);
        MODEL_LENS_COLORED.render(renderStack, buf, combinedLight, combinedOverlay);
        RenderingUtils.refreshDrawing(buf, type);
    }

    private void renderLens(MatrixStack renderStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay, float pitch) {
        MODEL_LENS.lens.rotateAngleX = pitch * 0.017453292F;

        MODEL_LENS.render(renderStack, buffer.getBuffer(MODEL_LENS.getGeneralType()), combinedLight, combinedOverlay);
    }
}
