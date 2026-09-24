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
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.util.RenderSpriteUtil;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.common.tile.TileLumenCrystallizer;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.tank.FluidTankView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileLumenCrystallizerRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileLumenCrystallizerRenderer implements BlockEntityRenderer<TileLumenCrystallizer> {

    @Override
    public void render(TileLumenCrystallizer crystallizer, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.renderItem(crystallizer, poseStack, bufferSource, packedLight, packedOverlay);
        this.renderFluid(crystallizer, poseStack, bufferSource, packedLight, packedOverlay);
    }

    private void renderFluid(TileLumenCrystallizer crystallizer, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        FluidTankView tank = crystallizer.getTileData().getFluidTank();
        FluidStack contained = tank.getFluidInTank(0);
        if (contained.isEmpty()) return;

        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(contained.getFluidType());
        ColorWrapper color = ColorWrapper.transparent(ext.getTintColor(contained));
        RenderType rendertype = RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS);
        TextureAtlasSprite tas = RenderSpriteUtil.getTexture(contained);
        VertexConsumer vb = bufferSource.getBuffer(rendertype);
        UVFrame uv = UVFrame.fromAtlasSprite(tas);

        Vector3 offset = Vector3.y(15.5F / 16F)
                .add(1F / 16F, 0, 1F / 16F);
        RenderingDrawUtil.renderNormalQuad(vb, poseStack,
                offset, Vector3.x(14F / 16F), Vector3.z(14F / 16F),
                color, packedLight, packedOverlay, uv);
    }

    private void renderItem(TileLumenCrystallizer crystallizer, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        ItemStack contained = crystallizer.getTileData().getInventory().getStackInSlot(0);
        if (contained.isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.97, 0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.scale(0.5F, 0.5F, 0.5F);

        ItemRenderer ir = Minecraft.getInstance().getItemRenderer();
        ir.renderStatic(contained, ItemDisplayContext.FIXED,
                packedLight, OverlayTexture.NO_OVERLAY,
                poseStack, bufferSource, null,
                (int) MiscUtil.getBlockPosSeed(crystallizer.getBlockPos()));

        poseStack.popPose();
        RenderUtil.finishDrawing(bufferSource);
    }
}
