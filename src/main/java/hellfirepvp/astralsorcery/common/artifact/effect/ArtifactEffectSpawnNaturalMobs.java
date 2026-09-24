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
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.IntRange;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactEffectSpawnNaturalMobs
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactEffectSpawnNaturalMobs extends ArtifactEffect {

    public static final MapCodec<ArtifactEffectSpawnNaturalMobs> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ArtifactEffect::getId),
            Codec.INT.fieldOf("range").forGetter(ArtifactEffectSpawnNaturalMobs::getRange),
            IntRange.CODEC.fieldOf("spawn_count").forGetter(ArtifactEffectSpawnNaturalMobs::getSpawnCount),
            StringRepresentable.fromEnum(MobCategory::values).fieldOf("category").forGetter(ArtifactEffectSpawnNaturalMobs::getCategory)
    ).apply(inst, ArtifactEffectSpawnNaturalMobs::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtifactEffectSpawnNaturalMobs> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ArtifactEffect::getId,
            ByteBufCodecs.INT,
            ArtifactEffectSpawnNaturalMobs::getRange,
            IntRange.STREAM_CODEC,
            ArtifactEffectSpawnNaturalMobs::getSpawnCount,
            CodecUtil.enumStreamCodec(MobCategory.class),
            ArtifactEffectSpawnNaturalMobs::getCategory,
            ArtifactEffectSpawnNaturalMobs::new);

    private final int range;
    private final IntRange spawnCount;
    private final MobCategory category;

    protected ArtifactEffectSpawnNaturalMobs(ResourceLocation id, int range, IntRange spawnCount, MobCategory category) {
        super(id);
        this.range = range;
        this.spawnCount = spawnCount;
        this.category = category;
    }

    public int getRange() {
        return this.range;
    }

    public IntRange getSpawnCount() {
        return this.spawnCount;
    }

    public MobCategory getCategory() {
        return this.category;
    }

    public static Provider of(int range, IntRange spawnCount, MobCategory category) {
        return id -> new ArtifactEffectSpawnNaturalMobs(id, range, spawnCount, category);
    }

    @Override
    public DeferredType<?> getType() {
        return ArtifactEffectTypesAS.SPAWN_NATURAL_MOBS;
    }

    @Override
    public void applyEffect(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifact) {
        BlockPos offset = artifact.blockPosition();
        int range = this.getRange();
        int count = this.getSpawnCount().getRandom(rand);
        sLevel.levelEvent(2004, offset, 0);

        for (int i = 0; i < count; i++) {
            int attempts = 10;
            LivingEntity spawned;
            do {
                BlockPos at = offset.offset(
                        rand.nextInt(2 * range + 1) - range,
                        rand.nextInt(2 * range + 1) - range,
                        rand.nextInt(2 * range + 1) - range);
                spawned = EntitySpawnUtil.performNaturalSpawnAt(sLevel, at, true, this.getCategory(),
                        EntitySpawnUtil.SpawnConditionFlags.C_IGNORE_SPAWN_RULES | EntitySpawnUtil.SpawnConditionFlags.IGNORE_ENTITY_SPAWN_COLLISION);
            } while (spawned == null && --attempts > 0);
            if (spawned != null) continue;

            attempts = 10;
            do {
                BlockPos at = offset.offset(
                        rand.nextInt(2 * range + 1) - range,
                        rand.nextInt(2 * range + 1) - range,
                        rand.nextInt(2 * range + 1) - range);
                spawned = EntitySpawnUtil.performNaturalSpawnAt(sLevel, at, true, this.getCategory(),
                        EntitySpawnUtil.SpawnConditionFlags.C_IGNORE_SPAWN_RULES | EntitySpawnUtil.SpawnConditionFlags.C_IGNORE_COLLISIONS);
            } while (spawned == null && --attempts > 0);
        }
    }
}
