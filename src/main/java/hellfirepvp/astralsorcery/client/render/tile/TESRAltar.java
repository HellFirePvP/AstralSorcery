package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.models.tcn.TCNModelAltarDiscovery;
import hellfirepvp.astralsorcery.client.util.item.IItemRenderer;
import hellfirepvp.astralsorcery.client.util.item.ItemRendererModelDummy;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.crafting.altar.ActiveCraftingTask;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRAltar
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:21
 */
public class TESRAltar extends TileEntitySpecialRenderer<TileAltar> implements IItemRenderer {

    private static final TCNModelAltarDiscovery altarDiscovery = new TCNModelAltarDiscovery();

    private static final BindableResource texAltarDiscovery = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "altar1");

    @Override
    public void renderTileEntityAt(TileAltar te, double x, double y, double z, float partialTicks, int destroyStage) {
        TileAltar.AltarLevel level = te.getAltarLevel();
        if(level == null) {
            level = TileAltar.AltarLevel.DISCOVERY;
        }
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glTranslated(x, y, z);
        doAltarTileTransforms(level);
        doAltarRender(level);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glPopAttrib();

        if(te.getActiveCraftingTask() != null) {
            te.getActiveCraftingTask().getRecipeToCraft().onCraftTESRRender(te, x, y, z, partialTicks);
        }
    }

    private void doAltarTileTransforms(TileAltar.AltarLevel level) {
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
                texAltarDiscovery.bind();
                altarDiscovery.render(null, 0, 0, 0, 0, 0, 1);
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

    @Override
    public void render(ItemStack stack) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        RenderHelper.disableStandardItemLighting();
        int damage = stack.getItemDamage();
        damage = MathHelper.clamp_int(damage, 0, 4);
        TileAltar.AltarLevel level = TileAltar.AltarLevel.values()[damage];
        doAltarItemTransforms(level);
        doAltarRender(level);
        RenderHelper.enableStandardItemLighting();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public static ItemCameraTransforms getTransforms() {
        return ItemRendererModelDummy.MODEL_GENERATED.getAllTransforms();
    }

}
