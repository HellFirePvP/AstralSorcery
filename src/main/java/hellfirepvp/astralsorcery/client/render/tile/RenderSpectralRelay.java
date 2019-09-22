/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.tile.TileSpectralRelay;
import hellfirepvp.astralsorcery.common.util.tile.TileInventory;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderSpectralRelay
 * Created by HellFirePvP
 * Date: 22.09.2019 / 18:33
 */
public class RenderSpectralRelay extends CustomTileEntityRenderer<TileSpectralRelay> {

    @Override
    public void render(TileSpectralRelay tile, double x, double y, double z, float pTicks, int destroyStage) {
        ItemStack i = tile.getInventory().getStackInSlot(0);
        if (!i.isEmpty()) {
            RenderingUtils.renderItemAsEntity(i, x + 0.5, y + 0.1, z + 0.5, pTicks, tile.getTicksExisted());
        }
    }
}
