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
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkDefensiveNoArmor
 * Created by HellFirePvP
 * Date: 04.12.2016 / 17:33
 */
public class PerkDefensiveNoArmor extends ConstellationPerk {

    private static float multiplierDamageReduction = 0.3F;
    private static float healPerSec = 0.15F;

    public PerkDefensiveNoArmor() {
        super("DEF_NOARMOR", Target.ENTITY_HURT, Target.PLAYER_TICK);
    }

    @Override
    public float onEntityHurt(EntityPlayer hurt, DamageSource source, float dmgIn) {
        int eq = 0;
        for (ItemStack stack : hurt.getArmorInventoryList()) {
            if(!stack.isEmpty()) {
                eq++;
            }
        }
        if(eq <= 2) {
            addAlignmentCharge(hurt, 0.3 * Math.max(0 ,dmgIn));
            dmgIn *= multiplierDamageReduction;
        }
        return dmgIn;
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if(side == Side.SERVER) {
            if(player.ticksExisted % 20 != 0) return;
            int eq = 0;
            for (ItemStack stack : player.getArmorInventoryList()) {
                if(!stack.isEmpty()) {
                    eq++;
                }
            }
            if(eq <= 2) {
                player.heal(healPerSec);
            }
        }
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        multiplierDamageReduction = cfg.getFloat(getKey() + "NoArmorDmgReductionMultiplier", getConfigurationSection(), 0.3F, 0.001F, 1F, "Defines the damage reduction multiplier if the player has less than 2 or 2 armor pieces equipped");
        healPerSec = cfg.getFloat(getKey() + "NoArmorHealPerSecond", getConfigurationSection(), 0.15F, 0.001F, 50F, "Defines the amount that the player is healed for each second if he has less than 2 or 2 armor pieces equipped");
    }

}
