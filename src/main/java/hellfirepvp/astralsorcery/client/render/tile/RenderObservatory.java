/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.model.builtin.ModelObservatory;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.entity.technical.EntityObservatoryHelper;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
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

    @Override
    public void render(TileObservatory tile, double x, double y, double z, float pTicks, int destroyStage) {
        if (new Vector3(x, y, z).length() >= 64) {
            return;
        }

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

        float iYaw = RenderingVectorUtils.interpolateRotation(prevYaw + 180, yaw + 180, pTicks);
        float iPitch = RenderingVectorUtils.interpolateRotation(prevPitch, pitch, pTicks);

        GlStateManager.pushMatrix();
        GlStateManager.translated(x + 0.5, y + 1.5, z + 0.5);
        GlStateManager.rotated(180, 1, 0, 0);
        GlStateManager.rotated(180, 0, 1, 0);
        GlStateManager.scaled(0.0625, 0.0625, 0.0625);

        renderObservatory(iYaw, iPitch, destroyStage);
        GlStateManager.popMatrix();
    }

    private void renderObservatory(float iYaw, float iPitch, int destoryStage) {
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();

        MODEL_OBSERVATORY.render(iYaw, iPitch, destoryStage);

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }

    @Override
    public boolean isGlobalRenderer(TileObservatory te) {
        return true;
    }
}
