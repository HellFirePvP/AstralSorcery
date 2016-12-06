package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.models.base.ASprism_color;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalPrismLens;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRPrismLens
 * Created by HellFirePvP
 * Date: 20.09.2016 / 13:08
 */
public class TESRPrismLens extends TileEntitySpecialRenderer<TileCrystalPrismLens> {

    private static final ASprism_color modelPrismColoredFrame = new ASprism_color();
    private static final BindableResource texPrismColorFrame = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "prism/prism_color");

    @Override
    public void renderTileEntityAt(TileCrystalPrismLens te, double x, double y, double z, float partialTicks, int destroyStage) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        if(te.getLinkedPositions().size() > 0) {
            long sBase = 0x5911539513145924L;
            sBase ^= (long) te.getPos().getX();
            sBase ^= (long) te.getPos().getY();
            sBase ^= (long) te.getPos().getZ();
            RenderingUtils.renderLightRayEffects(x + 0.5, y + 0.6, z + 0.5, BlockCollectorCrystal.CollectorCrystalType.ROCK_CRYSTAL.displayColor, sBase, ClientScheduler.getClientTick(), 9, 50, 25);
        }

        GL11.glTranslated(x + 0.5, y + 0.20, z + 0.5);

        GL11.glScaled(0.6, 0.6, 0.6);
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();
        TESRCollectorCrystal.renderCrystal(false, true);
        RenderHelper.disableStandardItemLighting();
        if(te.getLensColor() != null) {
            //TODO move past world render
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated(x + 0.5, y + 1.5, z + 0.5);
            GL11.glScaled(0.0625, 0.0625, 0.0625);
            GL11.glRotated(180, 1, 0, 0);
            GL11.glEnable(GL11.GL_BLEND);
            Blending.DEFAULT.apply();
            Color c = te.getLensColor().wrappedColor;
            GL11.glColor4f(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F, 1F);
            renderColoredPrism();
            GL11.glColor4f(1F, 1F, 1F, 1F);
        }
        TextureHelper.refreshTextureBindState();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void renderColoredPrism() {
        texPrismColorFrame.bind();
        modelPrismColoredFrame.render(null, 0, 0, 0, 0, 0, 1);
    }

}
