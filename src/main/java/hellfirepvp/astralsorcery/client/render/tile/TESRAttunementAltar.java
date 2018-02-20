/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.models.base.ASaltarAT;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRAttunementAltar
 * Created by HellFirePvP
 * Date: 06.12.2016 / 22:03
 */
public class TESRAttunementAltar extends TileEntitySpecialRenderer<TileAttunementAltar> {

    private static final ASaltarAT modelAttunementAltar = new ASaltarAT();
    private static final BindableResource texModelAttunementAltar = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "base/altarattunement");

    @Override
    public void render(TileAttunementAltar te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        GL11.glScaled(0.0625, 0.0625, 0.0625);
        GL11.glRotated(180, 1, 0, 0);

        GlStateManager.pushMatrix();
        GlStateManager.rotate(165.0F, 1.0F, 0.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();

        texModelAttunementAltar.bind();
        modelAttunementAltar.renderBase();
        GL11.glPopMatrix();

        float startY = -1.5F;
        float endY   = -0.5F;
        float tickPartY = (endY - startY) / ((float) TileAttunementAltar.MAX_START_ANIMATION_TICK);

        float prevPosY = endY + (te.prevActivationTick * tickPartY);
        float posY     = endY + (te.activationTick     * tickPartY);
        double framePosY = RenderingUtils.interpolate(prevPosY, posY, partialTicks);

        double generalAnimationTick = ClientScheduler.getClientTick() / 4D;
        if(te.animate) {
            if(te.tesrLocked) {
                te.tesrLocked = false;
            }
        } else {
            if(te.tesrLocked) {
                generalAnimationTick = 7.25D;
            } else {
                if(Math.abs((generalAnimationTick % TileAttunementAltar.MAX_START_ANIMATION_SPIN) - 7.25D) <= 0.3125) {
                    generalAnimationTick = 7.25D;
                    te.tesrLocked = true;
                }
            }
        }

        float spinDur = TileAttunementAltar.MAX_START_ANIMATION_SPIN;

        for (int i = 1; i < 9; i++) {
            float incrementer = (spinDur / 8) * i;

            double aFrame =     generalAnimationTick + incrementer;
            double prevAFrame = generalAnimationTick + incrementer - 1;
            double renderFrame = RenderingUtils.interpolate(prevAFrame, aFrame, 0);

            double partRenderFrame = (renderFrame % spinDur) / spinDur;
            float normalized = (float) (partRenderFrame * 2F * Math.PI);

            float xOffset = MathHelper.cos(normalized);
            float zOffset = MathHelper.sin(normalized);

            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5, y + framePosY, z + 0.5);
            GL11.glScaled(0.0625, 0.0625, 0.0625);
            GL11.glRotated(180, 1, 0, 0);

            modelAttunementAltar.renderHovering(xOffset, zOffset,
                    (float) RenderingUtils.interpolate(
                            ((float) te.prevActivationTick) / TileAttunementAltar.MAX_START_ANIMATION_TICK,
                            ((float) te.activationTick    ) / TileAttunementAltar.MAX_START_ANIMATION_TICK,
                            partialTicks));

            GL11.glPopMatrix();
        }

        RenderHelper.disableStandardItemLighting();

        GL11.glPopAttrib();
    }

}
