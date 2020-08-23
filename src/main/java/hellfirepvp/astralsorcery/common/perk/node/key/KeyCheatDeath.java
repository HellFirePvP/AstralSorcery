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
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.lib.EffectsAS;
import hellfirepvp.astralsorcery.common.perk.CooldownPerk;
import hellfirepvp.astralsorcery.common.perk.PerkCooldownHelper;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyCheatDeath
 * Created by HellFirePvP
 * Date: 25.08.2019 / 21:52
 */
public class KeyCheatDeath extends KeyPerk implements CooldownPerk {

    private static final int defaultCooldownPotionApplication = 600;
    private static final int defaultPotionDuration = 500;
    private static final int defaultPotionAmplifier = 0;
    private static final int defaultChargeCost = 350;

    public static final Config CONFIG = new Config("key.cheat_death");

    public KeyCheatDeath(ResourceLocation name, float x, float y) {
        super(name, x, y);
    }

    @Override
    public void attachListeners(IEventBus bus) {
        super.attachListeners(bus);
        bus.addListener(EventPriority.HIGHEST, this::onDeath);
    }

    private void onDeath(LivingDeathEvent event) {
        if (event.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntityLiving();
            LogicalSide side = this.getSide(player);
            PlayerProgress progress = ResearchHelper.getProgress(player, side);
            if (side.isServer() && progress.hasPerkEffect(this)) {
                if (!PerkCooldownHelper.isCooldownActiveForPlayer(player, this) &&
                        AlignmentChargeHandler.INSTANCE.drainCharge(player, side, CONFIG.chargeCost.get(), false)) {
                    PerkCooldownHelper.setCooldownActiveForPlayer(player, this, this.applyMultiplierI(CONFIG.cooldownPotionApplication.get()));
                    player.addPotionEffect(new EffectInstance(EffectsAS.EFFECT_CHEAT_DEATH,
                            this.applyMultiplierI(CONFIG.potionDuration.get()),
                            this.applyMultiplierI(CONFIG.potionAmplifier.get()),
                            true, false, true));
                }
            }
        }
    }

    @Override
    public void onCooldownTimeout(PlayerEntity player) {}

    private static class Config extends ConfigEntry {

        private ForgeConfigSpec.IntValue cooldownPotionApplication;
        private ForgeConfigSpec.IntValue potionDuration;
        private ForgeConfigSpec.IntValue potionAmplifier;
        private ForgeConfigSpec.IntValue chargeCost;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            cooldownPotionApplication = cfgBuilder
                    .comment("Once the potion effect gets applied, it'll take at least this amount of ticks or a server restart until it can be re-applied by this perk.")
                    .translation(translationKey("cooldownPotionApplication"))
                    .defineInRange("cooldownPotionApplication", defaultCooldownPotionApplication, 1, Integer.MAX_VALUE);
            potionDuration = cfgBuilder
                    .comment("Once the potion effect gets applied by any of the triggers, this will be used as tick-duration of the potion effect.")
                    .translation(translationKey("potionDuration"))
                    .defineInRange("potionDuration", defaultPotionDuration, 1, Integer.MAX_VALUE);
            potionAmplifier = cfgBuilder
                    .comment("Once the potion effect gets applied by any of the triggers, this will be used as amplifier of the potion effect.")
                    .translation(translationKey("potionAmplifier"))
                    .defineInRange("potionAmplifier", defaultPotionAmplifier, 0, 4);
            chargeCost = cfgBuilder
                    .comment("Defines the amount of starlight charge consumed per death-prevention.")
                    .translation(translationKey("chargeCost"))
                    .defineInRange("chargeCost", defaultChargeCost, 1, 500);
        }
    }
}
