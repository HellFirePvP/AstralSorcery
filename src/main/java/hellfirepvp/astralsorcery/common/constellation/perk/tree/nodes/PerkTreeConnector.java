/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreeMajor;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePoint;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTreeConnector
 * Created by HellFirePvP
 * Date: 15.07.2018 / 17:29
 */
public class PerkTreeConnector extends AbstractPerk {

    private static List<PerkTreeConnector> connectorCache = Lists.newArrayList();

    public PerkTreeConnector(String name, int x, int y) {
        super(name, x, y);
        connectorCache.add(this);
    }

    @Override
    public PerkTreePoint getPoint() {
        return new PerkTreeMajor(this, this.getOffset());
    }

    @Override
    public boolean mayUnlockPerk(PlayerProgress progress) {
        if (!progress.hasFreeAllocationPoint()) return false;

        boolean hasAllAdjacent = true;
        for (AbstractPerk otherPerks : PerkTree.PERK_TREE.getConnectedPerks(this)) {
            if (!progress.hasPerkUnlocked(otherPerks)) {
                hasAllAdjacent = false;
                break;
            }
        }
        if (!hasAllAdjacent) {
            for (PerkTreeConnector conn : connectorCache) {
                if (progress.hasPerkUnlocked(conn)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onUnlockPerkServer(EntityPlayer player, PlayerProgress progress) {
        super.onUnlockPerkServer(player, progress);

        for (AbstractPerk otherPerk : PerkTree.PERK_TREE.getConnectedPerks(this)) {
            if(ResearchManager.forceApplyPerk(player, otherPerk)) {
                ResearchManager.grantFreePerkPoint(player);
            }
        }
    }

    @Override
    protected void applyPerkLogic(EntityPlayer player, Side side) {}

    @Override
    protected void removePerkLogic(EntityPlayer player, Side side) {}

}
