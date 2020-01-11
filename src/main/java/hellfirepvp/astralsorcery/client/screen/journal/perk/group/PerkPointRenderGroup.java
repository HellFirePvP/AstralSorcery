/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.perk.group;

import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.screen.journal.perk.BatchPerkContext;
import hellfirepvp.astralsorcery.client.screen.journal.perk.PerkRenderGroup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkPointRenderGroup
 * Created by HellFirePvP
 * Date: 08.08.2019 / 17:46
 */
@OnlyIn(Dist.CLIENT)
public class PerkPointRenderGroup extends PerkRenderGroup {

    public static final PerkPointRenderGroup INSTANCE = new PerkPointRenderGroup();

    private PerkPointRenderGroup() {
        add(SpritesAS.SPR_PERK_INACTIVE,     BatchPerkContext.PRIORITY_BACKGROUND + 0);
        add(SpritesAS.SPR_PERK_ACTIVE,       BatchPerkContext.PRIORITY_BACKGROUND + 10);
        add(SpritesAS.SPR_PERK_ACTIVATEABLE, BatchPerkContext.PRIORITY_BACKGROUND + 20);
    }

}
