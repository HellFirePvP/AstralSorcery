/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.TriFunction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.block.trees.Tree;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.BlockSnapshot;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TreeType
 * Created by HellFirePvP
 * Date: 04.09.2020 / 22:39
 */
public class TreeType {

    private static final List<TreeType> TYPES = new ArrayList<>();

    private final BiPredicate<World, BlockPos> treeTest;
    private final TriFunction<ServerWorld, BlockPos, Random, Supplier<List<BlockPos>>> treeGenerator;

    private TreeType(BiPredicate<World, BlockPos> treeTest, TriFunction<ServerWorld, BlockPos, Random, Supplier<List<BlockPos>>> treeGenerator) {
        this.treeTest = treeTest;
        this.treeGenerator = treeGenerator;
    }

    public static TreeType register(BiPredicate<World, BlockPos> treeTest, TriFunction<ServerWorld, BlockPos, Random, Supplier<List<BlockPos>>> treeGenerator) {
        TreeType type = new TreeType(treeTest, treeGenerator);
        TYPES.add(type);
        return type;
    }

    public Supplier<List<BlockPos>> getTreeGenerator(ServerWorld world, BlockPos pos, Random rand) {
        return this.treeGenerator.apply(world, pos, rand);
    }

    @Nullable
    public static TreeType isTree(World world, BlockPos pos) {
        for (TreeType type : TYPES) {
            if (type.treeTest.test(world, pos)) {
                return type;
            }
        }
        return null;
    }

    static {
        register((world, pos) -> {
            BlockState state = world.getBlockState(pos);
            return state.getBlock() instanceof SaplingBlock && ((SaplingBlock) state.getBlock()).tree != null;
        }, (world, pos, rand) -> {
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof SaplingBlock) {
                Tree treeFeature = ((SaplingBlock) state.getBlock()).tree;
                return () -> {
                    List<BlockSnapshot> blockSnapshots = MiscUtils.captureBlockChanges(world, () -> {
                        treeFeature.place(world, world.getChunkProvider().getChunkGenerator(), pos, state, rand);
                    });
                    return blockSnapshots.stream()
                            .filter(snapshot -> {
                                Block b = snapshot.getCurrentBlock().getBlock();
                                return b.isIn(BlockTags.LEAVES) || b.isIn(BlockTags.LOGS) || b instanceof VineBlock;
                            })
                            .map(BlockSnapshot::getPos)
                            .collect(Collectors.toList());
                };
            }
            return Collections::emptyList;
        });
    }
}
