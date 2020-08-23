/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import hellfirepvp.astralsorcery.common.perk.tick.PlayerTickPerk;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyMending
 * Created by HellFirePvP
 * Date: 31.08.2019 / 21:28
 */
public class KeyMending extends KeyPerk implements PlayerTickPerk {

    private static final int defaultChanceToRepair = 800;
    private static final int defaultChargeCost = 60;

    public static final Config CONFIG = new Config("key.mending");

    public KeyMending(ResourceLocation name, float x, float y) {
        super(name, x, y);
    }

    @Override
    public void onPlayerTick(PlayerEntity player, LogicalSide side) {
        if (side.isServer()) {
            int repairChance = this.applyMultiplierI(CONFIG.chanceToRepair.get());
            repairChance /= PerkAttributeHelper.getOrCreateMap(player, side)
                    .getModifier(player, ResearchHelper.getProgress(player, side), PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT);
            repairChance = Math.max(repairChance, 1);
            for (ItemStack armor : player.getArmorInventoryList()) {
                if (rand.nextInt(repairChance) != 0) {
                    continue;
                }
                if (!armor.isEmpty() && armor.isDamageable() && armor.isDamaged()) {
                    if (AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCost.get(), false)) {
                        armor.setDamage(armor.getDamage() - 1);
                    }
                }
            }
        }
    }

    public static class Config extends ConfigEntry {

        private ForgeConfigSpec.IntValue chanceToRepair;
        private ForgeConfigSpec.IntValue chargeCost;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.chanceToRepair = cfgBuilder
                    .comment("Sets the chance (Random.nextInt(chance) == 0) to try to see if a piece of armor on the player that is damageable and damaged can be repaired; the lower the more likely.")
                    .translation(translationKey("chanceToRepair"))
                    .defineInRange("chanceToRepair", defaultChanceToRepair, 5, Integer.MAX_VALUE);
            this.chargeCost = cfgBuilder
                    .comment("Defines the amount of starlight charge consumed per restored durability point.")
                    .translation(translationKey("chargeCost"))
                    .defineInRange("chargeCost", defaultChargeCost, 1, 500);
        }
    }
}
