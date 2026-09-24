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
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.event.SidedEventBus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: KeyPerkCleanseNegativeEffects
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class KeyPerkCleanseNegativeEffects extends KeyPerk {

    public static final MapCodec<KeyPerkCleanseNegativeEffects> CODEC = RecordCodecBuilder.mapCodec(inst -> perkModifierFields(inst).apply(inst, KeyPerkCleanseNegativeEffects::new));
    public static final PerkType<KeyPerkCleanseNegativeEffects> TYPE =
            PerkType.of(KeyPerkCleanseNegativeEffects.CODEC, PerkDataTypesAS.DEFAULT_DATA, KeyPerkCleanseNegativeEffects::new);

    private KeyPerkCleanseNegativeEffects(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.MAJOR, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    protected KeyPerkCleanseNegativeEffects(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
    }

    @Override
    protected void attachEventListeners(SidedEventBus sidedEventBus) {
        super.attachEventListeners(sidedEventBus);
        sidedEventBus.addListener(LivingHealEvent.class, SidedEventBus.entityEvent(), this::onHeal);
    }

    private void onHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sPlayer)) return;
        LogicalSide side = this.getSide(sPlayer);
        PlayerProgress prog = ResearchManager.getProgress(sPlayer, side);
        if (!prog.getPerkData().hasPerkEffect(this)) return;

        List<MobEffectInstance> badEffects = sPlayer.getActiveEffects().stream()
                .filter(effect -> effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL)
                .toList();
        if (badEffects.isEmpty()) return;
        MiscUtil.getRandomEntry(badEffects, this.rand).ifPresent(effect -> {
            float removeChance = PerkManager.getOrCreateAttributes(sPlayer).modifyValue(sPlayer, prog, PerksAS.AttributeTypes.PERK_EFFECT, 0.33F);
            float chance = this.getChance(event.getAmount()) * removeChance;
            if (this.rand.nextFloat() < chance) {
                sPlayer.removeEffect(effect.getEffect());
            }
        });
    }

    private float getChance(float healAmount) {
        if (healAmount <= 0) return 0;
        return Math.min(1, healAmount / 20F);
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }
}
