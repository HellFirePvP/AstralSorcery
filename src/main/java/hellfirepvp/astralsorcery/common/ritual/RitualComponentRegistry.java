package hellfirepvp.astralsorcery.common.ritual;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RitualComponentRegistry
 * Created by HellFirePvP
 * Date: 09.08.2016 / 15:34
 */
@Deprecated
public class RitualComponentRegistry {

    private static Map<Block, List<Integer>> registeredComponents = new HashMap<>();

    @Deprecated
    public static boolean isComponent(@Nonnull IBlockState state) {
        Block b = state.getBlock();
        List<Integer> acceptedMetas = registeredComponents.get(b);
        if(acceptedMetas == null) return false;
        if(acceptedMetas.size() == 1 && acceptedMetas.get(0) == -1) return true; //Wildcard
        return acceptedMetas.contains(b.getMetaFromState(state));
    }

    @Deprecated
    public static void registerBlockAsComponent(Block b, @Nullable int... metas) {
        if(b == null || b.equals(Blocks.AIR)) return; //No.

        List<Integer> out = new LinkedList<>();
        if(metas == null || metas.length == 0) {
            out.add(-1);
        } else {
            for (int meta : metas) {
                out.add(meta);
            }
        }
        registeredComponents.put(b, out);
    }

    @Deprecated
    public static void setupRegistry() {

    }

}
