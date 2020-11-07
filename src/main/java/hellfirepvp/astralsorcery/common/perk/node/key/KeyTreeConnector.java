/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import hellfirepvp.astralsorcery.common.perk.node.MajorPerk;
import hellfirepvp.astralsorcery.common.perk.tree.PerkTreePoint;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyTreeConnector
 * Created by HellFirePvP
 * Date: 25.08.2019 / 18:29
 */
public class KeyTreeConnector extends MajorPerk {

    public KeyTreeConnector(ResourceLocation name, float x, float y) {
        super(name, x, y);
        this.setCategory(CATEGORY_EPIPHANY);
    }

    @Override
    public boolean mayUnlockPerk(PlayerProgress progress, PlayerEntity player) {
        if (!progress.hasFreeAllocationPoint(player, getSide(player)) ||
                !canSee(player, progress)) return false;

        LogicalSide side = getSide(player);
        boolean hasAllAdjacent = true;
        for (AbstractPerk otherPerks : PerkTree.PERK_TREE.getConnectedPerks(side, this)) {
            if (!progress.hasPerkUnlocked(otherPerks)) {
                hasAllAdjacent = false;
                break;
            }
        }
        if (!hasAllAdjacent) {
            return PerkTree.PERK_TREE.getPerkPoints(getSide(player)).stream()
                    .map(PerkTreePoint::getPerk)
                    .filter(perk -> perk instanceof KeyTreeConnector)
                    .anyMatch(progress::hasPerkUnlocked);
        } else {
            return true;
        }
    }

    @Override
    public void onUnlockPerkServer(@Nullable PlayerEntity player, PlayerProgress progress, CompoundNBT dataStorage) {
        super.onUnlockPerkServer(player, progress, dataStorage);

        ListNBT listTokens = new ListNBT();
        for (AbstractPerk otherPerk : PerkTree.PERK_TREE.getConnectedPerks(LogicalSide.SERVER, this)) {
            if (ResearchManager.forceApplyPerk(player, otherPerk)) {
                String token = "connector-tk-" + otherPerk.getRegistryName().toString();
                if (ResearchManager.grantFreePerkPoint(player, token)) {
                    listTokens.add(StringNBT.valueOf(token));
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

    @Override
    public void clearCaches(LogicalSide side) {
        super.clearCaches(side);
    }
}
