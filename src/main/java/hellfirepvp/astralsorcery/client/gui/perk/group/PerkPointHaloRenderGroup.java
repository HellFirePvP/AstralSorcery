/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.perk.group;

import hellfirepvp.astralsorcery.client.gui.perk.BatchPerkContext;
import hellfirepvp.astralsorcery.client.gui.perk.PerkRenderGroup;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkPointHaloRenderGroup
 * Created by HellFirePvP
 * Date: 24.11.2018 / 13:05
 */
@SideOnly(Side.CLIENT)
public class PerkPointHaloRenderGroup extends PerkRenderGroup {

    public static final PerkPointHaloRenderGroup INSTANCE = new PerkPointHaloRenderGroup();

    private PerkPointHaloRenderGroup() {
        add(SpriteLibrary.spriteHalo4, BatchPerkContext.PRIORITY_BACKGROUND + 51);
        add(SpriteLibrary.spriteHalo5, BatchPerkContext.PRIORITY_BACKGROUND + 52);
        add(SpriteLibrary.spriteHalo6, BatchPerkContext.PRIORITY_BACKGROUND + 53);
    }

}
