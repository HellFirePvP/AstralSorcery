/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import hellfirepvp.astralsorcery.client.helper.TranslucentBlockRenderHelper;
import hellfirepvp.astralsorcery.common.tile.TileTranslucentBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileTranslucentBlockRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileTranslucentBlockRenderer implements BlockEntityRenderer<TileTranslucentBlock> {

    @Override
    public void render(TileTranslucentBlock tile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState toRender = tile.getTileData().getStoredState();
        if (toRender.isAir()) toRender = Blocks.STONE.defaultBlockState();
        DyeColor color = tile.getTileData().getDyeColor();
        if (color == null) color = DyeColor.WHITE;

        TranslucentBlockRenderHelper.submitBlockState(color, toRender, poseStack);
    }
}
