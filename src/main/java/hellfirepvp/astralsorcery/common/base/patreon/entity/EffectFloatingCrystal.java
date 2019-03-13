/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.entity;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.models.obj.OBJModelLibrary;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.TextureQuery;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectFloatingCrystal
 * Created by HellFirePvP
 * Date: 14.07.2018 / 19:38
 */
public class EffectFloatingCrystal extends EntityComplexFX {

    private static int dlCrystal = -1;

    private RefreshFunction refreshFunction;
    private PositionController positionUpdateFunction;
    private AbstractRenderableTexture texCrystal = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "crystal_big_blue");
    private Color colorTheme = Color.WHITE;

    private double x, y, z;
    private double prevX, prevY, prevZ;

    public EffectFloatingCrystal setRefreshFunc(RefreshFunction func) {
        this.refreshFunction = func;
        return this;
    }

    public EffectFloatingCrystal setTexture(TextureQuery query) {
        this.texCrystal = query.resolve();
        return this;
    }

    public EffectFloatingCrystal setColorTheme(Color colorTheme) {
        this.colorTheme = colorTheme;
        return this;
    }

    public EffectFloatingCrystal setPositionUpdateFunction(PositionController positionUpdateFunction) {
        this.positionUpdateFunction = positionUpdateFunction;
        return this;
    }

    @Override
    public void tick() {
        super.tick();

        if(maxAge >= 0 && age >= maxAge) {
            if(refreshFunction != null) {
                Entity rView = Minecraft.getMinecraft().getRenderViewEntity();
                if(rView == null) rView = Minecraft.getMinecraft().player;
                if(rView.getDistanceSq(x, y, z) <= Config.maxEffectRenderDistanceSq) {
                    if(refreshFunction.shouldRefresh()) {
                        age = 0;
                    }
                }
            }
        }
        if(positionUpdateFunction != null) {
            this.prevX = this.x;
            this.prevY = this.y;
            this.prevZ = this.z;
            Vector3 newPos = positionUpdateFunction.updatePosition(this, new Vector3(x, y, z), new Vector3());
            this.x = newPos.getX();
            this.y = newPos.getY();
            this.z = newPos.getZ();
        }
    }

    @Override
    public void render(float pTicks) {
        Vector3 tr = RenderingUtils.getStandartTranslationRemovalVector(pTicks);

        double iX = RenderingUtils.interpolate(prevX, x, pTicks);
        double iY = RenderingUtils.interpolate(prevY, y, pTicks);
        double iZ = RenderingUtils.interpolate(prevZ, z, pTicks);
        long seed = 0x515F1EB654AB915EL;
        RenderingUtils.renderLightRayEffects(iX + tr.getX(), iY + 0.2 + tr.getY(), iZ + tr.getZ(),
                this.colorTheme, seed, ClientScheduler.getClientTick(),
                10, 1.4F, 50, 25);

        GlStateManager.pushMatrix();
        RenderingUtils.removeStandartTranslationFromTESRMatrix(pTicks);
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();
        GlStateManager.disableCull();
        GlStateManager.enableTexture2D();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.color(1F, 1F, 1F, 1F);

        GlStateManager.translate(iX, iY, iZ);
        renderTile(texCrystal);

        GlStateManager.color(1F, 1F, 1F, 1F);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableBlend();
        Blending.DEFAULT.applyStateManager();
        GlStateManager.enableAlpha();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    private static void renderTile(AbstractRenderableTexture tex) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.05F, 0.05F, 0.05F);
        tex.bindTexture();
        if(dlCrystal == -1) {
            dlCrystal = GLAllocation.generateDisplayLists(1);
            GlStateManager.glNewList(dlCrystal, GL11.GL_COMPILE);
            OBJModelLibrary.bigCrystal.renderAll(true);
            GlStateManager.glEndList();
        }
        GlStateManager.callList(dlCrystal);

        GlStateManager.popMatrix();
    }

}
