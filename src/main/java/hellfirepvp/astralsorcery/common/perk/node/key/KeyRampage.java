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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
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
 * Class: KeyRampage
 * Created by HellFirePvP
 * Date: 31.08.2019 / 22:07
 */
public class KeyRampage extends KeyPerk {

    private static final float defaultRampageChance = 1F;
    private static final int defaultRampageDuration = 100;

    private final Config config;

    public KeyRampage(ResourceLocation name, int x, int y) {
        super(name, x, y);
        this.config = new Config(name.getPath());
    }

    @Override
    public void attachListeners(IEventBus bus) {
        super.attachListeners(bus);

        bus.addListener(EventPriority.LOWEST, this::onEntityDeath);
    }

    @Nullable
    @Override
    protected ConfigEntry addConfig() {
        return this.config;
    }

    private void onEntityDeath(LivingDeathEvent event) {
        DamageSource source = event.getSource();
        if (source.getTrueSource() != null && source.getTrueSource() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) source.getTrueSource();
            LogicalSide side = this.getSide(player);
            PlayerProgress prog = ResearchHelper.getProgress(player, side);
            if (side.isServer() && prog.hasPerkEffect(this)) {
                float ch = (float) this.applyMultiplierD(this.config.rampageChance.get());
                ch = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT, ch);
                if (rand.nextFloat() < ch) {

                    int dur = (int) this.applyMultiplierD(this.config.rampageDuration.get());
                    dur = Math.round(PerkAttributeHelper.getOrCreateMap(player, side)
                            .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_RAMPAGE_DURATION, dur));
                    dur = Math.round(PerkAttributeHelper.getOrCreateMap(player, side)
                            .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT, dur));
                    if (dur > 0) {
                        player.addPotionEffect(new EffectInstance(Effects.SPEED, dur, 1, false, false, true));
                        player.addPotionEffect(new EffectInstance(Effects.HASTE, dur, 1, false, false, true));
                        player.addPotionEffect(new EffectInstance(Effects.STRENGTH, dur, 1, false, false, true));
                    }
                }
            }
        }
    }

    public static class Config extends ConfigEntry {

        private ForgeConfigSpec.DoubleValue rampageChance;
        private ForgeConfigSpec.IntValue rampageDuration;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.rampageChance = cfgBuilder
                    .comment("Defines the chance to gain rampage buffs when killing a mob")
                    .translation(translationKey("rampageChance"))
                    .defineInRange("rampageChance", defaultRampageChance, 0.05F, 1F);

            this.rampageDuration = cfgBuilder
                    .comment("Defines the duration of the rampage in ticks")
                    .translation(translationKey("rampageDuration"))
                    .defineInRange("rampageDuration", defaultRampageDuration, 10, 100_000);
        }
    }
}
