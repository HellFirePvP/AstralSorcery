/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.tile.TileInfuser;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderInfuser
 * Created by HellFirePvP
 * Date: 09.11.2019 / 21:51
 */
public class RenderInfuser extends CustomTileEntityRenderer<TileInfuser> {

    @Override
    public void render(TileInfuser tile, double x, double y, double z, float pTicks, int destroyStage) {
        ItemStack i = tile.getItemInput();
        if (!i.isEmpty()) {
            RenderingUtils.renderItemAsEntity(i, x + 0.5, y + 0.8, z + 0.5, pTicks, tile.getTicksExisted());
        }
    }
}
