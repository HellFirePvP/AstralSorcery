/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import hellfirepvp.astralsorcery.client.util.RenderLightFanUtil;
import hellfirepvp.astralsorcery.common.entity.ItemEntityHighlighted;
import hellfirepvp.astralsorcery.common.entity.ItemEntityReplacement;
import hellfirepvp.astralsorcery.common.lib.EntitiesAS;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderItemEntityHighlighted
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderItemEntityHighlighted extends ItemEntityRenderer {

    private final ItemRenderer itemRenderer;
    private final RandomSource random = RandomSource.create();

    public RenderItemEntityHighlighted(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ItemEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity instanceof ItemEntityHighlighted highlighted && highlighted.hasColor()) {
            highlighted.getColor().ifPresent(color -> {
                ItemStack stack = entity.getItem();
                this.random.setSeed(getSeedForItemStack(stack));
                BakedModel bakedmodel = this.itemRenderer.getModel(stack, entity.level(), null, entity.getId());
                boolean shouldBob = IClientItemExtensions.of(stack).shouldBobAsEntity(stack);
                float bobOffset = shouldBob ? Mth.sin(((float) entity.tickCount + partialTicks) / 10 + entity.bobOffs) * 0.1F + 0.1F : 0;
                float modelOffset = bakedmodel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();

                poseStack.pushPose();
                poseStack.translate(0, bobOffset + 0.065F * (1 / modelOffset), 0);
                RenderLightFanUtil.renderLightFan(poseStack, 160420L + entity.getId(), entity.tickCount, partialTicks, buffer,
                        color.getColor(), 16, 12, 15);
                poseStack.popPose();
            });
        }

        ItemEntityReplacement replaced = ItemEntityReplacement.replace(EntitiesAS.ITEM_HIGHLIGHTED.get(), entity);
        replaced.age = entity.tickCount;
        super.render(replaced, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
