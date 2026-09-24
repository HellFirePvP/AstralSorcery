/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree.perk.key;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.config.ConfigEntry;
import hellfirepvp.astralsorcery.common.lib.PerksAS;
import hellfirepvp.astralsorcery.common.lib.types.PerkDataTypesAS;
import hellfirepvp.astralsorcery.common.perk.PerkManager;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tree.PerkCategory;
import hellfirepvp.astralsorcery.common.perk.tree.PerkType;
import hellfirepvp.astralsorcery.common.perk.tree.perk.KeyPerk;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.event.SidedEventBus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.Collection;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: KeyPerkDamageEffects
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class KeyPerkDamageEffects extends KeyPerk {

    public static final MapCodec<KeyPerkDamageEffects> CODEC = RecordCodecBuilder.mapCodec(inst -> perkModifierFields(inst).apply(inst, KeyPerkDamageEffects::new));
    public static final PerkType<KeyPerkDamageEffects> TYPE =
            PerkType.of(KeyPerkDamageEffects.CODEC, PerkDataTypesAS.DEFAULT_DATA, KeyPerkDamageEffects::new);
    public static final Config CONFIG = new Config();

    private KeyPerkDamageEffects(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.MAJOR, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    protected KeyPerkDamageEffects(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
    }

    @Override
    protected void attachEventListeners(SidedEventBus sidedEventBus) {
        super.attachEventListeners(sidedEventBus);
        sidedEventBus.addListener(LivingDamageEvent.Post.class, SidedEventBus.entityEvent(), this::onAttacked);
    }

    private void onAttacked(LivingDamageEvent.Post event) {
        Entity source = event.getSource().getEntity();
        if (!(source instanceof ServerPlayer sPlayer)) return;
        LogicalSide side = this.getSide(sPlayer);
        if (!side.isServer()) return;
        PlayerProgress progress = ResearchManager.getProgress(sPlayer, side);
        if (!progress.getPerkData().hasPerkEffect(this)) return;
        LivingEntity target = event.getEntity();

        float chance = PerkManager.getOrCreateAttributes(sPlayer)
                .modifyValue(sPlayer, progress, PerksAS.AttributeTypes.PERK_EFFECT, CONFIG.effectApplicationChance.get().floatValue());
        if (this.rand.nextFloat() < chance) {
            switch (rand.nextInt(4)) {
                case 0 -> target.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 1));
                case 1 -> target.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));
                case 2 -> target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
                case 3 -> target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
            }
        }
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }

    public static class Config extends ConfigEntry {

        public ModConfigSpec.DoubleValue effectApplicationChance;

        public Config() {
            super("key_damage_effects");
        }

        @Override
        public void createEntries(ModConfigSpec.Builder cfgBuilder) {
            this.effectApplicationChance = cfgBuilder
                    .comment("Chance to apply a negative effect to the target on hit.")
                    .translation(translationKey("effectApplicationChance"))
                    .defineInRange("effectApplicationChance", 0.2D, 0D, 1D);
        }
    }
}
