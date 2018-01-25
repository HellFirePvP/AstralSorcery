/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemRendererFilteredTESR
 * Created by HellFirePvP
 * Date: 31.07.2016 / 10:20
 */
public class ItemRendererFilteredTESR implements IItemRenderer {

    private Map<Integer, TEISRProperties> renderMap = new HashMap<>();

    public void addRender(int stackMeta, TileEntitySpecialRenderer tesr, TileEntity renderTile) {
        renderMap.put(stackMeta, new TEISRProperties(tesr, renderTile));
    }

    @Override
    public void render(ItemStack stack) {
        if(renderMap.containsKey(stack.getItemDamage())) {
            TEISRProperties prop = renderMap.get(stack.getItemDamage());
            prop.tesr.render(prop.renderTile, 0, 0, 0, Minecraft.getMinecraft().getRenderPartialTicks(), 0, 1F);
        }
    }

    private static class TEISRProperties {

        private final TileEntitySpecialRenderer tesr;
        private final TileEntity renderTile;

        private TEISRProperties(TileEntitySpecialRenderer tesr, TileEntity renderTile) {
            this.tesr = tesr;
            this.renderTile = renderTile;
        }
    }

}
