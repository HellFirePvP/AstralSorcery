/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.visual;

import hellfirepvp.astralsorcery.common.visual.type.*;
import hellfirepvp.astralsorcery.common.network.play.PktPlayVisualEffect;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VisualEffectTypes
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class VisualEffectTypes {

    public static final StreamCodec<ByteBuf, Effect> STREAM_CODEC = ResourceLocation.STREAM_CODEC
            .dispatch(effect -> effect.getType().key(), key -> MiscUtil.cast(getEffectType(key).streamCodec()));

    private static final Map<ResourceLocation, EffectType<?>> byName = new HashMap<>();

    public static void init() {
        register(RockCrystalSparkle.TYPE);
        register(FocalPointTransmutationSparkle.TYPE);
        register(FocalPointCombineSparkle.TYPE);
        register(LumenTransferEffect.TYPE);
        register(LightningEffect.TYPE);
        register(BlockHarvestDraw.TYPE);
        register(BlockBreakEffect.TYPE);
        register(ShootingStarExplosion.TYPE);
        register(ArtifactConditionTriggerEffect.TYPE);
        register(SinglePlantGrowthEffect.TYPE);
        register(ChaliceLiquidInteractionEffect.TYPE);
        register(OreFinderEffect.TYPE);
        register(CelestialStrikeEffect.TYPE);
        register(CelestialStrikeBeamEffect.TYPE);
        register(SwordCrescentWaveEffect.TYPE);
        register(SwordShockwaveEffect.TYPE);
    }

    public static <T extends EffectType<E>, E extends Effect> T register(T effectType) {
        byName.put(effectType.key(), effectType);
        return effectType;
    }

    public static EffectType<?> getEffectType(ResourceLocation name) {
        return byName.get(name);
    }

    public interface Effect {

        default void sendEffect(ServerPlayer sPlayer) {
            PacketDistributor.sendToPlayer(sPlayer, PktPlayVisualEffect.playEffect(this));
        }

        default void sendToNearby(ServerLevel sLevel, Vec3i pos) {
            PacketDistributor.sendToPlayersNear(sLevel, null, pos.getX(), pos.getY(), pos.getZ(), 64,
                    PktPlayVisualEffect.playEffect(this));
        }

        @OnlyIn(Dist.CLIENT)
        void playEffect(RandomSource rand);

        EffectType<?> getType();

    }

    public record EffectType<T extends Effect>(ResourceLocation key, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {}
}
