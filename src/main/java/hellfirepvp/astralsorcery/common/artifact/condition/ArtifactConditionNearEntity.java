/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.artifact.ArtifactCondition;
import hellfirepvp.astralsorcery.common.entity.item.ItemEntityArtifact;
import hellfirepvp.astralsorcery.common.lib.types.ArtifactConditionTypesAS;
import hellfirepvp.astralsorcery.common.util.data.DescribedEntityPredicate;
import hellfirepvp.astralsorcery.common.util.data.IntRange;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactConditionNearEntity
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactConditionNearEntity extends ArtifactCondition {

    public static final MapCodec<ArtifactConditionNearEntity> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ArtifactCondition::getId),
            DescribedEntityPredicate.CODEC.fieldOf("entityPredicate").forGetter(ArtifactConditionNearEntity::getEntityPredicate),
            IntRange.CODEC.fieldOf("entityCountNeeded").forGetter(ArtifactConditionNearEntity::getEntityCountNeeded),
            Codec.INT.fieldOf("range").forGetter(ArtifactConditionNearEntity::getRange),
            Codec.FLOAT.fieldOf("entityConsumptionChance").forGetter(ArtifactConditionNearEntity::getEntityConsumptionChance)
    ).apply(inst, ArtifactConditionNearEntity::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtifactConditionNearEntity> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ArtifactCondition::getId,
            DescribedEntityPredicate.STREAM_CODEC,
            ArtifactConditionNearEntity::getEntityPredicate,
            IntRange.STREAM_CODEC,
            ArtifactConditionNearEntity::getEntityCountNeeded,
            ByteBufCodecs.INT,
            ArtifactConditionNearEntity::getRange,
            ByteBufCodecs.FLOAT,
            ArtifactConditionNearEntity::getEntityConsumptionChance,
            ArtifactConditionNearEntity::new);

    private final DescribedEntityPredicate entityPredicate;
    private final IntRange entityCountNeeded;
    private final int range;
    private final float entityConsumptionChance;

    public ArtifactConditionNearEntity(ResourceLocation id, DescribedEntityPredicate entityPredicate, IntRange entityCountNeeded, int range, float entityConsumptionChance) {
        super(id);
        this.entityPredicate = entityPredicate;
        this.entityCountNeeded = entityCountNeeded;
        this.range = range;
        this.entityConsumptionChance = entityConsumptionChance;
    }

    public DescribedEntityPredicate getEntityPredicate() {
        return this.entityPredicate;
    }

    public IntRange getEntityCountNeeded() {
        return this.entityCountNeeded;
    }

    public int getRange() {
        return this.range;
    }

    public float getEntityConsumptionChance() {
        return this.entityConsumptionChance;
    }

    public static Builder of(DescribedEntityPredicate predicate) {
        return new Builder(predicate);
    }

    @Override
    public DeferredType<?> getType() {
        return ArtifactConditionTypesAS.NEAR_ENTITY;
    }

    @Override
    public List<Vector3> isFulfilled(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifactEntity) {
        int countNeeded = this.getEntityCountNeeded().getRandom(rand);
        List<Entity> validEntities = this.findValidEntities(sLevel, artifactEntity);
        if (validEntities.size() < countNeeded) return List.of();
        return validEntities.stream().limit(countNeeded).map(Vector3::new).toList();
    }

    @Override
    public void fulfillCondition(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifactEntity) {
        if (this.getEntityConsumptionChance() <= 0F) return;

        int requiredEntities = this.getEntityCountNeeded().getRandom(rand);
        List<Entity> validEntities = new ArrayList<>(this.findValidEntities(sLevel, artifactEntity));
        for (int i = 0; i < requiredEntities; i++) {
            int index = rand.nextInt(validEntities.size());
            Entity toConsume = validEntities.get(index);
            validEntities.remove(index);

            if (rand.nextFloat() <= this.getEntityConsumptionChance()) {
                toConsume.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    protected List<Entity> findValidEntities(ServerLevel sLevel, ItemEntityArtifact artifact) {
        AABB bounds = artifact.getBoundingBox().inflate(this.getRange());
        return sLevel.getEntities(this.getEntityPredicate(), bounds, Entity::isAlive);
    }

    @Override
    public Component getDisplayHint(RandomSource rand, boolean wasSuccessful) {
        return Component.translatable("artifact.%s.condition.value.near_entity.%s.%s".formatted(
                this.getId().getNamespace(), this.getId().getPath(), wasSuccessful ? "clear" : "hidden"));
    }

    public static class Builder {

        private final DescribedEntityPredicate predicate;
        private IntRange entityCountNeeded = IntRange.of(1);
        private int range = 5;
        private float entityConsumptionChance = 0F;

        public Builder(DescribedEntityPredicate predicate) {
            this.predicate = predicate;
        }

        public Builder setEntityCountNeeded(IntRange entityCountNeeded) {
            this.entityCountNeeded = entityCountNeeded;
            return this;
        }

        public Builder setRange(int range) {
            this.range = range;
            return this;
        }

        public Builder setEntityConsumptionChance(float entityConsumptionChance) {
            this.entityConsumptionChance = entityConsumptionChance;
            return this;
        }

        public Provider build() {
            return id -> new ArtifactConditionNearEntity(id, this.predicate, this.entityCountNeeded, this.range, this.entityConsumptionChance);
        }
    }
}
