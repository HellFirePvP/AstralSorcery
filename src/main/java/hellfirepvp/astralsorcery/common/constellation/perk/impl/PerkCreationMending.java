/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkCreationMending
 * Created by HellFirePvP
 * Date: 27.12.2016 / 23:44
 */
public class PerkCreationMending extends ConstellationPerk {

    private static int chanceToRepair = 400;

    public PerkCreationMending() {
        super("CRE_MEND", Target.PLAYER_TICK);
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if(side == Side.SERVER) {
            for (ItemStack armor : player.getArmorInventoryList()) {
                if(rand.nextInt(chanceToRepair) != 0) continue;
                if(!armor.isEmpty() && armor.isItemStackDamageable() && armor.isItemDamaged()) {
                    armor.setItemDamage(armor.getItemDamage() - 1);
                    addAlignmentCharge(player, 0.2);
                }
            }
        }
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        chanceToRepair = cfg.getInt(getKey() + "ChanceForRepair", getConfigurationSection(), 400, 10, 4000, "Sets the chance (Random.nextInt(chance) == 0) to try to see if a piece of armor on the player that is damageable and damaged can be repaired");
    }

}
