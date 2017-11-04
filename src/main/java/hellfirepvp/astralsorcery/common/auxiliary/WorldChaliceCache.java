/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldChaliceCache
 * Created by HellFirePvP
 * Date: 29.10.2017 / 00:07
 */
public class WorldChaliceCache {

    private static Map<Integer, List<TileChalice>> chaliceCache = new HashMap<>();

    private WorldChaliceCache() {}

    public static void wipeCache() {
        chaliceCache.clear();
    }

    public static void register(TileChalice chalice) {
        chaliceCache.computeIfAbsent(chalice.getWorld().provider.getDimension(), i -> Lists.newArrayList()).add(chalice);
    }

    public static void remove(TileChalice chalice) {
        List<TileChalice> ch = chaliceCache.get(chalice.getWorld().provider.getDimension());
        if(ch != null) {
            ch.remove(chalice);
        }
    }

    @Nonnull
    public static List<TileChalice> getChalices(World world) {
        List<TileChalice> ch = chaliceCache.get(world.provider.getDimension());
        return ch == null ? Lists.newArrayList() : ch;
    }

}
