/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.models.obj.OBJModelLibrary;
import hellfirepvp.astralsorcery.client.util.item.IItemRenderer;
import hellfirepvp.astralsorcery.client.util.obj.WavefrontObject;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.tile.TileCelestialCrystals;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRCelestialCrystals
 * Created by HellFirePvP
 * Date: 14.09.2016 / 19:45
 */
public class TESRCelestialCrystals extends TileEntitySpecialRenderer<TileCelestialCrystals> implements IItemRenderer {

    private static int dlC0 = -1, dlC1 = -1, dlC2 = -1, dlC3 = -1, dlC4 = -1;
    private static final BindableResource texCelestialCrystals = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "c_crystal_tex");

    private static int[] rotMapping = new int[] { 45, 135, 270, 90, 315, 0, 180, 225 };

    @Override
    public void render(TileCelestialCrystals te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glTranslated(x + 0.5, y + 0.1, z + 0.5);
        float size = 0.2F;
        GL11.glScalef(size, size, size);

        int r = 0x59A51481;
        BlockPos at = te.getPos();
        r ^= at.getX();
        r ^= at.getY();
        r ^= at.getZ();
        r = Math.abs(r);
        r = rotMapping[r % rotMapping.length];
        GL11.glRotated(r, 0, 1, 0);

        renderCelestialCrystals(te.getGrowth());
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void renderCelestialCrystals(int stage) {
        GL11.glPushMatrix();
        texCelestialCrystals.bind();
        int dlSelected;
        WavefrontObject obj;
        switch (stage) {
            case 0:
                dlSelected = dlC0;
                obj = OBJModelLibrary.crystalsStage0;
                break;
            case 1:
                dlSelected = dlC1;
                obj = OBJModelLibrary.crystalsStage1;
                break;
            case 2:
                dlSelected = dlC2;
                obj = OBJModelLibrary.crystalsStage2;
                break;
            case 3:
                dlSelected = dlC3;
                obj = OBJModelLibrary.crystalsStage3;
                break;
            case 4:
                dlSelected = dlC4;
                obj = OBJModelLibrary.crystalsStage4;
                break;
            default:
                dlSelected = dlC0;
                obj = OBJModelLibrary.crystalsStage0;
                break;
        }
        if(dlSelected == -1) {
            dlSelected = GLAllocation.generateDisplayLists(1);
            switch (stage) {
                case 0:
                    dlC0 = dlSelected;
                    break;
                case 1:
                    dlC1 = dlSelected;
                    break;
                case 2:
                    dlC2 = dlSelected;
                    break;
                case 3:
                    dlC3 = dlSelected;
                    break;
                case 4:
                    dlC4 = dlSelected;
                    break;
                default:
                    dlC0 = dlSelected;
                    break;
            }
            GL11.glNewList(dlSelected, GL11.GL_COMPILE);
            obj.renderAll(true);
            GL11.glEndList();
        }
        GL11.glCallList(dlSelected);

        GL11.glPopMatrix();
    }

    @Override
    public void render(ItemStack stack) {
        GL11.glPushMatrix();
        GL11.glTranslated(0.5, 0.25, 0.5);
        GL11.glScalef(0.2F, 0.2F, 0.2F);
        GL11.glRotated(-10, 0, 0, 1);
        GL11.glRotated( 20, 1, 0, 0);
        GL11.glRotated(-70, 0, 1, 0);
        GL11.glDisable(GL11.GL_CULL_FACE);
        RenderHelper.disableStandardItemLighting();
        renderCelestialCrystals(MathHelper.clamp(stack.getItemDamage(), 0, 4));
        RenderHelper.enableStandardItemLighting();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }

}
