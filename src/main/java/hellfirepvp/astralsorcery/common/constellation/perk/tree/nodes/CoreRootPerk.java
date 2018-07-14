/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes;

import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreeMajor;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePoint;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CoreRootPerk
 * Created by HellFirePvP
 * Date: 11.07.2018 / 18:24
 */
public class CoreRootPerk extends AbstractPerk {

    public CoreRootPerk() {
        super("core", 0, 0);
    }

    @Override
    public PerkTreePoint getPoint() {
        return new PerkTreeMajor(this, this.getOffset());
    }

    @Override
    public void applyPerkLogic(EntityPlayer player, Side side) {}

    @Override
    public void removePerkLogic(EntityPlayer player, Side side) {}

}
