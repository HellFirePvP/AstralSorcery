/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.model.builtin.ModelAttunementAltar;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderAttunementAltar
 * Created by HellFirePvP
 * Date: 17.11.2019 / 07:52
 */
public class RenderAttunementAltar extends CustomTileEntityRenderer<TileAttunementAltar> {

    private static final ModelAttunementAltar MODEL_ATTUNEMENT_ALTAR = new ModelAttunementAltar();

    public RenderAttunementAltar(TileEntityRendererDispatcher tileRenderer) {
        super(tileRenderer);
    }

    @Override
    public void render(TileAttunementAltar tile, float pTicks, MatrixStack renderStack, IRenderTypeBuffer renderTypeBuffer, int combinedLight, int combinedOverlay) {
        IVertexBuilder buf = renderTypeBuffer.getBuffer(MODEL_ATTUNEMENT_ALTAR.getGeneralType());

        renderStack.push();
        renderStack.translate(0.5, 0.5, 0.5);
        renderStack.rotate(Vector3f.XP.rotationDegrees(180));
        MODEL_ATTUNEMENT_ALTAR.render(renderStack, buf, combinedLight, combinedOverlay);
        renderStack.pop();

        float spinDur = TileAttunementAltar.MAX_START_ANIMATION_SPIN;
        float spinStart = TileAttunementAltar.MAX_START_ANIMATION_TICK;

        float startY = -1.2F;
        float endY   = -0.5F;
        float tickPartY = (endY - startY) / spinStart;
        float prevPosY = endY + (tile.prevActivationTick * tickPartY);
        float posY     = endY + (tile.activationTick     * tickPartY);
        float framePosY = RenderingVectorUtils.interpolate(prevPosY, posY, pTicks);

        double generalAnimationTick = (ClientScheduler.getClientTick() + pTicks) / 4D;
        if (tile.animate) {
            if (tile.tesrLocked) {
                tile.tesrLocked = false;
            }
        } else {
            if (tile.tesrLocked) {
                generalAnimationTick = 7.25D;
            } else {
                if (Math.abs((generalAnimationTick % spinDur) - 7.25D) <= 0.3125) {
                    generalAnimationTick = 7.25D;
                    tile.tesrLocked = true;
                }
            }
        }

        for (int i = 1; i < 9; i++) {
            float incrementer = (spinDur / 8F) * i;

            double aFrame =     generalAnimationTick + incrementer;
            double prevAFrame = generalAnimationTick + incrementer - 1;
            double renderFrame = RenderingVectorUtils.interpolate(prevAFrame, aFrame, 0);

            double partRenderFrame = (renderFrame % spinDur) / spinDur;
            float normalized = (float) (partRenderFrame * 2F * Math.PI);

            float xOffset = MathHelper.cos(normalized);
            float zOffset = MathHelper.sin(normalized);
            float rotation = RenderingVectorUtils.interpolate(tile.prevActivationTick / spinStart, tile.activationTick / spinStart, pTicks);

            renderStack.push();
            renderStack.translate(0.5, framePosY, 0.5);
            renderStack.rotate(Vector3f.XP.rotationDegrees(180));
            MODEL_ATTUNEMENT_ALTAR.renderHovering(renderStack, buf, combinedLight, combinedOverlay, 1F, 1F, 1F, 1F, xOffset, zOffset, rotation);
            renderStack.pop();
        }
    }
}
