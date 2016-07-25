package hellfirepvp.astralsorcery.client.util.item;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralTEISR
 * Created by HellFirePvP
 * Date: 25.07.2016 / 21:26
 */
public class AstralTEISR extends TileEntityItemStackRenderer {

    private TileEntityItemStackRenderer parent;

    public AstralTEISR(TileEntityItemStackRenderer parent) {
        this.parent = parent;
    }

    @Override
    public void renderByItem(ItemStack itemStackIn) {
        if(ItemRenderRegistry.shouldHandleItemRendering(itemStackIn)) {
            ItemRenderRegistry.renderItemStack(itemStackIn);
            return;
        }

        parent.renderByItem(itemStackIn);
    }
}
