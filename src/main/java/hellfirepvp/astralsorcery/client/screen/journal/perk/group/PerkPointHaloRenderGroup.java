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
 * Class: PerkPointHaloRenderGroup
 * Created by HellFirePvP
 * Date: 08.08.2019 / 17:46
 */
@OnlyIn(Dist.CLIENT)
public class PerkPointHaloRenderGroup extends PerkRenderGroup {

    public static final PerkPointHaloRenderGroup INSTANCE = new PerkPointHaloRenderGroup();

    private PerkPointHaloRenderGroup() {
        add(SpritesAS.SPR_PERK_HALO_INACTIVE,     BatchPerkContext.PRIORITY_BACKGROUND + 50);
        add(SpritesAS.SPR_PERK_HALO_ACTIVE,       BatchPerkContext.PRIORITY_BACKGROUND + 60);
        add(SpritesAS.SPR_PERK_HALO_ACTIVATEABLE, BatchPerkContext.PRIORITY_BACKGROUND + 70);
    }

}

