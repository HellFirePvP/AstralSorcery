/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.fx;

import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.IComplexEffect;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityFXFacingSprite
 * Created by HellFirePvP
 * Date: 17.09.2016 / 23:35
 */
public abstract class EntityFXFacingSprite extends EntityComplexFX implements IComplexEffect.PreventRemoval {

    private final SpriteSheetResource spriteSheet;
    private double x, y, z;
    private double prevX, prevY, prevZ;
    private final float scale;
    private Color overlayColor = Color.WHITE;

    private RefreshFunction refreshFunction;
    private PositionController positionUpdateFunction;

    public EntityFXFacingSprite(SpriteSheetResource spriteSheet, double x, double y, double z) {
        this(spriteSheet, x, y, z, 1F);
    }

    public EntityFXFacingSprite(SpriteSheetResource spriteSheet, double x, double y, double z, float scale) {
        this.spriteSheet = spriteSheet;
        this.x = x;
        this.y = y;
        this.z = z;
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.scale = scale;
        this.maxAge = spriteSheet.getFrameCount();
    }

    public EntityFXFacingSprite updatePosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public void setOverlayColor(@Nonnull Color overlayColor) {
        this.overlayColor = overlayColor;
    }

    public static EntityFXFacingSprite fromSpriteSheet(SpriteSheetResource res, double x, double y, double z, float scale, int rLayer) {
        return new EntityFXFacingSprite(res, x, y, z, scale) {

            @Override
            public int getLayer() {
                return rLayer;
            }

        };
    }

    public EntityFXFacingSprite setPositionUpdateFunction(PositionController positionUpdateFunction) {
        this.positionUpdateFunction = positionUpdateFunction;
        return this;
    }

    public EntityFXFacingSprite setRefreshFunc(RefreshFunction func) {
        this.refreshFunction = func;
        return this;
    }

    protected float getULengthMultiplier() {
        return 1F;
    }

    protected float getVLengthMultiplier() {
        return 1F;
    }

    protected int getAgeBasedFrame() {
        float perc = ((float) age) / ((float) maxAge);
        return MathHelper.floor(spriteSheet.getFrameCount() * perc);
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
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();
        GlStateManager.disableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(false);
        Color c = this.overlayColor;
        GlStateManager.color(1F, 1F, 1F, 1F);
        int frame = getAgeBasedFrame();
        Tuple<Double, Double> uv = spriteSheet.getUVOffset(frame);
        spriteSheet.getResource().bind();
        double iX = RenderingUtils.interpolate(prevX, x, pTicks);
        double iY = RenderingUtils.interpolate(prevY, y, pTicks);
        double iZ = RenderingUtils.interpolate(prevZ, z, pTicks);
        RenderingUtils.renderFacingColoredQuad(iX, iY, iZ, pTicks, scale, 0, uv.key, uv.value, spriteSheet.getULength() * getULengthMultiplier(), spriteSheet.getVLength() * getVLengthMultiplier(), c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
        GlStateManager.disableBlend();
        Blending.DEFAULT.applyStateManager();
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
    }

}
