/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.key;

import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes.KeyPerk;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyDisarm
 * Created by HellFirePvP
 * Date: 20.07.2018 / 18:08
 */
public class KeyDisarm extends KeyPerk {

    private static float dropChance = 0.05F;

    public KeyDisarm(String name, int x, int y) {
        super(name, x, y);
        Config.addDynamicEntry(new ConfigEntry(ConfigEntry.Section.PERKS, name) {
            @Override
            public void loadFromConfig(Configuration cfg) {
                dropChance = cfg.getFloat("DropChance", getConfigurationSection(), dropChance, 0F, 1F,
                        "Defines the chance (in percent) per hit to make the attacked entity drop its armor.");
            }
        });
    }

    @SubscribeEvent
    public void onAttack(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) source.getTrueSource();
            Side side = player.world.isRemote ? Side.CLIENT : Side.SERVER;
            PlayerProgress prog = ResearchManager.getProgress(player, side);
            if (prog != null && prog.hasPerkEffect(this)) {
                float chance = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, dropChance);
                float currentChance = MathHelper.clamp(chance, 0F, 1F);
                for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                    if(slot.getSlotType() != EntityEquipmentSlot.Type.ARMOR) continue;
                    if(rand.nextFloat() < currentChance) continue;
                    EntityLivingBase attacked = event.getEntityLiving();
                    ItemStack stack = attacked.getItemStackFromSlot(slot);
                    if(!stack.isEmpty()) {
                        attacked.setItemStackToSlot(slot, ItemStack.EMPTY);
                        ItemUtils.dropItemNaturally(attacked.world, attacked.posX, attacked.posY, attacked.posZ, stack);
                        break;
                    }
                }
            }
        }
    }

}
