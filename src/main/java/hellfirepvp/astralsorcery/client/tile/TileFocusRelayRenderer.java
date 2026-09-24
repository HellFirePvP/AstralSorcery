/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.common.tile.TileFocusRelay;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileFocusRelayRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileFocusRelayRenderer implements BlockEntityRenderer<TileFocusRelay> {

    private final ItemRenderer itemRenderer;

    public TileFocusRelayRenderer(ItemRenderer itemRenderer)  {
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(TileFocusRelay focusRelay, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.renderItem(focusRelay, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private void renderItem(TileFocusRelay focusRelay, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack stack = focusRelay.getTileData().getInventory().getStackInSlot(0);
        if (stack.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.35F, 0.5F);
        RenderingDrawUtil.renderFloatingItem(this.itemRenderer, focusRelay.getLevel(), focusRelay.getBlockPos().getX(),
                1F, stack, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
