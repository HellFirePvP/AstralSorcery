/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.tile.TileStarlightInfuser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRStarlightInfuser
 * Created by HellFirePvP
 * Date: 13.01.2017 / 12:17
 */
public class TESRStarlightInfuser extends TileEntitySpecialRenderer<TileStarlightInfuser> {

    @Override
    public void render(TileStarlightInfuser te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        ItemStack in = te.getInputStack();
        if(in.isEmpty()) return;
        RenderingUtils.renderItemAsEntity(in, x, y, z, partialTicks, te.getTicksExisted());
    }
}
