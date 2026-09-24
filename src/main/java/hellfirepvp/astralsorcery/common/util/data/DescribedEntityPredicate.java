/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.entity.EntityTypeTest;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DescribedEntityPredicate
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record DescribedEntityPredicate(List<ResourceKey<EntityType<?>>> entityTypes,
                                       List<TagKey<EntityType<?>>> entityTags) implements EntityTypeTest<Entity, Entity> {

    public static final Codec<DescribedEntityPredicate> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceKey.codec(Registries.ENTITY_TYPE).listOf().fieldOf("damageTypes").forGetter(DescribedEntityPredicate::entityTypes),
            TagKey.codec(Registries.ENTITY_TYPE).listOf().fieldOf("damageTypeTags").forGetter(DescribedEntityPredicate::entityTags)
    ).apply(inst, DescribedEntityPredicate::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, DescribedEntityPredicate> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.ENTITY_TYPE).apply(ByteBufCodecs.list()),
            DescribedEntityPredicate::entityTypes,
            CodecUtil.tagKeyStreamCodec(Registries.ENTITY_TYPE).apply(ByteBufCodecs.list()),
            DescribedEntityPredicate::entityTags,
            DescribedEntityPredicate::new);

    public static Builder entityFilter() {
        return new Builder();
    }

    @Nullable
    @Override
    public Entity tryCast(Entity entity) {
        return BuiltInRegistries.ENTITY_TYPE.getResourceKey(entity.getType()).map(type -> {
            if (this.entityTypes.contains(type)) return entity;
            for (TagKey<EntityType<?>> typeTag : this.entityTags) {
                if (entity.getType().is(typeTag)) {
                    return entity;
                }
            }
            return null;
        }).orElse(null);
    }

    @Override
    public Class<? extends Entity> getBaseClass() {
        return Entity.class;
    }

    public static class Builder {

        private final List<ResourceKey<EntityType<?>>> entityTypes = new ArrayList<>();
        private final List<TagKey<EntityType<?>>> entityTags = new ArrayList<>();

        public Builder type(ResourceKey<EntityType<?>>... entityTypes) {
            this.entityTypes.addAll(List.of(entityTypes));
            return this;
        }

        public Builder tag(TagKey<EntityType<?>>... entityTags) {
            this.entityTags.addAll(List.of(entityTags));
            return this;
        }

        public DescribedEntityPredicate build() {
            return new DescribedEntityPredicate(List.copyOf(this.entityTypes), List.copyOf(this.entityTags));
        }
    }
}
