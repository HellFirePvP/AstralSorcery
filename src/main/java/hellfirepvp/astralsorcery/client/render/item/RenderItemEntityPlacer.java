package hellfirepvp.astralsorcery.client.render.item;

import hellfirepvp.astralsorcery.client.render.RenderEntityModel;
import hellfirepvp.astralsorcery.client.render.entity.RenderEntityGrindstone;
import hellfirepvp.astralsorcery.client.render.entity.RenderEntityTelescope;
import hellfirepvp.astralsorcery.client.util.item.IItemRenderer;
import hellfirepvp.astralsorcery.common.entities.EntityGrindstone;
import hellfirepvp.astralsorcery.common.entities.EntityTelescope;
import hellfirepvp.astralsorcery.common.item.ItemEntityPlacer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderItemEntityPlacer
 * Created by HellFirePvP
 * Date: 31.07.2016 / 18:00
 */
public class RenderItemEntityPlacer implements IItemRenderer {

    private static final RenderEntityModel<? super EntityTelescope> renderTelescope = new RenderEntityTelescope.Factory().createRenderFor(Minecraft.getMinecraft().getRenderManager());
    private static final RenderEntityModel<? super EntityGrindstone> renderGrindstone = new RenderEntityGrindstone.Factory().createRenderFor(Minecraft.getMinecraft().getRenderManager());

    private static final EntityTelescope telescopeDummy = new EntityTelescope(Minecraft.getMinecraft().theWorld);
    private static final EntityGrindstone grindstoneDummy = new EntityGrindstone(Minecraft.getMinecraft().theWorld);

    @Override
    public void render(ItemStack stack) {
        int meta = stack.getItemDamage();
        if(meta < 0 || meta >= ItemEntityPlacer.PlacerType.values().length) {
            return;
        }
        ItemEntityPlacer.PlacerType type = ItemEntityPlacer.PlacerType.values()[meta];
        switch (type) {
            case TELESCOPE:
                GL11.glPushMatrix();
                GL11.glScaled(0.028, 0.028, 0.028);
                GL11.glRotated(180, 0, 1, 0);
                GL11.glRotated(180, 1, 0, 0);
                GL11.glTranslated(-20, -25, 0);
                GL11.glPushMatrix();
                GL11.glRotated(-45, 0, 1, 0);
                renderTelescope.doModelRender(telescopeDummy);
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                break;
            case GRINDSTONE:
                GL11.glPushMatrix();
                GL11.glScaled(0.05, 0.05, 0.05);
                GL11.glRotated(200, 0, 1, 0);
                GL11.glRotated(180, 1, 0, 0);
                GL11.glTranslated(-10, -26, 0);
                GL11.glPushMatrix();
                GL11.glRotated(-45, 0, 1, 0);
                renderGrindstone.doModelRender(grindstoneDummy);
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                break;
        }
    }

}
