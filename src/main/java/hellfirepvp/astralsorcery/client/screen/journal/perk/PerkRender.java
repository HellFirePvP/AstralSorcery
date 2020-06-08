/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.perk;

import hellfirepvp.astralsorcery.common.perk.AllocationStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkRender
 * Created by HellFirePvP
 * Date: 08.08.2019 / 17:41
 */
public interface PerkRender {

    @OnlyIn(Dist.CLIENT)
    public void addGroups(Collection<PerkRenderGroup> groups);

    // Rendered with pos_tex_color
    @Nullable
    @OnlyIn(Dist.CLIENT)
    public Rectangle.Float renderPerkAtBatch(BatchPerkContext drawCtx,
                                              AllocationStatus status, long spriteOffsetTick, float pTicks,
                                              float x, float y, float zLevel, float scale);

}
