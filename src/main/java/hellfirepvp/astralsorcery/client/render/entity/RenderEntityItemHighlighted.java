/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.common.entity.EntityItemHighlighted;
import net.minecraft.client.Minecraft;
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
    public void doRender(ItemEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (entity instanceof EntityItemHighlighted && ((EntityItemHighlighted) entity).hasColor()) {
            GlStateManager.enableBlend();
            RenderingDrawUtils.renderLightRayFan(x, y + 0.35, z,
                    ((EntityItemHighlighted) entity).getHighlightColor(), 160420L + entity.getEntityId(),
                    16, 12, 15);
            GlStateManager.disableBlend();
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public static class Factory implements IRenderFactory<EntityItemHighlighted> {

        @Override
        public EntityRenderer<? super EntityItemHighlighted> createRenderFor(EntityRendererManager manager) {
            return new RenderEntityItemHighlighted(manager);
        }
    }
}
