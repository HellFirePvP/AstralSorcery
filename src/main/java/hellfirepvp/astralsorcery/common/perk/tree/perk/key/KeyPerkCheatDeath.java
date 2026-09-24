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
import hellfirepvp.astralsorcery.common.lib.types.PerkDataTypesAS;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tick.PerkCooldownHelper;
import hellfirepvp.astralsorcery.common.perk.tree.PerkCategory;
import hellfirepvp.astralsorcery.common.perk.tree.PerkType;
import hellfirepvp.astralsorcery.common.perk.tree.perk.KeyPerk;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.ServerSoundHelper;
import hellfirepvp.astralsorcery.common.util.event.SidedEventBus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.Collection;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: KeyPerkCheatDeath
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class KeyPerkCheatDeath extends KeyPerk {

    public static final MapCodec<KeyPerkCheatDeath> CODEC = RecordCodecBuilder.mapCodec(inst -> perkModifierFields(inst).apply(inst, KeyPerkCheatDeath::new));
    public static final PerkType<KeyPerkCheatDeath> TYPE =
            PerkType.of(KeyPerkCheatDeath.CODEC, PerkDataTypesAS.DEFAULT_DATA, KeyPerkCheatDeath::new);
    public static final Config CONFIG = new Config();

    private KeyPerkCheatDeath(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.MAJOR, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    protected KeyPerkCheatDeath(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
    }

    @Override
    protected void attachEventListeners(SidedEventBus sidedEventBus) {
        super.attachEventListeners(sidedEventBus);
        sidedEventBus.addListener(LivingDeathEvent.class, SidedEventBus.entityEvent(), this::onDeath);
    }

    private void onDeath(LivingDeathEvent event) {
        Entity died = event.getEntity();
        if (!(died instanceof ServerPlayer sPlayer)) return;
        if (PerkCooldownHelper.isCooldownActiveForPlayer(sPlayer, this)) return;
        LogicalSide side = this.getSide(sPlayer);
        if (!side.isServer()) return;
        PlayerProgress progress = ResearchManager.getProgress(sPlayer, side);
        if (!progress.getPerkData().hasPerkEffect(this)) return;

        //Yea this doesn't scale with perk effect. it's intentional. rip whoever is reading this looking for this information.
        PerkCooldownHelper.setCooldownActiveForPlayer(sPlayer, this, CONFIG.cooldownTicks.getAsInt());
        ServerSoundHelper.playSoundAround(SoundEvents.TOTEM_USE, SoundSource.PLAYERS, sPlayer.level(), sPlayer.position(), 1F, 1F);
        sPlayer.setHealth(1F);
        sPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 2));
        event.setCanceled(true);
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }

    public static class Config extends ConfigEntry {

        public ModConfigSpec.IntValue cooldownTicks;

        private Config() {
            super("key_cheat_death");
        }

        @Override
        public void createEntries(ModConfigSpec.Builder cfgBuilder) {
            this.cooldownTicks = cfgBuilder
                    .comment("Cooldown in ticks after cheat death from this perk procs.")
                    .translation(translationKey("cooldownTicks"))
                    .defineInRange("cooldownTicks", 1800, 0, Integer.MAX_VALUE);
        }
    }
}
