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
import hellfirepvp.astralsorcery.common.util.EntitySpawnUtil;
import hellfirepvp.astralsorcery.common.util.EntityUtil;
import hellfirepvp.astralsorcery.common.util.data.IntRange;
import hellfirepvp.astralsorcery.common.util.data.RandomWeightedList;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactEffectSpawnMobs
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactEffectSpawnMobs extends ArtifactEffect {

    public static final MapCodec<ArtifactEffectSpawnMobs> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ArtifactEffect::getId),
            Codec.INT.fieldOf("range").forGetter(ArtifactEffectSpawnMobs::getRange),
            IntRange.CODEC.fieldOf("spawn_count").forGetter(ArtifactEffectSpawnMobs::getSpawnCount),
            RandomWeightedList.codec(BuiltInRegistries.ENTITY_TYPE.byNameCodec()).fieldOf("spawnable_types").forGetter(ArtifactEffectSpawnMobs::getSpawnableTypes)
    ).apply(inst, ArtifactEffectSpawnMobs::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtifactEffectSpawnMobs> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ArtifactEffect::getId,
            ByteBufCodecs.INT,
            ArtifactEffectSpawnMobs::getRange,
            IntRange.STREAM_CODEC,
            ArtifactEffectSpawnMobs::getSpawnCount,
            RandomWeightedList.streamCodec(ByteBufCodecs.registry(Registries.ENTITY_TYPE)),
            ArtifactEffectSpawnMobs::getSpawnableTypes,
            ArtifactEffectSpawnMobs::new
    );

    private final int range;
    private final IntRange spawnCount;
    private final RandomWeightedList<EntityType<?>> spawnableTypes;

    protected ArtifactEffectSpawnMobs(ResourceLocation id, int range, IntRange spawnCount, RandomWeightedList<EntityType<?>> spawnableTypes) {
        super(id);
        this.range = range;
        this.spawnCount = spawnCount;
        this.spawnableTypes = spawnableTypes;
    }

    public int getRange() {
        return this.range;
    }

    public IntRange getSpawnCount() {
        return this.spawnCount;
    }

    public RandomWeightedList<EntityType<?>> getSpawnableTypes() {
        return this.spawnableTypes;
    }

    public static Provider of(int range, IntRange spawnCount, RandomWeightedList.Builder<EntityType<?>> spawnableTypes) {
        return id -> new ArtifactEffectSpawnMobs(id, range, spawnCount, spawnableTypes.build());
    }

    @Override
    public DeferredType<?> getType() {
        return ArtifactEffectTypesAS.SPAWN_MOBS;
    }

    @Override
    public void applyEffect(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifact) {
        if (this.getSpawnableTypes().isEmpty()) return;

        BlockPos offset = artifact.blockPosition();
        int range = this.getRange();
        int count = this.getSpawnCount().getRandom(rand);
        sLevel.levelEvent(2004, offset, 0);

        for (int i = 0; i < count; i++) {
            int attempts = 20;
            LivingEntity spawned = null;
            do {
                BlockPos at = offset.offset(
                        rand.nextInt(2 * range + 1) - range,
                        rand.nextInt(2 * range + 1) - range,
                        rand.nextInt(2 * range + 1) - range);
                EntityType<?> type = this.getSpawnableTypes().getRandomEntry(rand).orElse(null);
                if (type == null) continue;
                if (!EntityUtil.canEntityFit(sLevel, type, at)) continue;
                spawned = EntityUtil.spawnLivingEntity(sLevel, type, Vector3.atBottomCenter(at));
            } while (spawned == null && --attempts > 0);
            if (spawned != null) continue;

            EntityType<?> type = this.getSpawnableTypes().getRandomEntry(rand).orElse(null);
            if (type == null) continue;
            BlockPos at = offset.offset(
                    rand.nextInt(2 + 1) - 1,
                    rand.nextInt(2 + 1) - 1,
                    rand.nextInt(2 + 1) - 1);
            EntityUtil.spawnLivingEntity(sLevel, type, Vector3.atBottomCenter(at));
        }
    }
}
