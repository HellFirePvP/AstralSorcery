/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkTravelMovespeed
 * Created by HellFirePvP
 * Date: 04.12.2016 / 09:03
 */
public class PerkTravelMovespeed extends ConstellationPerk {

    public static AttributeModifier modMovespeedIncrease;
    private static final UUID modUUID = UUID.fromString("A7AA21B2-C4EF-4DC7-AF43-D55495E2F41F");

    private static double movespeedMultiplier = 0.20;

    public PerkTravelMovespeed() {
        super("TRV_MOVESPEED", Target.PLAYER_TICK);
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if(side == Side.SERVER) {
            AbstractAttributeMap map = player.getAttributeMap();
            IAttributeInstance instance = map.getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
            if(!instance.hasModifier(modMovespeedIncrease)) {
                instance.applyModifier(modMovespeedIncrease);
            }
            addAlignmentCharge(player, 0.002);
            setCooldownActiveForPlayer(player, 20);
        }
    }

    @Override
    public void onTimeout(EntityPlayer player) {
        AbstractAttributeMap map = player.getAttributeMap();
        IAttributeInstance instance = map.getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
        if(instance.hasModifier(modMovespeedIncrease)) {
            instance.removeModifier(modMovespeedIncrease);
        }
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        movespeedMultiplier = cfg.getFloat(getKey() + "SpeedMultipler", getConfigurationSection(), 0.2F, 0F, 5F, "Sets the movement-speed multiplier when the player has this perk.");
        setupModifier();
    }

    private static void setupModifier() {
        AttributeModifier mod = new AttributeModifier(modUUID, "Perk Travel Movespeed Bonus", movespeedMultiplier, 2);
        mod.setSaved(false);
        modMovespeedIncrease = mod;
    }

}
