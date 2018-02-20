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
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTravelLavaProtection
 * Created by HellFirePvP
 * Date: 04.12.2016 / 08:39
 */
public class PerkTravelLavaProtection extends ConstellationPerk {

    private static int ticksProtection = 10 * 20;
    private static int ticksUntilProtectionWorksAgain = 120 * 20;

    public PerkTravelLavaProtection() {
        super("TRV_LPROT", Target.ENTITY_HURT);
    }

    @Override
    public float onEntityHurt(EntityPlayer hurt, DamageSource source, float dmgIn) {
        int cd = getActiveCooldownForPlayer(hurt);
        if(cd <= 0 && hurt.isInLava()) {
            int amt = ticksProtection + ticksUntilProtectionWorksAgain;
            setCooldownActiveForPlayer(hurt, amt);
            cd = amt;
        }
        if(cd > ticksUntilProtectionWorksAgain) {
            if(source.isFireDamage() || hurt.isInLava()) {
                addAlignmentCharge(hurt, 0.7);
                dmgIn = 0;
            }
        }
        return dmgIn;
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        ticksProtection = cfg.getInt(getKey() + "TicksProtection", getConfigurationSection(), 200, 1, 6000, "Defines the ticks of protection from fire damage or any damage as long as you are in lava after falling in lava.");
        ticksUntilProtectionWorksAgain = cfg.getInt(getKey() + "TicksProtectionCooldown", getConfigurationSection(), 2400, 1, 60000, "Defines the ticks of cooldown the protection needs to 'recharge' after it was active.");
    }

}
