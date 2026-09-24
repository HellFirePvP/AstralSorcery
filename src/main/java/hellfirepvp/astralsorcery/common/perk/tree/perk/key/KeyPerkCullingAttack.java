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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.Collection;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: KeyPerkCullingAttack
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class KeyPerkCullingAttack extends KeyPerk {

    public static final MapCodec<KeyPerkCullingAttack> CODEC = RecordCodecBuilder.mapCodec(inst -> perkModifierFields(inst).apply(inst, KeyPerkCullingAttack::new));
    public static final PerkType<KeyPerkCullingAttack> TYPE =
            PerkType.of(KeyPerkCullingAttack.CODEC, PerkDataTypesAS.DEFAULT_DATA, KeyPerkCullingAttack::new);
    public static final Config CONFIG = new Config();

    private KeyPerkCullingAttack(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.MAJOR, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    protected KeyPerkCullingAttack(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
    }

    @Override
    protected void attachEventListeners(SidedEventBus sidedEventBus) {
        super.attachEventListeners(sidedEventBus);
        sidedEventBus.addListener(EventPriority.LOW, LivingIncomingDamageEvent.class, SidedEventBus.entityEvent(), this::onDamage);
    }

    private void onDamage(LivingIncomingDamageEvent event) {
        Entity source = event.getSource().getEntity();
        if (!(source instanceof ServerPlayer sPlayer)) return;
        LogicalSide side = this.getSide(sPlayer);
        if (!side.isServer()) return;
        PlayerProgress progress = ResearchManager.getProgress(sPlayer, side);
        if (!progress.getPerkData().hasPerkEffect(this)) return;
        LivingEntity target = event.getEntity();

        float actualCullHealth = PerkManager.getOrCreateAttributes(sPlayer)
                .modifyValue(sPlayer, progress, PerksAS.AttributeTypes.PERK_EFFECT, CONFIG.cullHealthPercentage.get().floatValue());
        float lifePerc = target.getHealth() / target.getMaxHealth();
        if (lifePerc < actualCullHealth) {
            target.setHealth(0);
            target.getEntityData().set(LivingEntity.DATA_HEALTH_ID, 0F);
        }
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }

    public static class Config extends ConfigEntry {

        public ModConfigSpec.DoubleValue cullHealthPercentage;

        private Config() {
            super("key_cull_attack");
        }

        @Override
        public void createEntries(ModConfigSpec.Builder cfgBuilder) {
            this.cullHealthPercentage = cfgBuilder
                    .comment("The health percentage at how low the target's relative health needs to be for it to be killed by the culling attack.")
                    .translation(translationKey("cullHealthPercentage"))
                    .defineInRange("cullHealthPercentage", 0.15, 0, 1);
        }
    }
}
