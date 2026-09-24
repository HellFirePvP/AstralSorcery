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
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.Collection;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: KeyPerkLastBreath
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class KeyPerkLastBreath extends KeyPerk {

    public static final MapCodec<KeyPerkLastBreath> CODEC = RecordCodecBuilder.mapCodec(inst -> perkModifierFields(inst).apply(inst, KeyPerkLastBreath::new));
    public static final PerkType<KeyPerkLastBreath> TYPE =
            PerkType.of(KeyPerkLastBreath.CODEC, PerkDataTypesAS.DEFAULT_DATA, KeyPerkLastBreath::new);
    public static final Config CONFIG = new Config();

    private KeyPerkLastBreath(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.MAJOR, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    protected KeyPerkLastBreath(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
    }

    @Override
    protected void attachEventListeners(SidedEventBus sidedEventBus) {
        super.attachEventListeners(sidedEventBus);
        sidedEventBus.addListener(LivingIncomingDamageEvent.class, SidedEventBus.entityEvent(), this::onAttack);
        sidedEventBus.addListener(PlayerEvent.BreakSpeed.class, SidedEventBus.entityEvent(), this::onBreakSpeed);
    }

    private void onAttack(LivingIncomingDamageEvent event) {
        Entity source = event.getSource().getEntity();
        if (!(source instanceof ServerPlayer sPlayer)) return;
        LogicalSide side = this.getSide(sPlayer);
        if (!side.isServer()) return;
        PlayerProgress progress = ResearchManager.getProgress(sPlayer, side);
        if (!progress.getPerkData().hasPerkEffect(this)) return;

        float perc = PerkManager.getOrCreateAttributes(sPlayer)
                .modifyValue(sPlayer, progress, PerksAS.AttributeTypes.PERK_EFFECT, CONFIG.damageMultiplier.get().floatValue());
        float healthPerc = 1F - (sPlayer.getHealth() / sPlayer.getMaxHealth());
        event.setAmount(event.getAmount() * (1F + (healthPerc * perc)));
    }

    private void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        LogicalSide side = this.getSide(player);
        if (!side.isServer()) return;
        PlayerProgress progress = ResearchManager.getProgress(player, side);
        if (!progress.getPerkData().hasPerkEffect(this)) return;

        float perc = PerkManager.getOrCreateAttributes(player)
                .modifyValue(player, progress, PerksAS.AttributeTypes.PERK_EFFECT, CONFIG.digSpeedMultiplier.get().floatValue());
        float healthPerc = 1F - (player.getHealth() / player.getMaxHealth());
        event.setNewSpeed(event.getNewSpeed() * (1F + (healthPerc * perc)));
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }

    public static class Config extends ConfigEntry {

        public ModConfigSpec.DoubleValue damageMultiplier;
        public ModConfigSpec.DoubleValue digSpeedMultiplier;

        public Config() {
            super("key_last_breath");
        }

        @Override
        public void createEntries(ModConfigSpec.Builder cfgBuilder) {
            this.damageMultiplier = cfgBuilder
                    .comment("Maximum additional multiplier to damage dealt, the lower hp gets, from 0 to this value when health reaches 0.")
                    .translation(translationKey("damageMultiplier"))
                    .defineInRange("damageMultiplier", 3, 0.1, 50);
            this.digSpeedMultiplier = cfgBuilder
                    .comment("Maximum additional multiplier to dig speed, the lower hp gets, from 0 to this value when health reaches 0.")
                    .translation(translationKey("digSpeedMultiplier"))
                    .defineInRange("digSpeedMultiplier", 2, 0.1, 50);
        }
    }
}
