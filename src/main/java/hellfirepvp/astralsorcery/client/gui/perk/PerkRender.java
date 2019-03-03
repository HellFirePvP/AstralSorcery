/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.perk;

import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkRender
 * Created by HellFirePvP
 * Date: 24.11.2018 / 11:12
 */
public interface PerkRender {

    @SideOnly(Side.CLIENT)
    public void addGroups(Collection<PerkRenderGroup> groups);

    // Rendered with pos_tex_color
    @Nullable
    @SideOnly(Side.CLIENT)
    public Rectangle.Double renderPerkAtBatch(BatchPerkContext drawCtx,
                                       PerkTreePoint.AllocationStatus status, long spriteOffsetTick, float pTicks,
                                       double x, double y, double scale);

}
