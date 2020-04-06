/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.render.ObjModelRender;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.resource.query.TextureQuery;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import hellfirepvp.astralsorcery.client.util.obj.WavefrontObject;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXCrystal
 * Created by HellFirePvP
 * Date: 05.04.2020 / 09:53
 */
public class FXCrystal extends EntityVisualFX {

    private AbstractRenderableTexture alternativeTexture = null;
    private Color lightRayColor = null;
    private Vector3 rotation = new Vector3();

    public FXCrystal(Vector3 pos) {
        super(pos);
    }

    public FXCrystal rotation(float x, float y, float z) {
        this.rotation = new Vector3(x, y, z);
        return this;
    }

    public FXCrystal setLightRayColor(Color lightRayColor) {
        this.lightRayColor = lightRayColor;
        return this;
    }

    public FXCrystal setTexture(TextureQuery query) {
        this.alternativeTexture = query.resolve();
        return this;
    }

    @Override
    public <T extends EntityVisualFX> void render(BatchRenderContext<T> ctx, BufferContext buf, float pTicks) {
        if (this.alternativeTexture != null) {
            this.alternativeTexture.bindTexture();
        }

        Vector3 vec = this.getRenderPosition(pTicks);
        float alpha = this.getAlpha(pTicks);
        float scale = this.getScale(pTicks);
        Color col = this.getColor(pTicks);
        float r = col.getRed() / 255F;
        float g = col.getGreen() / 255F;
        float b = col.getBlue() / 255F;

        if (this.lightRayColor != null) {
            long seed = 0x515F1EB654AB915EL;
            RenderingDrawUtils.renderLightRayFan(vec.getX(), vec.getY(), vec.getZ(),
                    this.lightRayColor, seed, 5, 1F, 50);
            RenderHelper.enableGUIStandardItemLighting();
        }

        GlStateManager.pushMatrix();
        GlStateManager.color4f(r, g, b, alpha);

        GlStateManager.translated(vec.getX(), vec.getY() - 0.125, vec.getZ());
        GlStateManager.scalef(scale, scale, scale);
        GlStateManager.rotated(rotation.getX(), 1, 0, 0);
        GlStateManager.rotated(rotation.getY(), 0, 1, 0);
        GlStateManager.rotated(rotation.getZ(), 0, 0, 1);

        ObjModelRender.renderCrystal();

        GlStateManager.popMatrix();

        if (this.alternativeTexture != null) {
            ctx.getSprite().bindTexture();
        }
    }
}
