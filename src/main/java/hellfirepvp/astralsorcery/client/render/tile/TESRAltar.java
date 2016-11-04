package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.constellation.CelestialHandler;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.item.ItemFocusLens;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRAltar
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:21
 */
public class TESRAltar extends TileEntitySpecialRenderer<TileAltar> {

    private static final Random rand = new Random();

    @Override
    public void renderTileEntityAt(TileAltar te, double x, double y, double z, float partialTicks, int destroyStage) {
        switch (te.getAltarLevel()) {
            case CONSTELLATION_CRAFT:
                if(te.getMultiblockState()) {
                    GL11.glPushMatrix();
                    renderCrystalEffects(te, x, y, z, partialTicks);
                    renderFocusLens(te, x, y, z, partialTicks);
                    renderConstellation(te, x, y, z, partialTicks);
                    GL11.glPopMatrix();
                }
                break;
        }
    }

    private void renderConstellation(TileAltar te, double x, double y, double z, float partialTicks) {
        Constellation c = te.getFocusedConstellation();
        if(c == null) return;

        float alphaDaytime = (float) CelestialHandler.calcDaytimeDistribution(te.getWorld());

        int max = 5000;
        int t = ClientScheduler.getClientTick() % max;
        float halfAge = max / 2F;
        float tr = 1F - (Math.abs(halfAge - t) / halfAge);
        tr *= 2;

        RenderingUtils.removeStandartTranslationFromTESRMatrix(partialTicks);

        RenderConstellation.renderConstellationIntoWorldFlat(c, c.queryTier().calcRenderColor(), new Vector3(te).add(0.5, 0.03, 0.5), 6.5 + tr, 2, 0.1F + 0.8F * alphaDaytime);
    }

    private void renderFocusLens(TileAltar te, double x, double y, double z, float partialTicks) {
        ItemStack focus = te.getFocusLensStack();
        if(focus == null || focus.getItem() == null || !(focus.getItem() instanceof ItemFocusLens)) return;

    }

    private void renderCrystalEffects(TileAltar te, double x, double y, double z, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        long sBase = 1553015L;
        sBase ^= (long) te.getPos().getX();
        sBase ^= (long) te.getPos().getY();
        sBase ^= (long) te.getPos().getZ();
        RenderingUtils.renderLightRayEffects(x + 0.5, y + 3.5, z + 0.5, BlockCollectorCrystal.CollectorCrystalType.ROCK_CRYSTAL.displayColor, sBase, ClientScheduler.getClientTick(), 20, 50, 25);
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 3, z + 0.5);
        TESRCollectorCrystal.renderCrystal(false, true);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    /*private void doAltarTileTransforms(TileAltar.AltarLevel level) {
        switch (level) {
            case DISCOVERY:
                GL11.glTranslated(0.5, 1.44, 0.5);
                GL11.glScaled(0.06, 0.06, 0.06);
                GL11.glRotated(180, 1, 0, 0);
                break;
            case ATTENUATION:
                break;
            case CONSTELLATION_CRAFT:
                break;
            case TRAIT_CRAFT:
                break;
            case ENDGAME:
                break;
        }
    }
    private void doAltarItemTransforms(TileAltar.AltarLevel level) {
        switch (level) {
            case DISCOVERY:
                GL11.glTranslated(0.5, 0.9, 0.5);
                GL11.glScalef(0.035F, 0.035F, 0.035F);
                GL11.glRotated(45, 1, 0, 0);
                GL11.glRotated(45, 0, 1, 0);
                GL11.glRotated(180, 0, 0, 1);
                break;
            case ATTENUATION:
                break;
            case CONSTELLATION_CRAFT:
                break;
            case TRAIT_CRAFT:
                break;
            case ENDGAME:
                break;
        }
    }

    private void doAltarRender(TileAltar.AltarLevel level) {
        switch (level) {
            case DISCOVERY:
                break;
            case ATTENUATION:
                break;
            case CONSTELLATION_CRAFT:
                break;
            case TRAIT_CRAFT:
                break;
            case ENDGAME:
                break;
        }
    }*/

}
