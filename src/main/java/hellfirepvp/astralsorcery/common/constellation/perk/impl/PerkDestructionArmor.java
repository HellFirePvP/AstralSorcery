/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkDestructionArmor
 * Created by HellFirePvP
 * Date: 27.07.2017 / 18:19
 */
public class PerkDestructionArmor extends ConstellationPerk {

    private static float dropChance = 0.2F;
    private static float damageChanceMultiplier = 0.2F;

    public PerkDestructionArmor() {
        super("DTR_ARMORBREAK", Target.ENTITY_ATTACK);
    }

    @Override
    public float onEntityAttack(EntityPlayer attacker, EntityLivingBase attacked, float dmgIn) {
        if(!attacked.isDead) {
            float currentChance = MathHelper.clamp(dropChance + dmgIn * damageChanceMultiplier, 0F, 1F);
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                if(slot.getSlotType() != EntityEquipmentSlot.Type.ARMOR) continue;
                if(rand.nextFloat() < currentChance) continue;
                ItemStack stack = attacked.getItemStackFromSlot(slot);
                if(!stack.isEmpty()) {
                    addAlignmentCharge(attacker, 0.15);
                    attacked.setItemStackToSlot(slot, ItemStack.EMPTY);
                    ItemUtils.dropItemNaturally(attacked.world, attacked.posX, attacked.posY, attacked.posZ, stack);
                    break;
                }
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
        dropChance = cfg.getFloat(getKey() + "DropChance", getConfigurationSection(), dropChance, 0F, 1F, "Defines the chance (in percent) per hit to make the attacked entity drop its armor.");
        damageChanceMultiplier = cfg.getFloat(getKey() + "DamageChanceMultiplier", getConfigurationSection(), damageChanceMultiplier, 0F, 1F, "Defines the multiplier applied to damage to increase the drop chance. (Example: 2 damage at 1% would increase the chance by 2%)");
    }

}
