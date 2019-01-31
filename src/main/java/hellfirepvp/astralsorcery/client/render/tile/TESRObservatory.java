/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.models.base.ASobservatory;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.entities.EntityObservatoryHelper;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRObservatory
 * Created by HellFirePvP
 * Date: 26.05.2018 / 16:04
 */
public class TESRObservatory extends TileEntitySpecialRenderer<TileObservatory> {

    private static final ASobservatory modelTelescope = new ASobservatory();
    private static final BindableResource texTelescope = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "base/observatory");

    @Override
    public void render(TileObservatory te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if(new Vector3(x, y, z).length() >= 64) {
            return;
        }

        Entity ridden;
        EntityPlayer player;
        if ((player = Minecraft.getMinecraft().player) != null && (ridden = Minecraft.getMinecraft().player.getRidingEntity()) != null &&
                ridden instanceof EntityObservatoryHelper && ((EntityObservatoryHelper) ridden).tryGetObservatory() != null) {
            ((EntityObservatoryHelper) ridden).applyObservatoryRotationsFrom(te, player);
        }

        float prevYaw = te.prevObservatoryYaw;
        float yaw = te.observatoryYaw;
        float prevPitch = te.prevObservatoryPitch;
        float pitch = te.observatoryPitch;

        float iYaw = RenderingUtils.interpolateRotation(prevYaw + 180, yaw + 180, partialTicks);
        float iPitch = RenderingUtils.interpolateRotation(prevPitch, pitch, partialTicks);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 1.5, z + 0.5);
        GlStateManager.rotate(180, 1, 0, 0);
        GlStateManager.rotate(180, 0, 1, 0);
        GlStateManager.scale(0.0625, 0.0625, 0.0625);

        RenderHelper.disableStandardItemLighting();

        renderModel(iYaw, iPitch);
        GlStateManager.popMatrix();

        TextureHelper.refreshTextureBindState();
    }

    private void renderModel(float iYaw, float iPitch) {
        texTelescope.bind();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();
        Blending.DEFAULT.apply();
        modelTelescope.render(null, iYaw, iPitch, 0, 0, 0, 1);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
    }

    @Override
    public boolean isGlobalRenderer(TileObservatory te) {
        return true;
    }
}
