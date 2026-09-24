/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.util.RenderSpriteUtil;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.common.tile.TileLightwell;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.tank.FluidTankView;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileLightwellRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileLightwellRenderer implements BlockEntityRenderer<TileLightwell> {

    private final ItemRenderer itemRenderer;

    public TileLightwellRenderer(ItemRenderer itemRenderer)  {
        this.itemRenderer = itemRenderer;
    }

    @Override
    public AABB getRenderBoundingBox(TileLightwell blockEntity) {
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity).inflate(0, 1, 0);
    }

    @Override
    public void render(TileLightwell lightwell, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.renderItem(lightwell, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        this.renderFluid(lightwell, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private void renderItem(TileLightwell lightwell, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack stack = lightwell.getTileData().getInventory().getStackInSlot(0);
        if (stack.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0.5F, 1.2F, 0.5F);
        RenderingDrawUtil.renderFloatingItem(this.itemRenderer, lightwell.getLevel(), lightwell.getBlockPos().getX(),
                1F, stack, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }

    private void renderFluid(TileLightwell lightwell, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        FluidTankView tank = lightwell.getTileData().getFluidTank();
        FluidStack contained = tank.getFluidInTank(0);
        if (contained.isEmpty()) return;

        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(contained.getFluidType());
        ColorWrapper color = ColorWrapper.transparent(ext.getTintColor(contained));
        RenderType rendertype = RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS);
        TextureAtlasSprite tas = RenderSpriteUtil.getTexture(contained);
        VertexConsumer vb = bufferSource.getBuffer(rendertype);
        UVFrame uv = UVFrame.fromAtlasSprite(tas);

        float fillPercent = (float) contained.getAmount() / tank.getTankCapacity(0);
        Vector3 offset = Vector3.y(0.32D)
                .addY(fillPercent * 0.6)
                .add(2F / 16F, 0, 2F / 16F);

        RenderingDrawUtil.renderNormalQuad(vb, poseStack,
                offset, Vector3.x(12F / 16F), Vector3.z(12F / 16F),
                color, packedLight, packedOverlay, uv);
    }
}
