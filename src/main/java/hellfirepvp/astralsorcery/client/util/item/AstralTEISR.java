/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.item;

import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

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
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            ItemRenderRegistry.renderItemStack(itemStackIn);
            GL11.glPopAttrib();
            return;
        }

        parent.renderByItem(itemStackIn);
    }
}
