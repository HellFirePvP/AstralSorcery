package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalPrismLens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRPrismLens
 * Created by HellFirePvP
 * Date: 20.09.2016 / 13:08
 */
public class TESRPrismLens extends TileEntitySpecialRenderer<TileCrystalPrismLens> {

    @Override
    public void renderTileEntityAt(TileCrystalPrismLens te, double x, double y, double z, float partialTicks, int destroyStage) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        if(te.getLinkedPositions().size() > 0) {
            long sBase = 0x5911539513145924L;
            sBase ^= (long) te.getPos().getX();
            sBase ^= (long) te.getPos().getY();
            sBase ^= (long) te.getPos().getZ();
            RenderingUtils.renderLightRayEffects(x + 0.5, y + 0.6, z + 0.5, BlockCollectorCrystal.CollectorCrystalType.ROCK_CRYSTAL.displayColor, sBase, ClientScheduler.getClientTick(), 20, 50, 25);
        }

        GL11.glTranslated(x + 0.5, y + 0.20, z + 0.5);

        GL11.glScaled(0.6, 0.6, 0.6);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        TESRCollectorCrystal.renderCrystal(false, true);

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

}
