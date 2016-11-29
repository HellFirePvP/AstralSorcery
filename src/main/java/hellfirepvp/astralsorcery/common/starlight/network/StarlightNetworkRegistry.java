package hellfirepvp.astralsorcery.common.starlight.network;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.network.IBlockStarlightRecipient;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.starlight.network.handlers.StarmetalFormHandler;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarlightNetworkRegistry
 * Created by HellFirePvP
 * Date: 04.08.2016 / 22:25
 */
public class StarlightNetworkRegistry {

    private static Map<Block, Map<Integer, IStarlightBlockHandler>> validEndpoints = new HashMap<>();

    @Nullable
    public static IStarlightBlockHandler getStarlightHandler(IBlockState state) {
        Block b = state.getBlock();
        if(b instanceof IBlockStarlightRecipient) return null;
        if(!validEndpoints.containsKey(b)) return null;
        Map<Integer, IStarlightBlockHandler> handlerMap = validEndpoints.get(b);
        if(handlerMap == null || handlerMap.isEmpty()) return null;

        if(handlerMap.containsKey(-1)) {
            return handlerMap.get(-1);
        }
        return handlerMap.get(b.getMetaFromState(state));
    }

    public static void registerEndpoint(IBlockState state, IStarlightBlockHandler handler) {
        registerEndpoint(state.getBlock(), state.getBlock().getMetaFromState(state), handler);
    }

    public static void registerEndpoint(Block block, IStarlightBlockHandler handler) {
        registerEndpoint(block, -1, handler);
    }

    public static void registerEndpoint(Block block, int meta, IStarlightBlockHandler handler) {
        if(!validEndpoints.containsKey(block)) {
            validEndpoints.put(block, new HashMap<>());
        }
        Map<Integer, IStarlightBlockHandler> handlerMap = validEndpoints.get(block);
        if(handlerMap.containsKey(-1)) {
            AstralSorcery.log.warn("[AstralSorcery] Tried to register Special StarlightBlockHandler for wildcard registered block.");
            AstralSorcery.log.warn("[AstralSorcery] Won't clear handlerMap, ignoring wildcard handler instead....");
        } else {
            if(!handlerMap.isEmpty()) {
                if(meta == -1) {
                    AstralSorcery.log.warn("[AstralSorcery] Tried to register wildcard Special StarlightBlockHandler for a block that already has special handling! Ignoring and proceeding...");
                }
                handlerMap.clear();
                handlerMap.put(-1, handler);
            } else {
                handlerMap.put(meta, handler);
            }
        }
    }

    public static void setupRegistry() {
        registerEndpoint(Blocks.IRON_ORE, new StarmetalFormHandler());
    }

    //1 instance is/should be created for 1 type of block+meta pair
    //This is NOT suggested as "first choice" - please implement IBlockStarlightRecipient instead if possible.
    public static interface IStarlightBlockHandler {

        public void receiveStarlight(World world, Random rand, BlockPos pos, @Nullable IMajorConstellation starlightType, double amount);

    }

}
