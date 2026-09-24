/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.artifact.ArtifactCondition;
import hellfirepvp.astralsorcery.common.entity.item.ItemEntityArtifact;
import hellfirepvp.astralsorcery.common.lib.types.ArtifactConditionTypesAS;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactConditionDamageType
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactConditionDamageType extends ArtifactCondition implements OnHitArtifactCondition {

    public static final MapCodec<ArtifactConditionDamageType> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ArtifactCondition::getId),
            ResourceKey.codec(Registries.DAMAGE_TYPE).listOf().fieldOf("damageTypes").forGetter(ArtifactConditionDamageType::getDamageTypes),
            TagKey.codec(Registries.DAMAGE_TYPE).listOf().fieldOf("damageTypeTags").forGetter(ArtifactConditionDamageType::getDamageTypeTags),
            ComponentSerialization.CODEC.fieldOf("hiddenDescription").forGetter(ArtifactConditionDamageType::getHiddenDescription),
            ComponentSerialization.CODEC.fieldOf("clearDescription").forGetter(ArtifactConditionDamageType::getClearDescription)
    ).apply(inst, ArtifactConditionDamageType::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtifactConditionDamageType> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ArtifactCondition::getId,
            ResourceKey.streamCodec(Registries.DAMAGE_TYPE).apply(ByteBufCodecs.list()),
            ArtifactConditionDamageType::getDamageTypes,
            CodecUtil.tagKeyStreamCodec(Registries.DAMAGE_TYPE).apply(ByteBufCodecs.list()),
            ArtifactConditionDamageType::getDamageTypeTags,
            ComponentSerialization.STREAM_CODEC,
            ArtifactConditionDamageType::getHiddenDescription,
            ComponentSerialization.STREAM_CODEC,
            ArtifactConditionDamageType::getClearDescription,
            ArtifactConditionDamageType::new);

    private final List<ResourceKey<DamageType>> damageTypes;
    private final List<TagKey<DamageType>> damageTypeTags;
    private final Component hiddenDescription;
    private final Component clearDescription;

    protected ArtifactConditionDamageType(ResourceLocation id, List<ResourceKey<DamageType>> damageTypes, List<TagKey<DamageType>> damageTypeTags, Component hiddenDescription, Component clearDescription) {
        super(id);
        this.damageTypes = damageTypes;
        this.damageTypeTags = damageTypeTags;
        this.hiddenDescription = hiddenDescription;
        this.clearDescription = clearDescription;
    }

    public List<ResourceKey<DamageType>> getDamageTypes() {
        return this.damageTypes;
    }

    public List<TagKey<DamageType>> getDamageTypeTags() {
        return this.damageTypeTags;
    }

    public Component getHiddenDescription() {
        return this.hiddenDescription;
    }

    public Component getClearDescription() {
        return this.clearDescription;
    }

    public static Builder of(Component hiddenDescription, Component clearDescription) {
        return new Builder(hiddenDescription, clearDescription);
    }

    @Override
    public DeferredType<?> getType() {
        return ArtifactConditionTypesAS.DAMAGE_TYPE;
    }

    @Override
    public boolean isFulfilled(ItemEntityArtifact artifact, RandomSource rand, DamageSource source, float amount) {
        for (ResourceKey<DamageType> type : this.getDamageTypes()) {
            if (source.is(type)) return true;
        }
        for (TagKey<DamageType> tag : this.getDamageTypeTags()) {
            if (source.is(tag)) return true;
        }
        return false;
    }

    @Override
    public List<Vector3> isFulfilled(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifactEntity) {
        return List.of();
    }

    @Override
    public void fulfillCondition(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifactEntity) {}

    @Override
    public Component getDisplayHint(RandomSource rand, boolean wasSuccessful) {
        return wasSuccessful ? this.getClearDescription() : this.getHiddenDescription();
    }

    public static class Builder {

        private final List<ResourceKey<DamageType>> damageTypes = new ArrayList<>();
        private final List<TagKey<DamageType>> damageTypeTags = new ArrayList<>();
        private final Component hiddenDescription;
        private final Component clearDescription;

        public Builder(Component hiddenDescription, Component clearDescription) {
            this.hiddenDescription = hiddenDescription;
            this.clearDescription = clearDescription;
        }

        public Builder type(ResourceKey<DamageType>... damageTypes) {
            this.damageTypes.addAll(Arrays.asList(damageTypes));
            return this;
        }

        public Builder tag(TagKey<DamageType>... damageTypeTags) {
            this.damageTypeTags.addAll(Arrays.asList(damageTypeTags));
            return this;
        }

        public Provider build() {
            if (this.damageTypes.isEmpty() && this.damageTypeTags.isEmpty()) {
                throw new IllegalArgumentException("At least one damage type or damage type tag must be specified!");
            }
            return id -> new ArtifactConditionDamageType(id, this.damageTypes, this.damageTypeTags, this.hiddenDescription, this.clearDescription);
        }
    }
}
