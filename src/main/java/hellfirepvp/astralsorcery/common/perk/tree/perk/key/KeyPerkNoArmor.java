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
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.Collection;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: KeyPerkNoArmor
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class KeyPerkNoArmor extends KeyPerk {

    public static final MapCodec<KeyPerkNoArmor> CODEC = RecordCodecBuilder.mapCodec(inst -> perkModifierFields(inst).apply(inst, KeyPerkNoArmor::new));
    public static final PerkType<KeyPerkNoArmor> TYPE =
            PerkType.of(KeyPerkNoArmor.CODEC, PerkDataTypesAS.DEFAULT_DATA, KeyPerkNoArmor::new);
    public static final Config CONFIG = new Config();

    private KeyPerkNoArmor(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.MAJOR, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    protected KeyPerkNoArmor(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
    }

    @Override
    protected void attachEventListeners(SidedEventBus sidedEventBus) {
        super.attachEventListeners(sidedEventBus);
        sidedEventBus.addListener(LivingDamageEvent.Pre.class, SidedEventBus.entityEvent(), this::onDamagePre);
    }

    private void onDamagePre(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer sPlayer)) return;
        LogicalSide side = this.getSide(sPlayer);
        if (!side.isServer()) return;
        PlayerProgress progress = ResearchManager.getProgress(sPlayer, side);
        if (!progress.getPerkData().hasPerkEffect(this)) return;

        int foundArmorPieces = 0;
        for (ItemStack stack : sPlayer.getArmorSlots()) {
            if (!stack.isEmpty()) foundArmorPieces++;
        }
        if (foundArmorPieces > 1) return;

        float mult = PerkManager.getOrCreateAttributes(sPlayer)
                .modifyValue(sPlayer, progress, PerksAS.AttributeTypes.PERK_EFFECT, 1F - CONFIG.damageTakenMultiplier.get().floatValue());
        event.setNewDamage(event.getNewDamage() * Mth.clamp(1F - mult, 0F, 1F));
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }

    public static class Config extends ConfigEntry {

        public ModConfigSpec.DoubleValue damageTakenMultiplier;

        private Config() {
            super("key_no_armor");
        }

        @Override
        public void createEntries(ModConfigSpec.Builder cfgBuilder) {
            this.damageTakenMultiplier = cfgBuilder
                    .comment("Damage taken multiplier when only wearing 1 armor piece.")
                    .translation(translationKey("damageTakenMultiplier"))
                    .defineInRange("damageTakenMultiplier", 0.7D, 0.1D, 1D);
        }
    }
}
