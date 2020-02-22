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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyDamageEffects
 * Created by HellFirePvP
 * Date: 31.08.2019 / 17:26
 */
public class KeyDamageEffects extends KeyPerk {

    private static final float defaultApplicationChance = 0.08F;

    private final Config config;

    public KeyDamageEffects(ResourceLocation name, int x, int y) {
        super(name, x, y);
        this.config = new Config(name.getPath());
    }

    @Override
    public void attachListeners(IEventBus bus) {
        super.attachListeners(bus);

        bus.addListener(EventPriority.LOWEST, this::onDamageResult);
    }

    @Nullable
    @Override
    protected ConfigEntry addConfig() {
        return this.config;
    }

    private void onDamageResult(LivingDamageEvent event) {
        DamageSource source = event.getSource();
        if (source.getTrueSource() != null && source.getTrueSource() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) source.getTrueSource();
            LogicalSide side = this.getSide(player);
            PlayerProgress prog = ResearchHelper.getProgress(player, side);
            if (prog.hasPerkEffect(this)) {
                LivingEntity attacked = event.getEntityLiving();
                float chance = PerkAttributeHelper.getOrCreateMap(player, side)
                        .modifyValue(player, prog, PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EFFECT, (float) this.applyMultiplierD(this.config.applicationChance.get()));
                if (rand.nextFloat() < chance) {
                    switch (rand.nextInt(3)) {
                        case 0:
                            attacked.addPotionEffect(new EffectInstance(Effects.WITHER, 200, 1, false, false, true));
                            break;
                        case 1:
                            attacked.addPotionEffect(new EffectInstance(Effects.POISON, 200, 1, false, false, true));
                            break;
                        case 2:
                            attacked.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 200, 1, false, false, true));
                            attacked.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 200, 1, false, false, true));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    public static class Config extends ConfigEntry {

        private ForgeConfigSpec.DoubleValue applicationChance;

        private Config(String section) {
            super(section);
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            this.applicationChance = cfgBuilder
                    .comment("Defines the chance per hit to apply additional effects.")
                    .translation(translationKey("applicationChance"))
                    .defineInRange("applicationChance", defaultApplicationChance, 0.01F, 0.2F);
        }
    }
}
