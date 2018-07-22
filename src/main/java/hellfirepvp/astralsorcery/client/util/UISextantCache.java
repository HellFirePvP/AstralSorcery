/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.common.item.tool.sextant.SextantFinder;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktRequestSextantTarget;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: UISextantCache
 * Created by HellFirePvP
 * Date: 08.06.2018 / 17:02
 */
@SideOnly(Side.CLIENT)
public class UISextantCache {

    public static final UISextantCache INSTANCE = new UISextantCache();

    private static final long WAIT_TIME_MS = 7500;
    private static Map<SextantFinder.TargetObject, Long> wait = new HashMap<>();
    private static Map<Tuple<ChunkPos, Integer>, List<CachedSextantResult>> sextantCache = new HashMap<>();

    private UISextantCache() {}

    public static void addTarget(SextantFinder.TargetObject to, BlockPos pos, int dim) {
        if (Minecraft.getMinecraft().player == null) return;
        BlockPos at = Minecraft.getMinecraft().player.getPosition();
        ChunkPos chAt = new ChunkPos(at);

        Tuple<ChunkPos, Integer> key = new Tuple<>(chAt, dim);
        List<CachedSextantResult> cache = sextantCache.computeIfAbsent(key, (t) -> new LinkedList<>());
        cache.removeIf((c) -> c.target.equals(to));
        cache.add(new CachedSextantResult(to, pos));
    }

    @Nullable
    public static BlockPos queryLocation(BlockPos position, int dim, SextantFinder.TargetObject to) {
        Tuple<ChunkPos, Integer> key = new Tuple<>(new ChunkPos(position), dim);
        List<CachedSextantResult> cache = sextantCache.computeIfAbsent(key, (t) -> new LinkedList<>());
        CachedSextantResult result;
        if ((result = MiscUtils.iterativeSearch(cache, (c) -> c.target.equals(to))) != null) {
            return result.destination;
        }
        Long ms = wait.computeIfAbsent(to, (t) -> 0L);
        if (System.currentTimeMillis() - ms >= WAIT_TIME_MS) {
            wait.put(to, System.currentTimeMillis());
            PacketChannel.CHANNEL.sendToServer(new PktRequestSextantTarget(to));
        }
        return null;
    }

    public void clearClient() {
        sextantCache.clear();
    }

    static class CachedSextantResult {

        private SextantFinder.TargetObject target;
        private BlockPos destination;

        CachedSextantResult(SextantFinder.TargetObject target, BlockPos destination) {
            this.target = target;
            this.destination = destination;
        }

    }

}
