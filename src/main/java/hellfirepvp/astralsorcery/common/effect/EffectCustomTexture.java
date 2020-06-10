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
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import org.lwjgl.opengl.GL11;

import java.awt.*;
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
        float wh = 18;
        float offsetX = x + 6;
        float offsetY = y + 7;
        float red =   ((float) this.colorAsObj.getRed())   / 255F;
        float green = ((float) this.colorAsObj.getGreen()) / 255F;
        float blue =  ((float) this.colorAsObj.getBlue())  / 255F;

        SpriteSheetResource ssr = getSpriteQuery().resolveSprite();
        ssr.bindTexture();

        Tuple<Float, Float> uvTpl = ssr.getUVOffset(ClientScheduler.getClientTick());
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            RenderingGuiUtils.rect(buf, offsetX, offsetY, z, wh, wh)
                    .color(red, green, blue, 1F)
                    .tex(uvTpl.getA(), uvTpl.getB(), ssr.getUWidth(), ssr.getVWidth())
                    .draw();
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderHUDEffect(EffectInstance effect, AbstractGui gui, int x, int y, float z, float alpha) {
        float wh = 18;
        float offsetX = x + 3;
        float offsetY = y + 3;
        float red =   ((float) this.colorAsObj.getRed())   / 255F;
        float green = ((float) this.colorAsObj.getGreen()) / 255F;
        float blue =  ((float) this.colorAsObj.getBlue())  / 255F;

        SpriteSheetResource ssr = getSpriteQuery().resolveSprite();
        ssr.bindTexture();

        Tuple<Float, Float> uvTpl = ssr.getUVOffset(ClientScheduler.getClientTick());
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            RenderingGuiUtils.rect(buf, offsetX, offsetY, z, wh, wh)
                    .color(red, green, blue, 1F)
                    .tex(uvTpl.getA(), uvTpl.getB(), ssr.getUWidth(), ssr.getVWidth())
                    .draw();
        });
    }
}
