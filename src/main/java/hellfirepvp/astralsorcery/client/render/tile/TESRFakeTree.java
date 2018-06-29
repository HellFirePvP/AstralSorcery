/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.tile;

import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.base.patreon.base.PtEffectTreeBeacon;
import hellfirepvp.astralsorcery.common.tile.TileFakeTree;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;

import java.awt.*;

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
        if(x * x + y * y + z * z >= 64 * 64) return;
        Color effect = null;
        if (te.getPlayerEffectRef() != null) {
            PatreonEffectHelper.PatreonEffect pe = PatreonEffectHelper.getEffect(Side.CLIENT, te.getPlayerEffectRef());
            if (pe != null && pe instanceof PtEffectTreeBeacon) {
                effect = new Color(((PtEffectTreeBeacon) pe).getColorTranslucentOverlay(), true);
            }
        }
        TESRTranslucentBlock.addForRender(effect, renderState, te.getPos());
    }

}
