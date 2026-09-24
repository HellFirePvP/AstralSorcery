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
import hellfirepvp.astralsorcery.common.entity.item.ItemEntityArtifact;
import hellfirepvp.astralsorcery.common.lib.types.ArtifactEffectTypesAS;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactEffectEffects
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactEffectEffects extends ArtifactEffect {

    public static final MapCodec<ArtifactEffectEffects> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ArtifactEffect::getId),
            Codec.INT.fieldOf("range").forGetter(ArtifactEffectEffects::getRange),
            MobEffectInstance.CODEC.listOf().fieldOf("effects").forGetter(ArtifactEffectEffects::getEffects)
    ).apply(inst, ArtifactEffectEffects::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtifactEffectEffects> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ArtifactEffect::getId,
            ByteBufCodecs.INT,
            ArtifactEffectEffects::getRange,
            MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ArtifactEffectEffects::getEffects,
            ArtifactEffectEffects::new);

    private final int range;
    private final List<MobEffectInstance> effects;

    protected ArtifactEffectEffects(ResourceLocation id, int range, List<MobEffectInstance> effects) {
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

    public static Builder of() {
        return new Builder();
    }

    @Override
    public DeferredType<?> getType() {
        return ArtifactEffectTypesAS.EFFECTS;
    }

    @Override
    public void applyEffect(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifact) {
        AABB bounds = artifact.getBoundingBox().inflate(this.getRange());
        sLevel.getEntities(EntityTypeTest.forClass(LivingEntity.class), bounds, Entity::isAlive).forEach(entity -> {
            this.getEffects().forEach(effect -> entity.addEffect(new MobEffectInstance(effect)));
        });

        PotionContents contents = new PotionContents(Optional.empty(), Optional.empty(), this.getEffects());
        sLevel.levelEvent(2002, artifact.blockPosition(), contents.getColor());
    }

    public static class Builder {

        private int range = 8;
        private final List<MobEffectInstance> effects = new ArrayList<>();

        public Builder setRange(int range) {
            this.range = range;
            return this;
        }

        public Builder addEffect(Holder<MobEffect> effect) {
            return this.addEffect(new MobEffectInstance(effect, 5 * 20, 0, true, true, false));
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
            return id -> new ArtifactEffectEffects(id, this.range, List.copyOf(this.effects));
        }
    }
}
