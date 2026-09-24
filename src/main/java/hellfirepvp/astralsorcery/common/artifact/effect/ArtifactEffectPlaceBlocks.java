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
import hellfirepvp.astralsorcery.common.util.BlockUtil;
import hellfirepvp.astralsorcery.common.util.data.RandomWeightedList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactEffectPlaceBlocks
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ArtifactEffectPlaceBlocks extends ArtifactEffect {

    public static final MapCodec<ArtifactEffectPlaceBlocks> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ArtifactEffectPlaceBlocks::getId),
            Codec.INT.fieldOf("range").forGetter(ArtifactEffectPlaceBlocks::getRange),
            Codec.FLOAT.fieldOf("place_chance").forGetter(ArtifactEffectPlaceBlocks::getPlaceChance),
            RandomWeightedList.codec(BlockState.CODEC).fieldOf("palette").forGetter(ArtifactEffectPlaceBlocks::getPalette)
    ).apply(inst, ArtifactEffectPlaceBlocks::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArtifactEffectPlaceBlocks> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            ArtifactEffect::getId,
            ByteBufCodecs.INT,
            ArtifactEffectPlaceBlocks::getRange,
            ByteBufCodecs.FLOAT,
            ArtifactEffectPlaceBlocks::getPlaceChance,
            RandomWeightedList.streamCodec(ByteBufCodecs.fromCodecWithRegistriesTrusted(BlockState.CODEC)),
            ArtifactEffectPlaceBlocks::getPalette,
            ArtifactEffectPlaceBlocks::new);

    private final int range;
    private final float placeChance;
    private final RandomWeightedList<BlockState> palette;

    protected ArtifactEffectPlaceBlocks(ResourceLocation id, int range, float placeChance, RandomWeightedList<BlockState> palette) {
        super(id);
        this.range = range;
        this.placeChance = placeChance;
        this.palette = palette;
    }

    public int getRange() {
        return this.range;
    }

    public float getPlaceChance() {
        return this.placeChance;
    }

    public RandomWeightedList<BlockState> getPalette() {
        return this.palette;
    }

    public static Provider of(int range, float placeChance, RandomWeightedList.Builder<BlockState> palette) {
        return id -> new ArtifactEffectPlaceBlocks(id, range, placeChance, palette.build());
    }

    @Override
    public DeferredType<?> getType() {
        return ArtifactEffectTypesAS.PLACE_BLOCKS;
    }

    @Override
    public void applyEffect(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifact) {
        Vec3 artifactPos = artifact.position();
        float rangeSq = this.getRange() * this.getRange();
        Vec3i radiusVec = new Vec3i(this.getRange(), this.getRange(), this.getRange());
        BlockPos.betweenClosed(artifact.blockPosition().subtract(radiusVec), artifact.blockPosition().offset(radiusVec)).forEach(pos -> {
            if (!sLevel.isInWorldBounds(pos)) return;
            if (rand.nextFloat() > this.getPlaceChance()) return;
            if (pos.distToCenterSqr(artifactPos) > rangeSq) return;
            if (!sLevel.isEmptyBlock(pos)) return;
            if (!BlockUtil.isReplaceable(sLevel, pos)) return;
            this.getPalette().getRandomEntry(rand).ifPresent(state -> sLevel.setBlockAndUpdate(pos, state));
        });
    }
}
