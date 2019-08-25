/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.node.MajorPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TreeConnectorPerk
 * Created by HellFirePvP
 * Date: 25.08.2019 / 18:29
 */
public class TreeConnectorPerk extends MajorPerk {

    private static List<TreeConnectorPerk> connectorCache = new ArrayList<>();

    public TreeConnectorPerk(ResourceLocation name, int x, int y) {
        super(name, x, y);
        this.setCategory(CATEGORY_EPIPHANY);
        connectorCache.add(this);
    }

    @Override
    public boolean mayUnlockPerk(PlayerProgress progress, PlayerEntity player) {
        if (!progress.hasFreeAllocationPoint(player) ||
                !canSee(player, progress)) return false;

        boolean hasAllAdjacent = true;
        for (AbstractPerk otherPerks : PerkTree.PERK_TREE.getConnectedPerks(this)) {
            if (!progress.hasPerkEffect(otherPerks)) {
                hasAllAdjacent = false;
                break;
            }
        }
        if (!hasAllAdjacent) {
            for (TreeConnectorPerk conn : connectorCache) {
                if (progress.hasPerkEffect(conn)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onUnlockPerkServer(@Nullable PlayerEntity player, PlayerProgress progress, CompoundNBT dataStorage) {
        super.onUnlockPerkServer(player, progress, dataStorage);

        ListNBT listTokens = new ListNBT();
        for (AbstractPerk otherPerk : PerkTree.PERK_TREE.getConnectedPerks(this)) {
            if(ResearchManager.forceApplyPerk(player, otherPerk)) {
                String token = "connector-tk-" + otherPerk.getRegistryName().toString();
                if(ResearchManager.grantFreePerkPoint(player, token)) {
                    listTokens.add(new StringNBT(token));
                }
            }
        }
        dataStorage.put("pointtokens", listTokens);
    }

    @Override
    public void onRemovePerkServer(PlayerEntity player, PlayerProgress progress, CompoundNBT dataStorage) {
        super.onRemovePerkServer(player, progress, dataStorage);

        ListNBT list = dataStorage.getList("pointtokens", Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            ResearchManager.revokeFreePoint(player, list.getString(i));
        }
    }
}
