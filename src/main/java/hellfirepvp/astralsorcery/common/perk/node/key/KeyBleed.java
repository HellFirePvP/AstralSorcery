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
import hellfirepvp.astralsorcery.common.lib.EffectsAS;
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.node.KeyPerk;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyBleed
 * Created by HellFirePvP
 * Date: 25.08.2019 / 20:01
 */
public class KeyBleed extends KeyPerk {

    private static final int defaultBleedDuration = 40;
    private static final float defaultBleedChance = 0.25F;

    private final Config config;

    public KeyBleed(ResourceLocation name, int x, int y) {
        super(name, x, y);
        this.config = new Config(name.getPath());
    }

    @Override
    public void attachListeners(IEventBus bus) {
        super.attachListeners(bus);
        bus.addListener(this::onAttack);
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
                LivingEntity target = event.getEntityLiving();

                double chance = this.applyMultiplierD(this.config.bleedChance.get());
                chance = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_BLEED_CHANCE, (float) chance);
                if (rand.nextFloat() < chance) {
                    int stackCap = 3; //So the "real" stackcap is 'amplifier = 3' that means we always have to be lower than this value.
                    stackCap = Math.round(PerkAttributeHelper.getOrCreateMap(player, side)
                            .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_BLEED_STACKS, stackCap));
                    int duration = this.applyMultiplierI(this.config.bleedDuration.get());
                    duration = Math.round(PerkAttributeHelper.getOrCreateMap(player, side)
                            .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_BLEED_DURATION, duration));

                    int setAmplifier = 0;
                    if (target.isPotionActive(EffectsAS.EFFECT_BLEED)) {
                        EffectInstance pe = target.getActivePotionEffect(EffectsAS.EFFECT_BLEED);
                        if (pe != null) {
                            setAmplifier = Math.min(pe.getAmplifier() + 1, stackCap - 1);
                        }
                    }

                    target.addPotionEffect(new EffectInstance(EffectsAS.EFFECT_BLEED, duration, setAmplifier, false, true));
                }
            }
        }
    }

    private static class Config extends ConfigEntry {

        private ForgeConfigSpec.IntValue bleedDuration;
        private ForgeConfigSpec.DoubleValue bleedChance;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            bleedDuration = cfgBuilder
                    .comment("Defines the duration of the bleeding effect when applied. Refreshes this duration when a it is applied again")
                    .translation(translationKey("bleedDuration"))
                    .defineInRange("bleedDuration", defaultBleedDuration, 5, 400);

            bleedChance = cfgBuilder
                    .comment("Defines the base chance a bleed can/is applied when an entity is being hit by this entity")
                    .translation(translationKey("bleedChance"))
                    .defineInRange("bleedChance", defaultBleedChance, 0.01, 1);
        }
    }
}
