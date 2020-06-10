/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.node.key;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyDisarm
 * Created by HellFirePvP
 * Date: 31.08.2019 / 17:35
 */
public class KeyDisarm extends KeyPerk {

    private static final float defaultDropChance = 0.05F;

    private final Config config;

    public KeyDisarm(ResourceLocation name, int x, int y) {
        super(name, x, y);
        this.config = new Config(name.getPath());
    }

    @Override
    public void attachListeners(IEventBus bus) {
        super.attachListeners(bus);

        bus.addListener(EventPriority.HIGH, this::onAttack);
    }

    @Nullable
    @Override
    protected ConfigEntry addConfig() {
        return this.config;
    }

    private void onAttack(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source.getTrueSource() != null && source.getTrueSource() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) source.getTrueSource();
            LogicalSide side = this.getSide(player);
            PlayerProgress prog = ResearchHelper.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                float chance = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT, (float) this.applyMultiplierD(this.config.dropChance.get()));
                float currentChance = MathHelper.clamp(chance, 0F, 1F);
                for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                    if (rand.nextFloat() >= currentChance) {
                        continue;
                    }
                    LivingEntity attacked = event.getEntityLiving();
                    ItemStack stack = attacked.getItemStackFromSlot(slot);
                    if (!stack.isEmpty()) {
                        attacked.setItemStackToSlot(slot, ItemStack.EMPTY);
                        ItemUtils.dropItemNaturally(attacked.world, attacked.getPosX(), attacked.getPosY(), attacked.getPosZ(), stack);
                        break;
                    }
                }
            }
        }
    }

    public static class Config extends ConfigEntry {

        private ForgeConfigSpec.DoubleValue dropChance;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.dropChance = cfgBuilder
                    .comment("Defines the chance (in percent) per hit to make the attacked entity drop its armor.")
                    .translation(translationKey("dropChance"))
                    .defineInRange("dropChance", defaultDropChance, 0F, 1F);
        }
    }
}
