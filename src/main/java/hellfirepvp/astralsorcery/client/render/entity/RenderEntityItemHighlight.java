package hellfirepvp.astralsorcery.client.render.entity;

import hellfirepvp.astralsorcery.common.entities.EntityItemHighlighted;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityItemHighlight
 * Created by HellFirePvP
 * Date: 13.05.2016 / 13:59
 */
public class RenderEntityItemHighlight extends Render<EntityItemHighlighted> {

    private static final Field ageField = ReflectionHelper.findField(EntityItem.class, "age", "field_70292_b");

    private static final Random rand = new Random();
    private final RenderEntityItem renderItem;

    public RenderEntityItemHighlight(RenderManager renderManager) {
        super(renderManager);
        renderItem = new RenderEntityItem(renderManager, Minecraft.getMinecraft().getRenderItem());
    }

    @Override
    public void doRender(EntityItemHighlighted entity, double x, double y, double z, float entityYaw, float partialTicks) {
        rand.setSeed(16024L);

        float entityPartialYOffset = MathHelper.sin((entity.getAge() + partialTicks) / 10.0F + entity.hoverStart) * 0.1F + 0.1F;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y + entityPartialYOffset + 0.35F, (float) z);

        int fancy_count = !FMLClientHandler.instance().getClient().gameSettings.fancyGraphics ? 5 : 20;

        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();

        RenderHelper.disableStandardItemLighting();
        float f1 = entity.getAge() / 500.0F;
        float f2 = 0.0F;

        Color effectColor = entity.getHighlightColor();

        Random random = new Random(245L);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(false);
        GL11.glPushMatrix();
        for (int i = 0; i < fancy_count; i++) {
            GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(random.nextFloat() * 360.0F + f1 * 360.0F, 0.0F, 0.0F, 1.0F);
            vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
            float fa = random.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
            float f4 = random.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
            fa /= 30.0F / (Math.min(entity.getAge(), 10) / 10.0F);
            f4 /= 30.0F / (Math.min(entity.getAge(), 10) / 10.0F);
            vb.pos(0, 0, 0).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), (int) (255.0F * (1.0F - f2))).endVertex();
            vb.pos(-0.866D * f4, fa, -0.5F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            vb.pos(0.866D * f4, fa, -0.5F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            vb.pos(0.0D, fa, 1.0F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            vb.pos(-0.866D * f4, fa, -0.5F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            tes.draw();
        }
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();

        GL11.glPushMatrix();
        ItemStack stack = entity.getEntityItem();
        if (stack != null) {
            EntityItem ei = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, stack);
            if (ageField != null) {
                try {
                    ageField.set(ei, entity.getAge());
                } catch (IllegalAccessException ignored) {}
            }
            ei.hoverStart = entity.hoverStart;

            renderItem.doRender(ei, x, y, z, entityYaw, partialTicks);
        }
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityItemHighlighted entity) {
        return null;
    }

    public static class Factory implements IRenderFactory<EntityItemHighlighted> {

        @Override
        public Render<? super EntityItemHighlighted> createRenderFor(RenderManager manager) {
            return new RenderEntityItemHighlight(manager);
        }

    }

}
