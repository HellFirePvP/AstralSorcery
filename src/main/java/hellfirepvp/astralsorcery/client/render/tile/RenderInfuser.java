/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.tile.TileInfuser;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderInfuser
 * Created by HellFirePvP
 * Date: 09.11.2019 / 21:51
 */
public class RenderInfuser extends CustomTileEntityRenderer<TileInfuser> {

    public RenderInfuser(TileEntityRendererDispatcher tileRenderer) {
        super(tileRenderer);
    }

    @Override
    public void render(TileInfuser tile, float pTicks, MatrixStack renderStack, IRenderTypeBuffer renderTypeBuffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = tile.getItemInput();
        if (!stack.isEmpty()) {
            renderStack.push();
            renderStack.translate(0.5F, 0.75F, 0.5F);
            RenderingUtils.renderItemAsEntity(stack, renderStack, renderTypeBuffer, 0, 0, 0, combinedLight, pTicks, tile.getTicksExisted());
            renderStack.pop();
        }
    }
}
