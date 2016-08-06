package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.util.item.IItemRenderer;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRAltar
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:21
 */
public class TESRAltar extends TileEntitySpecialRenderer<TileAltar> implements IItemRenderer {

    @Override
    public void renderTileEntityAt(TileAltar te, double x, double y, double z, float partialTicks, int destroyStage) {
        TileAltar.AltarLevel level = te.getAltarLevel();
        RenderHelper.disableStandardItemLighting();

        RenderHelper.enableStandardItemLighting();
    }

    @Override
    public void render(ItemStack stack) {
        RenderHelper.disableStandardItemLighting();
        try {
            TileAltar.AltarLevel level = TileAltar.AltarLevel.values()[stack.getItemDamage()];

        } catch (Exception exc) {}
        RenderHelper.enableStandardItemLighting();
    }

}
