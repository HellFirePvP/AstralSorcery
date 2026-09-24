/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.visual.type;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.sound.PlayableSoundInstance;
import hellfirepvp.astralsorcery.common.lib.SoundsAS;
import hellfirepvp.astralsorcery.common.visual.VisualEffectTypes;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.transfer.LumenNode;
import hellfirepvp.astralsorcery.common.lumen.transfer.LumenRequestChain;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenTransferEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenTransferEffect implements VisualEffectTypes.Effect {

    public static final StreamCodec<RegistryFriendlyByteBuf, LumenTransferEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(RegistriesAS.KEY_LUMEN),
            LumenTransferEffect::getLumen,
            Vector3.STREAM_CODEC.apply(ByteBufCodecs.list()),
            LumenTransferEffect::getPath,
            LumenTransferEffect::new);
    public static final VisualEffectTypes.EffectType<LumenTransferEffect> TYPE =
            new VisualEffectTypes.EffectType<>(AstralSorcery.key("lumen_transfer_effect"), STREAM_CODEC);

    private final Lumen lumen;
    private final List<Vector3> path;

    private LumenTransferEffect(Lumen lumen, List<Vector3> path) {
        this.lumen = lumen;
        this.path = path;
    }

    public static LumenTransferEffect withChain(Lumen lumen, LumenRequestChain chain) {
        if (!chain.isValid()) {
            return new LumenTransferEffect(lumen, List.of());
        }
        List<Vector3> positions = new ArrayList<>();
        positions.add(Vector3.atCenter(chain.getStart()));
        for (LumenNode node : chain.getNodeChain()) {
            positions.add(new Vector3(node.getLineOfSightPos()));
        }
        return new LumenTransferEffect(lumen, Lists.reverse(positions));
    }

    private Lumen getLumen() {
        return this.lumen;
    }

    private List<Vector3> getPath() {
        return this.path;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void playEffect(RandomSource rand) {
        if (this.path.size() < 2) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        Level level = player.level();

        Vector3 closestToPlayer = findClosestPoint(this.path, player);
        PlayableSoundInstance.of(SoundsAS.LUMEN_TRANSFER)
                .pos(closestToPlayer)
                .random(rand)
                .volume(0.35F)
                .pitch(0.9F + rand.nextFloat() * 0.1F)
                .play();

        List<Vector3> pointPath = new ArrayList<>();
        for (int i = 0; i < this.path.size() - 1; i++) {
            Vector3 from = this.path.get(i);
            Vector3 to = this.path.get(i + 1);

            pointPath.addAll(VectorUtil.iteratePoints(from, to, 0.35F));
        }

        int targetPointsPerTick = 30;
        int maxTickTime = 4;
        int pointsPerTick = Math.max(targetPointsPerTick, pointPath.size() / maxTickTime);

        for (int i = 0; i < pointPath.size(); i++) {
            Vector3 pos = pointPath.get(i);
            int tickDelay = i / pointsPerTick;

            ClientProxy.scheduleEffectTask(tickDelay + 1, () -> {
                for (int j = 0; j < 2; j++) {
                    Vector3 effectPos = VectorUtil.withRandomOffset(pos.copy(), rand, 0.1F);
                    Vector3 dir = Vector3.random(rand).normalize().multiply(0.005F + rand.nextFloat() * 0.005F);

                    EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                            .spawn(effectPos)
                            .alpha(FXAlphaFunction.FADE_OUT)
                            .color(FXColorFunction.constant(this.lumen.getColor(level, effectPos)))
                            .setScale(0.4F + rand.nextFloat() * 0.2F)
                            .setMotion(dir)
                            .setGravity(Vector3.y(0.0005F))
                            .setMaxAge(20 + rand.nextInt(20));
                    EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                            .spawn(effectPos)
                            .alpha(FXAlphaFunction.FADE_OUT)
                            .setScale(0.2F + rand.nextFloat() * 0.1F)
                            .setMotion(dir)
                            .setGravity(Vector3.y(0.0005F))
                            .setMaxAge(20 + rand.nextInt(8));

                    if (rand.nextInt(4) == 0) {
                        EffectHelper.of(EffectTemplatesAS.LUMEN_PARTICLE)
                                .spawn(VectorUtil.withRandomOffset(pos.copy(), rand, 0.3F))
                                .setSprite(TexturesAS.ATLAS_LUMEN, RegistriesAS.REGISTRY_LUMEN.getKey(this.lumen))
                                .alpha(FXAlphaFunction.FADE_OUT)
                                .setAlpha(0.7F)
                                .color(FXColorFunction.constant(this.lumen.getColor(level, effectPos)))
                                .setGravity(Vector3.y(0.0007F))
                                .setMaxAge(30 + rand.nextInt(8));
                    }
                }
            });
        }
    }

    private static Vector3 findClosestPoint(List<Vector3> points, Player player) {
        Vector3 playerPos = new Vector3(player);
        Vector3 closest = playerPos.copy();
        double closestDistSq = Double.MAX_VALUE;
        for (int i = 0; i < points.size() - 1; i++) {
            Vector3 point = closestBetween(points.get(i), points.get(i + 1), playerPos);
            double distSq = point.distanceSquared(playerPos);
            if (distSq < closestDistSq) {
                closest = point;
                closestDistSq = distSq;
            }
        }
        return closest;
    }

    private static Vector3 closestBetween(Vector3 segStart, Vector3 segEnd, Vector3 player) {
        Vector3 segDir = segEnd.copy().subtract(segStart);
        double segLengthSq = segDir.dot(segDir);
        if (segLengthSq <= 1.0E-9) {
            return segStart.copy();
        }
        double t = player.copy().subtract(segStart).dot(segDir) / segLengthSq;
        t = Mth.clamp(t, 0.0, 1.0);
        return segStart.copy().add(segDir.multiply(t));
    }

    @Override
    public VisualEffectTypes.EffectType<?> getType() {
        return TYPE;
    }

    @Override
    public void sendToNearby(ServerLevel sLevel, Vec3i pos) {
        sLevel.players().stream().filter(player -> {
            return this.path.stream().anyMatch(v -> v.distanceSquared(player) < 4096);
        }).forEach(this::sendEffect);
    }
}
