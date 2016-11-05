package hellfirepvp.astralsorcery.client.render.entity;

import hellfirepvp.astralsorcery.client.models.tcn.TCNModelGrindstone;
import hellfirepvp.astralsorcery.client.render.RenderEntityModel;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.entities.EntityGrindstone;
import hellfirepvp.astralsorcery.common.item.base.IGrindable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityGrindstone
 * Created by HellFirePvP
 * Date: 13.09.2016 / 13:15
 */
public class RenderEntityGrindstone<T extends EntityGrindstone> extends RenderEntityModel<T> {

    private static final TCNModelGrindstone telescopeGrindstone = new TCNModelGrindstone();
    private static final BindableResource texGrindstone = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "grindstone");

    protected RenderEntityGrindstone(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y + 2.38, z);
        GL11.glScaled(0.1, 0.1, 0.1);
        GL11.glRotated(180, 1, 0, 0);
        doModelRender(entity);
        GL11.glPopMatrix();
        GL11.glPopAttrib();


        ItemStack grind = entity.getGrindItem();
        if(grind != null) {
            if(grind.getItem() != null) {
                if(grind.getItem() instanceof IGrindable) {
                    IGrindable grindable = (IGrindable) grind.getItem();
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    GL11.glPushMatrix();
                    GL11.glTranslated(x, y, z);
                    grindable.applyClientGrindstoneTransforms();
                    RenderHelper.enableStandardItemLighting();
                    Minecraft.getMinecraft().getRenderItem().renderItem(grind, ItemCameraTransforms.TransformType.GROUND);
                    RenderHelper.disableStandardItemLighting();
                    GL11.glPopMatrix();
                    GL11.glPopAttrib();
                }
                if(grind.getItem() instanceof ItemSword) {
                    GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                    GL11.glPushMatrix();
                    GL11.glTranslated(x, y, z);
                    GL11.glTranslated(0.3, 0.8, -0.3);
                    GL11.glRotated(65, 1, 0, 0);
                    GL11.glRotated(140, 0, 1, 0);
                    RenderHelper.enableStandardItemLighting();
                    Minecraft.getMinecraft().getRenderItem().renderItem(grind, ItemCameraTransforms.TransformType.GROUND);
                    RenderHelper.disableStandardItemLighting();
                    GL11.glPopMatrix();
                    GL11.glPopAttrib();
                }
            }
        }
    }

    @Override
    public void doModelRender(T entity) {
        texGrindstone.bind();
        telescopeGrindstone.render(entity, 0, 0, 0, 0, 0, 1);
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null; //LUL null.
    }

    public static class Factory extends RenderEntityModelFactory<EntityGrindstone> {

        @Override
        public RenderEntityModel<? super EntityGrindstone> createRenderFor(RenderManager manager) {
            return new RenderEntityGrindstone<>(manager);
        }

    }
}
