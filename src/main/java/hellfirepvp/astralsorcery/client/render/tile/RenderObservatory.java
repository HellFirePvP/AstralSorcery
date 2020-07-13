/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import hellfirepvp.astralsorcery.client.model.builtin.ModelObservatory;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.entity.technical.EntityObservatoryHelper;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderObservatory
 * Created by HellFirePvP
 * Date: 16.02.2020 / 10:34
 */
public class RenderObservatory extends CustomTileEntityRenderer<TileObservatory> {

    private static final ModelObservatory MODEL_OBSERVATORY = new ModelObservatory();

    public RenderObservatory(TileEntityRendererDispatcher tileRenderer) {
        super(tileRenderer);
    }

    @Override
    public void render(TileObservatory tile, float pTicks, MatrixStack renderStack, IRenderTypeBuffer renderTypeBuffer, int combinedLight, int combinedOverlay) {
        Entity ridden;
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null &&
                (ridden = Minecraft.getInstance().player.getRidingEntity()) != null &&
                ridden instanceof EntityObservatoryHelper &&
                ((EntityObservatoryHelper) ridden).getAssociatedObservatory() != null) {
            ((EntityObservatoryHelper) ridden).applyObservatoryRotationsFrom(tile, player);
        }

        float prevYaw = tile.prevObservatoryYaw;
        float yaw = tile.observatoryYaw;
        float prevPitch = tile.prevObservatoryPitch;
        float pitch = tile.observatoryPitch;

        float iYawDegree = RenderingVectorUtils.interpolateRotation(prevYaw + 180, yaw + 180, pTicks);
        float iPitchDegree = RenderingVectorUtils.interpolateRotation(prevPitch, pitch, pTicks);


        renderStack.push();
        renderStack.translate(0.5F, 1.5F, 0.5F);
        renderStack.rotate(Vector3f.XP.rotationDegrees(180F));
        renderStack.rotate(Vector3f.YP.rotationDegrees(180F));
        //renderStack.scale(0.0625F, 0.0625F, 0.0625F);

        MODEL_OBSERVATORY.setupRotations(iYawDegree, iPitchDegree);
        MODEL_OBSERVATORY.render(renderStack, renderTypeBuffer.getBuffer(MODEL_OBSERVATORY.getGeneralType()), combinedLight, combinedOverlay);

        renderStack.pop();
    }
}
