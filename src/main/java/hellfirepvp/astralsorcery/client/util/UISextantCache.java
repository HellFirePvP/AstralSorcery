/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.common.item.tool.sextant.SextantFinder;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    private static Map<Tuple<BlockPos, Integer>, List<CachedSextantResult>> sextantCache = new HashMap<>();

    private UISextantCache() {}

    public static void addTarget(SextantFinder.TargetObject to, BlockPos pos, int dim) {
        if (Minecraft.getMinecraft().player == null) return;
        BlockPos at = Minecraft.getMinecraft().player.getPosition();

        Tuple<BlockPos, Integer> key = new Tuple<>(at, dim);
        List<CachedSextantResult> cache = sextantCache.computeIfAbsent(key, (t) -> new LinkedList<>());
        if (!MiscUtils.contains(cache, (c) -> c.destination.equals(pos) && c.target.equals(to))) {
            cache.add(new CachedSextantResult(to, pos));
        }
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
