package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRAltar
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:21
 */
public class TESRAltar extends TileEntitySpecialRenderer<TileAltar> {

    @Override
    public void renderTileEntityAt(TileAltar te, double x, double y, double z, float partialTicks, int destroyStage) {

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
