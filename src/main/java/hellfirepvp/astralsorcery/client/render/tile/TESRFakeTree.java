/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.common.tile.TileFakeTree;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TESRFakeTree
 * Created by HellFirePvP
 * Date: 11.11.2016 / 21:13
 */
public class TESRFakeTree extends TileEntitySpecialRenderer<TileFakeTree> {

    @Override
    public void render(TileFakeTree te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if(te.getFakedState() == null) return;
        IBlockState renderState = te.getFakedState();
        TESRTranslucentBlock.blocks.add(new TESRTranslucentBlock.TranslucentBlockState(renderState, te.getPos()));
    }

}
