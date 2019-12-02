/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.model.builtin.ModelAttunementAltar;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import net.minecraft.util.math.MathHelper;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderAttunementAltar
 * Created by HellFirePvP
 * Date: 17.11.2019 / 07:52
 */
public class RenderAttunementAltar extends CustomTileEntityRenderer<TileAttunementAltar> {

    private static final ModelAttunementAltar MODEL_ATTUNEMENT_ALTAR = new ModelAttunementAltar();
    private static final Supplier<AbstractRenderableTexture> MODEL_TEXTURE =
            AssetLibrary.loadReference(AssetLoader.TextureLocation.MODELS, "attunement_altar/attunement_altar_tesr");

    @Override
    public void render(TileAttunementAltar tile, double x, double y, double z, float pTicks, int destroyStage) {
        this.bindDamaged(MODEL_TEXTURE.get(), destroyStage);

        GlStateManager.pushMatrix();
        GlStateManager.translated(x + 0.5, y + 0.5, z + 0.5);
        GlStateManager.scaled(0.0625, 0.0625, 0.0625);
        GlStateManager.rotated(180, 1, 0, 0);
        MODEL_ATTUNEMENT_ALTAR.renderBase();
        GlStateManager.popMatrix();

        float spinDur = TileAttunementAltar.MAX_START_ANIMATION_SPIN;
        float spinStart = TileAttunementAltar.MAX_START_ANIMATION_TICK;

        float startY = -1.2F;
        float endY   = -0.5F;
        float tickPartY = (endY - startY) / spinStart;
        float prevPosY = endY + (tile.prevActivationTick * tickPartY);
        float posY     = endY + (tile.activationTick     * tickPartY);
        double framePosY = RenderingVectorUtils.interpolate(prevPosY, posY, pTicks);

        double generalAnimationTick = (ClientScheduler.getClientTick() + pTicks) / 4D;
        if (tile.animate) {
            if (tile.tesrLocked) {
                tile.tesrLocked = false;
            }
        } else {
            if (tile.tesrLocked) {
                generalAnimationTick = 7.25D;
            } else {
                if(Math.abs((generalAnimationTick % spinDur) - 7.25D) <= 0.3125) {
                    generalAnimationTick = 7.25D;
                    tile.tesrLocked = true;
                }
            }
        }

        GlStateManager.disableLighting();
        for (int i = 1; i < 9; i++) {
            float incrementer = (spinDur / 8F) * i;

            double aFrame =     generalAnimationTick + incrementer;
            double prevAFrame = generalAnimationTick + incrementer - 1;
            double renderFrame = RenderingVectorUtils.interpolate(prevAFrame, aFrame, 0);

            double partRenderFrame = (renderFrame % spinDur) / spinDur;
            float normalized = (float) (partRenderFrame * 2F * Math.PI);

            float xOffset = MathHelper.cos(normalized);
            float zOffset = MathHelper.sin(normalized);
            float rotation = (float) RenderingVectorUtils.interpolate(tile.prevActivationTick / spinStart, tile.activationTick / spinStart, pTicks);

            GlStateManager.pushMatrix();
            GlStateManager.translated(x + 0.5, y + framePosY, z + 0.5);
            GlStateManager.scaled(0.0625, 0.0625, 0.0625);
            GlStateManager.rotated(180, 1, 0, 0);
            MODEL_ATTUNEMENT_ALTAR.renderHovering(xOffset, zOffset, rotation);
            GlStateManager.popMatrix();
        }
        GlStateManager.enableLighting();
    }
}
