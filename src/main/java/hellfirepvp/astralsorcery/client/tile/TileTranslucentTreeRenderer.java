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
import hellfirepvp.astralsorcery.common.tile.TileTranslucentTree;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileTranslucentBlockRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileTranslucentTreeRenderer implements BlockEntityRenderer<TileTranslucentTree> {

    @Override
    public void render(TileTranslucentTree tile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState toRender = tile.getTileData().getStoredState();
        if (toRender.isAir()) return;

        TranslucentBlockRenderHelper.submitBlockState(DyeColor.WHITE, toRender, poseStack);
    }
}
