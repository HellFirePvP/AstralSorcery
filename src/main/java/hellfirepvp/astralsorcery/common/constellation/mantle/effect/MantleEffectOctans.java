/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle.effect;

import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.event.EventFlags;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffectOctans
 * Created by HellFirePvP
 * Date: 20.02.2020 / 18:59
 */
public class MantleEffectOctans extends MantleEffect {

    public static OctansConfig CONFIG = new OctansConfig();

    public MantleEffectOctans() {
        super(ConstellationsAS.octans);
    }

    @Override
    protected void attachEventListeners(IEventBus bus) {
        super.attachEventListeners(bus);

        bus.addListener(this::handleUnderwaterBreakSpeed);
        bus.addListener(this::handleUnderwaterUnwavering);
    }

    @Override
    protected void tickServer(PlayerEntity player) {
        super.tickServer(player);

        if (player.areEyesInFluid(FluidTags.WATER)) {
            if (player.getAir() < (player.getMaxAir() - 20)) {
                player.setAir(player.getMaxAir());
            }

            player.heal(CONFIG.healPerTick.get().floatValue());
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void tickClient(PlayerEntity player) {
        super.tickClient(player);

        float chance = 0.1F;
        if (player.areEyesInFluid(FluidTags.WATER)) {
            chance = 0.3F;
        }
        this.playCapeSparkles(player, chance);
    }

    private void handleUnderwaterBreakSpeed(PlayerEvent.BreakSpeed event) {
        PlayerEntity player = event.getPlayer();
        if (player.areEyesInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(player)) {
            MantleEffectOctans octans = ItemMantle.getEffect(player, ConstellationsAS.octans);
            if (octans != null) {
                //Grab helmet
                ItemStack existing = player.getItemStackFromSlot(EquipmentSlotType.HEAD);

                //Set aqua affinity
                ItemStack st = new ItemStack(Items.LEATHER_HELMET);
                st.addEnchantment(Enchantments.AQUA_AFFINITY, 1);
                player.inventory.armorInventory.set(EquipmentSlotType.HEAD.getIndex(), st);

                //Recalc breakspeed
                EventFlags.CHECK_UNDERWATER_BREAK_SPEED.executeWithFlag(() -> {
                    event.setNewSpeed(player.getDigSpeed(event.getState(), event.getPos()));
                });

                //Reset helmet
                player.inventory.armorInventory.set(EquipmentSlotType.HEAD.getIndex(), existing);
            }
        }
    }

    private void handleUnderwaterUnwavering(LivingKnockBackEvent event) {
        if (event.getEntityLiving().areEyesInFluid(FluidTags.WATER)) {
            MantleEffectOctans octans = ItemMantle.getEffect(event.getEntityLiving(), ConstellationsAS.octans);
            if (octans != null) {
                event.setCanceled(true);
            }
        }
    }

    public static boolean shouldPreventWaterSlowdown(ItemStack elytraStack, LivingEntity wearingEntity) {
        if (elytraStack.getItem() instanceof ItemMantle) {
            MantleEffect effect = ItemMantle.getEffect(wearingEntity, ConstellationsAS.octans);
            return effect != null;
        }
        return false;
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    @Override
    protected boolean usesTickMethods() {
        return true;
    }

    public static class OctansConfig extends Config {

        private final double defaultHealPerTick = 0.01F;

        public ForgeConfigSpec.DoubleValue healPerTick;

        public OctansConfig() {
            super("octans");
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.healPerTick = cfgBuilder
                    .comment("Defines the amount of health that is healed while the wearer is in water. Can be set to 0 to disable this.")
                    .translation(translationKey("healPerTick"))
                    .defineInRange("healPerTick", this.defaultHealPerTick, 0.0, 5.0);
        }
    }
}
