/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.root;

import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.type.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.types.IPlayerTickPerk;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsManager;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VicioRootPerk
 * Created by HellFirePvP
 * Date: 16.07.2018 / 14:27
 */
public class VicioRootPerk extends RootPerk implements IPlayerTickPerk {

    private Map<StatBase, Map<UUID, Integer>> moveTrackMap = new HashMap<>();

    public VicioRootPerk(int x, int y) {
        super("vicio", Constellations.vicio, x, y);
    }

    @Override
    public void removePerkLogic(EntityPlayer player, Side side) {
        super.removePerkLogic(player, side);

        if (side == Side.SERVER) {
            this.moveTrackMap.computeIfAbsent(StatList.WALK_ONE_CM, s -> new HashMap<>()).remove(player.getUniqueID());
            this.moveTrackMap.computeIfAbsent(StatList.SPRINT_ONE_CM, s -> new HashMap<>()).remove(player.getUniqueID());
        }
    }

    @Override
    public void clearCaches(Side side) {
        super.clearCaches(side);

        if (side == Side.SERVER) {
            this.moveTrackMap.clear();
        }
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if (side == Side.SERVER && player instanceof EntityPlayerMP) {
            UUID uuid = player.getUniqueID();
            StatisticsManager manager = ((EntityPlayerMP) player).getStatFile();
            int walked = manager.readStat(StatList.WALK_ONE_CM);
            int sprint = manager.readStat(StatList.SPRINT_ONE_CM);

            int lastWalked = this.moveTrackMap.computeIfAbsent(StatList.WALK_ONE_CM, s -> new HashMap<>()).computeIfAbsent(uuid, u -> walked);
            int lastSprint = this.moveTrackMap.computeIfAbsent(StatList.SPRINT_ONE_CM, s -> new HashMap<>()).computeIfAbsent(uuid, u -> sprint);

            float added = 0;

            if (walked > lastWalked) {
                added += (walked - lastWalked);
                this.moveTrackMap.get(StatList.WALK_ONE_CM).put(uuid, walked);
            }
            if (sprint > lastSprint) {
                added += (sprint - lastSprint) * 1.2F;
                this.moveTrackMap.get(StatList.SPRINT_ONE_CM).put(uuid, sprint);
            }

            if (added > 0) {
                added *= 0.0025F;
                added *= expMultiplier;
                added = PerkAttributeHelper.getOrCreateMap(player, side).modifyValue(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP, added);
                ResearchManager.modifyExp(player, added);
            }
        }
    }

}
