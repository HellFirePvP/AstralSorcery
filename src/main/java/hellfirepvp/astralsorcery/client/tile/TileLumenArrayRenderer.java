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
import com.mojang.math.Axis;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.util.RenderSpriteUtil;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.common.tile.TileLumenArray;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.tank.FluidTankView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileLumenArrayRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileLumenArrayRenderer implements BlockEntityRenderer<TileLumenArray> {

    private final ItemRenderer itemRenderer;

    public TileLumenArrayRenderer(ItemRenderer itemRenderer)  {
        this.itemRenderer = itemRenderer;
    }

    @Override
    public AABB getRenderBoundingBox(TileLumenArray blockEntity) {
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity).inflate(1, 2, 1);
    }

    @Override
    public void render(TileLumenArray lumenArray, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.renderItem(lumenArray, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        this.renderFluid(lumenArray, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private void renderItem(TileLumenArray lumenArray, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack stack = lumenArray.getTileData().getInventory().getStackInSlot(0);
        if (stack.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.55F, 0.5F);
        RenderingDrawUtil.renderFloatingItem(this.itemRenderer, lumenArray.getLevel(), lumenArray.getBlockPos().getX(),
                0.6F, stack, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }

    private void renderFluid(TileLumenArray lumenArray, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        FluidTankView tank = lumenArray.getTileData().getFluidTank();
        FluidStack contained = tank.getFluidInTank(0);
        if (contained.isEmpty()) return;

        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(contained.getFluidType());
        ColorWrapper color = ColorWrapper.transparent(ext.getTintColor(contained));
        color = color.copyWithAlpha(204);
        RenderType rendertype = RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS);
        TextureAtlasSprite tas = RenderSpriteUtil.getTexture(contained);
        VertexConsumer vb = bufferSource.getBuffer(rendertype);

        float height = (this.getTankMaxHeight() - this.getTankOffset()) / 16F;
        float yOffset = this.getTankOffset() / 16F;
        float fillPercent = (float) contained.getAmount() / tank.getTankCapacity(0);
        UVFrame uv = UVFrame.fromAtlasSprite(tas);

        float size = this.getTankSize();
        float midSize = size / 2F;

        Vector3 topStart = new Vector3(0.5F - (midSize / 16F), yOffset + fillPercent * height, 0.5F - (midSize / 16F));
        RenderingDrawUtil.renderNormalQuad(vb, poseStack,
                topStart, Vector3.x(size / 16F), Vector3.z(size / 16F),
                color, packedLight, packedOverlay, uv);

        RenderingDrawUtil.renderNormalQuad(vb, poseStack,
                new Vector3(0.5F - (midSize / 16F) + 0.001F, yOffset, 0.5F - (midSize / 16F) + 0.001F), Vector3.x(size / 16F - 0.002F), Vector3.y(fillPercent * height),
                color, packedLight, packedOverlay, uv.multiplyVHeight(fillPercent));
        RenderingDrawUtil.renderNormalQuad(vb, poseStack,
                new Vector3(0.5F - (midSize / 16F) + 0.001F, yOffset, 0.5F + (midSize / 16F) - 0.001F), Vector3.x(size / 16F - 0.002F), Vector3.y(fillPercent * height),
                color, packedLight, packedOverlay, uv.multiplyVHeight(fillPercent));

        RenderingDrawUtil.renderNormalQuad(vb, poseStack,
                new Vector3(0.5F - (midSize / 16F) + 0.001F, yOffset, 0.5F - (midSize / 16F) + 0.001F), Vector3.z(size / 16F - 0.002F), Vector3.y(fillPercent * height),
                color, packedLight, packedOverlay, uv.multiplyVHeight(fillPercent));
        RenderingDrawUtil.renderNormalQuad(vb, poseStack,
                new Vector3(0.5F + (midSize / 16F) - 0.001F, yOffset, 0.5F - (midSize / 16F) + 0.001F), Vector3.z(size / 16F - 0.002F), Vector3.y(fillPercent * height),
                color, packedLight, packedOverlay, uv.multiplyVHeight(fillPercent));
    }

    protected float getTankMaxHeight() {
        return 13F;
    }

    protected float getTankOffset() {
        return 6F;
    }

    protected float getTankSize() {
        return 6F;
    }
}
