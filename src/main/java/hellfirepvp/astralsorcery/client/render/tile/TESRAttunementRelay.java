/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.tile.TileAttunementRelay;
import hellfirepvp.astralsorcery.common.tile.base.TileInventoryBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRAttunementRelay
 * Created by HellFirePvP
 * Date: 27.03.2017 / 18:07
 */
public class TESRAttunementRelay extends TileEntitySpecialRenderer<TileAttunementRelay> {

    @Override
    public void render(TileAttunementRelay te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        TileInventoryBase.ItemHandlerTile iht = te.getInventoryHandler();
        if (iht == null) return;
        ItemStack in = iht.getStackInSlot(0);
        if (in.isEmpty()) return;
        RenderingUtils.renderItemAsEntity(in, x, y - 0.5, z, partialTicks, te.getTicksExisted());
    }

}
