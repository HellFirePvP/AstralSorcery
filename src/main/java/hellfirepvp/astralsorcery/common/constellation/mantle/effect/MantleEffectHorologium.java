/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.mantle.effect;

import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.item.armor.ItemMantle;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.time.TimeStopController;
import hellfirepvp.astralsorcery.common.util.time.TimeStopZone;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MantleEffectHorologium
 * Created by HellFirePvP
 * Date: 22.02.2020 / 14:07
 */
public class MantleEffectHorologium extends MantleEffect {

    public static HorologiumConfig CONFIG = new HorologiumConfig();

    public MantleEffectHorologium() {
        super(ConstellationsAS.horologium);
    }

    @Override
    protected void attachEventListeners(IEventBus bus) {
        super.attachEventListeners(bus);
        bus.addListener(this::onHurt);
    }

    @Override
    protected boolean usesTickMethods() {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void tickClient(PlayerEntity player) {
        super.tickClient(player);

        if (!player.getCooldownTracker().hasCooldown(ItemsAS.MANTLE)) {
            this.playCapeSparkles(player, 0.4F);
        } else {
            this.playCapeSparkles(player, 0.2F);
        }
    }

    private void onHurt(LivingHurtEvent event) {
        if (ItemMantle.getEffect(event.getEntityLiving(), ConstellationsAS.horologium) != null &&
                event.getEntityLiving() instanceof PlayerEntity &&
                !event.getEntityLiving().getEntityWorld().isRemote() &&
                !event.getSource().isFireDamage()) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();

            if (!player.getCooldownTracker().hasCooldown(ItemsAS.MANTLE) &&
                    AlignmentChargeHandler.INSTANCE.hasCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerFreeze.get())) {
                TimeStopController.freezeWorldAt(
                        TimeStopZone.EntityTargetController.allExcept(player),
                        player.getEntityWorld(),
                        player.getPosition(),
                        CONFIG.effectRange.get().floatValue(),
                        CONFIG.effectDuration.get());
                player.getCooldownTracker().setCooldown(ItemsAS.MANTLE, CONFIG.cooldown.get());
                AlignmentChargeHandler.INSTANCE.drainCharge(player, LogicalSide.SERVER, CONFIG.chargeCostPerFreeze.get(), false);
            }
        }
    }

    @Override
    public Config getConfig() {
        return CONFIG;
    }

    public static class HorologiumConfig extends Config {

        private final double defaultEffectRange = 20.0;
        private final int defaultEffectDuration = 180;
        private final int defaultCooldown = 1000;

        private final int defaultChargeCostPerFreeze = 400;

        public ForgeConfigSpec.DoubleValue effectRange;
        public ForgeConfigSpec.IntValue effectDuration;
        public ForgeConfigSpec.IntValue cooldown;

        public ForgeConfigSpec.IntValue chargeCostPerFreeze;


        public HorologiumConfig() {
            super("horologium");
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            super.createEntries(cfgBuilder);

            this.effectRange = cfgBuilder
                    .comment("Defines the range of the time-freeze bubble.")
                    .translation(translationKey("effectRange"))
                    .defineInRange("effectRange", this.defaultEffectRange, 4.0, 64.0);
            this.effectDuration = cfgBuilder
                    .comment("Defines the duration of the time-freeze bubble.")
                    .translation(translationKey("effectDuration"))
                    .defineInRange("effectDuration", this.defaultEffectDuration, 40, 1000);
            this.cooldown = cfgBuilder
                    .comment("Defines the cooldown for the time-freeze effect after it triggered (should be longer than duration maybe)")
                    .translation(translationKey("cooldown"))
                    .defineInRange("cooldown", this.defaultCooldown, 40, 20_000);

            this.chargeCostPerFreeze = cfgBuilder
                    .comment("Set the amount alignment charge consumed per created time stop zone")
                    .translation(translationKey("chargeCostPerFreeze"))
                    .defineInRange("chargeCostPerFreeze", this.defaultChargeCostPerFreeze, 0, 1000);
        }
    }
}
