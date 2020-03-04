/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.effect;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.resource.query.SpriteQuery;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectCustomTexture
 * Created by HellFirePvP
 * Date: 26.08.2019 / 19:18
 */
public abstract class EffectCustomTexture extends Effect {

    protected static final Random rand = new Random();
    private final Color colorAsObj;

    public EffectCustomTexture(EffectType type, Color color) {
        super(type, color.getRGB());
        this.colorAsObj = color;
    }

    public void attachEventListeners(IEventBus bus) {}

    public abstract SpriteQuery getSpriteQuery();

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderInventoryEffect(EffectInstance effect, DisplayEffectsScreen<?> gui, int x, int y, float z) {
        double wh = 18;
        double offsetX = x + 6;
        double offsetY = y + 7;
        float red =   ((float) this.colorAsObj.getRed())   / 255F;
        float green = ((float) this.colorAsObj.getGreen()) / 255F;
        float blue =  ((float) this.colorAsObj.getBlue())  / 255F;

        SpriteSheetResource ssr = getSpriteQuery().resolveSprite();
        ssr.bindTexture();

        Tuple<Float, Float> uvTpl = ssr.getUVOffset(ClientScheduler.getClientTick());
        double u = uvTpl.getA();
        double v = uvTpl.getB();

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        vb.pos(offsetX,      offsetY,      z)
                .tex(u, v)
                .color(red, green, blue, 1F).endVertex();
        vb.pos(offsetX,      offsetY + wh, z)
                .tex(u, v + ssr.getVLength())
                .color(red, green, blue, 1F).endVertex();
        vb.pos(offsetX + wh, offsetY + wh, z)
                .tex(u + ssr.getULength(), v + ssr.getVLength())
                .color(red, green, blue, 1F).endVertex();
        vb.pos(offsetX + wh, offsetY,      z)
                .tex(u + ssr.getULength(), v)
                .color(red, green, blue, 1F).endVertex();

        tes.draw();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderHUDEffect(EffectInstance effect, AbstractGui gui, int x, int y, float z, float alpha) {
        Tessellator tes = Tessellator.getInstance();
        double wh = 18;
        double offsetX = x + 3;
        double offsetY = y + 3;
        float red =   ((float) this.colorAsObj.getRed())   / 255F;
        float green = ((float) this.colorAsObj.getGreen()) / 255F;
        float blue =  ((float) this.colorAsObj.getBlue())  / 255F;

        SpriteSheetResource ssr = getSpriteQuery().resolveSprite();
        ssr.bindTexture();

        Tuple<Float, Float> uvTpl = ssr.getUVOffset(ClientScheduler.getClientTick());
        double u = uvTpl.getA();
        double v = uvTpl.getB();

        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        vb.pos(offsetX, offsetY, 0)
                .tex(u, v)
                .color(red, green, blue, alpha).endVertex();
        vb.pos(offsetX,offsetY + wh, 0)
                .tex(u, v + ssr.getVLength())
                .color(red, green, blue, alpha).endVertex();
        vb.pos(offsetX + wh, offsetY + wh, 0)
                .tex(u + ssr.getULength(), v + ssr.getVLength())
                .color(red, green, blue, alpha).endVertex();
        vb.pos(offsetX + wh, offsetY, 0)
                .tex(u + ssr.getULength(), v)
                .color(red, green, blue, alpha).endVertex();

        tes.draw();
    }
}
