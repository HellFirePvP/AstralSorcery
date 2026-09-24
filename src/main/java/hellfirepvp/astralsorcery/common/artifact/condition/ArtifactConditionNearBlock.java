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
import hellfirepvp.astralsorcery.common.util.data.IntRange;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactConditionNearBlock
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactConditionNearBlock extends ArtifactCondition {

    public static final MapCodec<ArtifactConditionNearBlock> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ArtifactCondition::getId),
            BlockPredicate.CODEC.fieldOf("blockPredicate").forGetter(ArtifactConditionNearBlock::getBlockPredicate),
            IntRange.CODEC.fieldOf("blockCountNeeded").forGetter(ArtifactConditionNearBlock::getBlockCountNeeded),
            Codec.INT.fieldOf("range").forGetter(ArtifactConditionNearBlock::getRange),
            Codec.FLOAT.fieldOf("blockConsumptionChance").forGetter(ArtifactConditionNearBlock::getBlockConsumptionChance)
    ).apply(inst, ArtifactConditionNearBlock::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtifactConditionNearBlock> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ArtifactCondition::getId,
            BlockPredicate.STREAM_CODEC,
            ArtifactConditionNearBlock::getBlockPredicate,
            IntRange.STREAM_CODEC,
            ArtifactConditionNearBlock::getBlockCountNeeded,
            ByteBufCodecs.INT,
            ArtifactConditionNearBlock::getRange,
            ByteBufCodecs.FLOAT,
            ArtifactConditionNearBlock::getBlockConsumptionChance,
            ArtifactConditionNearBlock::new);

    private final BlockPredicate blockPredicate;
    private final IntRange blockCountNeeded;
    private final int range;
    private final float blockConsumptionChance;

    protected ArtifactConditionNearBlock(ResourceLocation id, BlockPredicate blockPredicate, IntRange blockCountNeeded, int range, float blockConsumptionChance) {
        super(id);
        this.blockPredicate = blockPredicate;
        this.blockCountNeeded = blockCountNeeded;
        this.range = range;
        this.blockConsumptionChance = blockConsumptionChance;
    }

    public BlockPredicate getBlockPredicate() {
        return this.blockPredicate;
    }

    public IntRange getBlockCountNeeded() {
        return this.blockCountNeeded;
    }

    public int getRange() {
        return this.range;
    }

    public float getBlockConsumptionChance() {
        return this.blockConsumptionChance;
    }

    public static Builder of(BlockPredicate predicate) {
        return new Builder(predicate);
    }

    @Override
    public DeferredType<?> getType() {
        return ArtifactConditionTypesAS.NEAR_BLOCK;
    }

    @Override
    public List<Vector3> isFulfilled(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifactEntity) {
        int requiredBlocks = this.getBlockCountNeeded().getRandom(rand);
        List<BlockPos> posList = this.findValidBlocks(sLevel, artifactEntity.blockPosition());
        if (posList.size() < requiredBlocks) return List.of();
        return posList.stream().limit(requiredBlocks).map(Vector3::atCenter).toList();
    }

    @Override
    public void fulfillCondition(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifactEntity) {
        if (this.getBlockConsumptionChance() <= 0) return;

        int requiredBlocks = this.getBlockCountNeeded().getRandom(rand);
        List<BlockPos> validBlocks = new ArrayList<>(this.findValidBlocks(sLevel, artifactEntity.blockPosition()));
        for (int i = 0; i < requiredBlocks && !validBlocks.isEmpty(); i++) {
            int index = rand.nextInt(validBlocks.size());
            BlockPos toConsume = validBlocks.get(index);
            validBlocks.remove(index);

            if (rand.nextFloat() <= this.getBlockConsumptionChance()) {
                sLevel.destroyBlock(toConsume, false);
            }
        }
    }

    protected List<BlockPos> findValidBlocks(ServerLevel sLevel, BlockPos centerPos) {
        Vec3i offsetVec = new Vec3i(this.getRange(), this.getRange(), this.getRange());
        return BlockPos.betweenClosedStream(centerPos.subtract(offsetVec), centerPos.offset(offsetVec))
                .filter(pos -> this.getBlockPredicate().matches(sLevel, pos))
                .map(BlockPos::immutable)
                .toList();
    }

    @Override
    public Component getDisplayHint(RandomSource rand, boolean wasSuccessful) {
        return Component.translatable("artifact.%s.condition.value.near_block.%s.%s".formatted(
                this.getId().getNamespace(), this.getId().getPath(), wasSuccessful ? "clear" : "hidden"));
    }

    public static class Builder {

        private final BlockPredicate predicate;
        private IntRange blockCountNeeded = IntRange.of(1);
        private int range = 5;
        private float blockConsumptionChance = 0F;

        public Builder(BlockPredicate predicate) {
            this.predicate = predicate;
        }

        public Builder setBlockCountNeeded(IntRange blockCountNeeded) {
            this.blockCountNeeded = blockCountNeeded;
            return this;
        }

        public Builder setRange(int range) {
            this.range = range;
            return this;
        }

        public Builder setBlockConsumptionChance(float blockConsumptionChance) {
            this.blockConsumptionChance = blockConsumptionChance;
            return this;
        }

        public Provider build() {
            return id -> new ArtifactConditionNearBlock(id, this.predicate, this.blockCountNeeded, this.range, this.blockConsumptionChance);
        }
    }
}
