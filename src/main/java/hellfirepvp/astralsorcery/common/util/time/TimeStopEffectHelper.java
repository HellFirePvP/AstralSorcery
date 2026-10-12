/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.time;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataManager;
import hellfirepvp.astralsorcery.common.lib.types.SyncDataTypesAS;
import hellfirepvp.astralsorcery.common.util.ChunkUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TimeStopEffectHelper
 * Created by HellFirePvP
 * Date: 10.10.2026 / 17:03
 */
@OnlyIn(Dist.CLIENT)
public class TimeStopEffectHelper {

    public static void playClientEffectTick() {
        if (Minecraft.getInstance().isPaused()) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        Level level = player.level();
        RandomSource rand = RandomSource.create();

        SyncDataManager.getInstance().getClientData(SyncDataTypesAS.TIME_STOP_ZONES).getZones(level)
                .forEach(zone -> playZoneEffects(rand, level, zone));
    }

    public static void playZoneEffects(RandomSource rand, Level level, TimeStopZone.Snapshot zone) {
        Vector3 zonePos = Vector3.atCenter(zone.offset());

        zone.capturedBlockEntities().forEach(pos -> {
            ChunkUtil.executeWithChunk(level, pos, () -> {
                BlockEntity be = level.getBlockEntity(pos);
                if (be != null) {
                    AABB box = be.getBlockState().getShape(level, pos).bounds().move(pos).inflate(0.1F);
                    playParticle(Vector3.randomInAABB(box, rand), rand);
                }
            });
        });

        for (int i = 0; i < 10; i++) {
            Vector3 pos = Vector3.random(rand).normalize().multiply(rand.nextFloat() * zone.range()).add(zonePos);
            playParticle(pos, rand);
        }

        if (rand.nextInt(4) == 0) {
            Vector3 from = Vector3.random(rand).normalize().multiply(rand.nextFloat() * zone.range()).add(zonePos);
            Vector3 to   = Vector3.random(rand).normalize().multiply(rand.nextFloat() * zone.range()).add(zonePos);

            if (from.distance(to) >= 10) {
                Vector3 dir = from.vectorFromHereTo(to);
                to = from.copy().add(dir.normalize().multiply(10));
            }

            EffectHelper.of(EffectTemplatesAS.LIGHTNING)
                    .spawn(from)
                    .makeDefault(to);
        }
    }

    public static void playEntityTickParticles(Entity entity, RandomSource rand) {
        Vector3 pos = Vector3.randomInAABB(entity.getBoundingBox(), rand);
        playParticle(pos, rand);
    }

    private static void playParticle(Vector3 pos, RandomSource rand) {
        EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(pos)
                .alpha(FXAlphaFunction.FADE_OUT)
                .setScale(0.2F + rand.nextFloat() * 0.2F)
                .setMaxAge(30 + rand.nextInt(20));
    }
}
