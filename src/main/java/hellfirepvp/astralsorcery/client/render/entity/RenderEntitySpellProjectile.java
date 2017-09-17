/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import hellfirepvp.astralsorcery.common.constellation.spell.entity.SpellProjectile;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntitySpellProjectile
 * Created by HellFirePvP
 * Date: 18.07.2017 / 19:34
 */
public class RenderEntitySpellProjectile extends Render<SpellProjectile> {

    protected RenderEntitySpellProjectile(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(SpellProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks + 90F, 0.0F, 0.0F, 1.0F);
        RenderItem ri = Minecraft.getMinecraft().getRenderItem();
        //TODO change
        ri.renderItem(new ItemStack(ItemsAS.rockCrystal), ItemCameraTransforms.TransformType.GROUND);
        GlStateManager.popMatrix();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(SpellProjectile entity) {
        return null;
    }

    public static class Factory implements IRenderFactory<SpellProjectile> {

        @Override
        public Render<? super SpellProjectile> createRenderFor(RenderManager manager) {
            return new RenderEntitySpellProjectile(manager);
        }

    }

}
