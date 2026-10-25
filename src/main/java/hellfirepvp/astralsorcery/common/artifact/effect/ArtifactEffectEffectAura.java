/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.artifact.ArtifactEffect;
import hellfirepvp.astralsorcery.common.artifact.ContinuousArtifactEffect;
import hellfirepvp.astralsorcery.common.entity.item.ItemEntityArtifact;
import hellfirepvp.astralsorcery.common.lib.types.ArtifactEffectTypesAS;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactEffectEffectAura
 * Created by HellFirePvP
 * Date: 24.10.2026 / 19:24
 */
public class ArtifactEffectEffectAura extends ArtifactEffect implements ContinuousArtifactEffect {

    public static final MapCodec<ArtifactEffectEffectAura> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ArtifactEffect::getId),
            Codec.INT.fieldOf("range").forGetter(ArtifactEffectEffectAura::getRange),
            MobEffectInstance.CODEC.listOf().fieldOf("effects").forGetter(ArtifactEffectEffectAura::getEffects)
    ).apply(inst, ArtifactEffectEffectAura::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtifactEffectEffectAura> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, ArtifactEffect::getId,
            ByteBufCodecs.INT, ArtifactEffectEffectAura::getRange,
            MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list()), ArtifactEffectEffectAura::getEffects,
            ArtifactEffectEffectAura::new);

    private final int range;
    private final List<MobEffectInstance> effects;

    protected ArtifactEffectEffectAura(ResourceLocation id, int range, List<MobEffectInstance> effects) {
        super(id);
        this.range = range;
        this.effects = effects;
    }

    public int getRange() {
        return this.range;
    }

    public List<MobEffectInstance> getEffects() {
        return Collections.unmodifiableList(this.effects);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public DeferredType<?> getType() {
        return ArtifactEffectTypesAS.EFFECT_AURA;
    }

    @Override
    public void tickEffect(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifact) {
        if (artifact.tickCount % 10 != 0) return;

        this.applyToEntities(sLevel, artifact);
        sLevel.sendParticles(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, PotionContents.getColor(this.getEffects())),
                artifact.getX(), artifact.getY() + 0.2, artifact.getZ(), this.getRange(),
                this.getRange() / 2D, 0.5, this.getRange() / 2D, 0);
    }

    @Override
    public void applyEffect(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifact) {
        this.applyToEntities(sLevel, artifact);
        sLevel.levelEvent(2002, artifact.blockPosition(), PotionContents.getColor(this.getEffects()));
    }

    private void applyToEntities(ServerLevel sLevel, ItemEntityArtifact artifact) {
        AABB bounds = artifact.getBoundingBox().inflate(this.getRange());
        sLevel.getEntities(EntityTypeTest.forClass(LivingEntity.class), bounds, EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(Entity::isAlive)).forEach(entity -> {
            this.getEffects().forEach(effect -> entity.addEffect(new MobEffectInstance(effect)));
        });
    }

    public static class Builder {

        private int range = 6;
        private final List<MobEffectInstance> effects = new ArrayList<>();

        public Builder setRange(int range) {
            this.range = range;
            return this;
        }

        public Builder addEffect(Holder<MobEffect> effect) {
            return this.addEffect(new MobEffectInstance(effect, 2 * 20, 0, true, true, false));
        }

        public Builder addEffect(Holder<MobEffect> effect, int duration) {
            return this.addEffect(new MobEffectInstance(effect, duration, 0, true, true, false));
        }

        public Builder addEffect(Holder<MobEffect> effect, int duration, int amplifier) {
            return this.addEffect(new MobEffectInstance(effect, duration, amplifier, true, true, false));
        }

        public Builder addEffect(MobEffectInstance effect) {
            this.effects.add(effect);
            return this;
        }

        public Provider build() {
            return id -> new ArtifactEffectEffectAura(id, this.range, List.copyOf(this.effects));
        }
    }
}
