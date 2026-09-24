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
import hellfirepvp.astralsorcery.common.util.data.FloatRange;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactEffectRandomMove
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactEffectRandomMove extends ArtifactEffect {

    public static final MapCodec<ArtifactEffectRandomMove> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ArtifactEffect::getId),
            FloatRange.CODEC.fieldOf("distance").forGetter(ArtifactEffectRandomMove::getDistance),
            Codec.FLOAT.fieldOf("move_speed").forGetter(ArtifactEffectRandomMove::getMoveSpeed)
    ).apply(inst, ArtifactEffectRandomMove::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtifactEffectRandomMove> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ArtifactEffect::getId,
            FloatRange.STREAM_CODEC,
            ArtifactEffectRandomMove::getDistance,
            ByteBufCodecs.FLOAT,
            ArtifactEffectRandomMove::getMoveSpeed,
            ArtifactEffectRandomMove::new);

    private final FloatRange distance;
    private final float moveSpeed;

    protected ArtifactEffectRandomMove(ResourceLocation id, FloatRange distance, float moveSpeed) {
        super(id);
        this.distance = distance;
        this.moveSpeed = moveSpeed;
    }

    public FloatRange getDistance() {
        return this.distance;
    }

    public float getMoveSpeed() {
        return this.moveSpeed;
    }

    public static Provider of(FloatRange distance, float moveSpeed) {
        return id -> new ArtifactEffectRandomMove(id, distance, moveSpeed);
    }

    @Override
    public DeferredType<?> getType() {
        return ArtifactEffectTypesAS.RANDOM_MOVE;
    }

    @Override
    public void applyEffect(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifact) {
        int attempts = 50;
        BlockPos target = null;
        do {
            float dist = this.getDistance().getRandom(rand);
            Vector3 dir = Vector3.random(rand).normalize().multiply(dist);
            BlockPos candidate = artifact.blockPosition().offset(dir.toBlockPos());
            if (sLevel.isInWorldBounds(candidate) && sLevel.isEmptyBlock(candidate) && sLevel.isEmptyBlock(candidate.above()) &&
                    sLevel.isInWorldBounds(candidate.above(2)) &&
                    sLevel.isInWorldBounds(candidate.below(2))) {
                target = candidate;
            }
        } while (target == null && attempts-- > 0);
        if (target == null) return;

        artifact.setForcedMovePos(target, this.getMoveSpeed());
    }
}
