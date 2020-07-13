/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.common.entity.item.EntityItemHighlighted;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.entity.item.ItemEntity;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityItemHighlighted
 * Created by HellFirePvP
 * Date: 18.08.2019 / 10:37
 */
public class RenderEntityItemHighlighted extends ItemRenderer {

    protected RenderEntityItemHighlighted(EntityRendererManager renderManager) {
        super(renderManager, Minecraft.getInstance().getItemRenderer());
    }

    @Override
    public void render(ItemEntity entity, float entityYaw, float partialTicks, MatrixStack renderStack, IRenderTypeBuffer buffer, int packedLight) {
        if (entity instanceof EntityItemHighlighted && ((EntityItemHighlighted) entity).hasColor()) {
            renderStack.push();
            renderStack.translate(0, 0.35F, 0);
            RenderingDrawUtils.renderLightRayFan(renderStack, buffer,
                    ((EntityItemHighlighted) entity).getHighlightColor(), 160420L + entity.getEntityId(),
                    16, 12, 15);
            renderStack.pop();
        }

        super.render(entity, entityYaw, partialTicks, renderStack, buffer, packedLight);
    }

    public static class Factory implements IRenderFactory<EntityItemHighlighted> {

        @Override
        public EntityRenderer<? super EntityItemHighlighted> createRenderFor(EntityRendererManager manager) {
            return new RenderEntityItemHighlighted(manager);
        }
    }
}
