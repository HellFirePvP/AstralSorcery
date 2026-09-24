/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.util.RenderConstellationUtil;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.level.DayTimeHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileAltarRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileAltarRenderer implements BlockEntityRenderer<TileAltar> {

    @Override
    public void render(TileAltar altar, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!altar.hasStructure()) return;
        altar.getTileData().getFocusedConstellation().ifPresent(cst -> {
            float dayAlpha = DayTimeHelper.getCurrentDaytimeDistribution(altar.getLevel()) * 0.6F;

            long tick = ClientProxy.getClientTick();
            float animTime = (tick + partialTick) * 0.0005F;
            float scale = 4.5F + Mth.sin(animTime);

            RenderConstellationUtil.drawConstellationInWorld(
                    cst,
                    poseStack,
                    bufferSource,
                    new Vector3(0.5F, 0.001F, 0.5F),
                    scale,
                    1.5F,
                    0.1F + dayAlpha * 0.7F
            );
            RenderUtil.finishDrawing(bufferSource);
        });
    }

    @Override
    public AABB getRenderBoundingBox(TileAltar blockEntity) {
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity)
                .inflate(4, 1, 4);
    }
}
