/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.struct;

import hellfirepvp.astralsorcery.common.block.BlockStructural;
import hellfirepvp.astralsorcery.common.item.base.render.ISpecialStackDescriptor;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.UniversalBucket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockArray
 * Created by HellFirePvP
 * Date: 30.07.2016 / 16:23
 */
public class BlockArray {

    protected static final Random STATIC_RAND = new Random();

    protected Map<BlockPos, TileEntityCallback> tileCallbacks = new HashMap<>();
    protected Map<BlockPos, BlockInformation> pattern = new HashMap<>();
    private Vec3i min = new Vec3i(0, 0, 0), max = new Vec3i(0, 0, 0), size = new Vec3i(0, 0, 0);

    public void addBlock(int x, int y, int z, @Nonnull IBlockState state) {
        addBlock(new BlockPos(x, y, z), state);
    }

    public void addBlock(BlockPos offset, @Nonnull IBlockState state) {
        Block b = state.getBlock();
        pattern.put(offset, new BlockInformation(b, state));
        updateSize(offset);
    }

    public void addBlock(int x, int y, int z, @Nonnull IBlockState state, BlockStateCheck match) {
        addBlock(new BlockPos(x, y, z), state, match);
    }

    public void addBlock(BlockPos offset, @Nonnull IBlockState state, BlockStateCheck match) {
        Block b = state.getBlock();
        pattern.put(offset, new BlockInformation(b, state, match));
        updateSize(offset);
    }

    public void addAll(BlockArray other) {
        addAll(other, null);
    }

    public void addAll(BlockArray other, @Nullable Function<BlockPos, BlockPos> positionTransform) {
        for (Map.Entry<BlockPos, BlockInformation> patternEntry : other.pattern.entrySet()) {
            BlockPos to = patternEntry.getKey();
            if(positionTransform != null) {
                to = positionTransform.apply(to);
            }
            pattern.put(to, patternEntry.getValue());
            updateSize(to);
        }
        for (Map.Entry<BlockPos, TileEntityCallback> patternEntry : other.tileCallbacks.entrySet()) {
            BlockPos to = patternEntry.getKey();
            if(positionTransform != null) {
                to = positionTransform.apply(to);
            }
            tileCallbacks.put(to, patternEntry.getValue());
        }
    }

    public void addTileCallback(BlockPos pos, TileEntityCallback callback) {
        tileCallbacks.put(pos, callback);
    }

    public boolean hasBlockAt(BlockPos pos) {
        return pattern.containsKey(pos);
    }

    public boolean isEmpty() {
        return pattern.isEmpty();
    }

    public Vec3i getMax() {
        return max;
    }

    public Vec3i getMin() {
        return min;
    }

    public Vec3i getSize() {
        return size;
    }

    private void updateSize(BlockPos addedPos) {
        if(addedPos.getX() < min.getX()) {
            min = new Vec3i(addedPos.getX(), min.getY(), min.getZ());
        }
        if(addedPos.getX() > max.getX()) {
            max = new Vec3i(addedPos.getX(), max.getY(), max.getZ());
        }
        if(addedPos.getY() < min.getY()) {
            min = new Vec3i(min.getX(), addedPos.getY(), min.getZ());
        }
        if(addedPos.getY() > max.getY()) {
            max = new Vec3i(max.getX(), addedPos.getY(), max.getZ());
        }
        if(addedPos.getZ() < min.getZ()) {
            min = new Vec3i(min.getX(), min.getY(), addedPos.getZ());
        }
        if(addedPos.getZ() > max.getZ()) {
            max = new Vec3i(max.getX(), max.getY(), addedPos.getZ());
        }
        size = new Vec3i(max.getX() - min.getX() + 1, max.getY() - min.getY() + 1, max.getZ() - min.getZ() + 1);
    }

    public Map<BlockPos, BlockInformation> getPattern() {
        return pattern;
    }

    public Map<BlockPos, TileEntityCallback> getTileCallbacks() {
        return tileCallbacks;
    }

    public void addBlockCube(@Nonnull IBlockState state, int ox, int oy, int oz, int tx, int ty, int tz) {
        addBlockCube(state, new BlockStateCheck.Meta(state.getBlock(), state.getBlock().getMetaFromState(state)), ox, oy, oz, tx, ty, tz);
    }

    public void addBlockCube(@Nonnull IBlockState state, BlockStateCheck match, int ox, int oy, int oz, int tx, int ty, int tz) {
        int lx, ly, lz;
        int hx, hy, hz;
        if(ox < tx) {
            lx = ox;
            hx = tx;
        } else {
            lx = tx;
            hx = ox;
        }
        if(oy < ty) {
            ly = oy;
            hy = ty;
        } else {
            ly = ty;
            hy = oy;
        }
        if(oz < tz) {
            lz = oz;
            hz = tz;
        } else {
            lz = tz;
            hz = oz;
        }

        for (int xx = lx; xx <= hx; xx++) {
            for (int zz = lz; zz <= hz; zz++) {
                for (int yy = ly; yy <= hy; yy++) {
                    addBlock(new BlockPos(xx, yy, zz), state, match);
                }
            }
        }
    }

    public Map<BlockPos, IBlockState> placeInWorld(World world, BlockPos center) {
        Map<BlockPos, IBlockState> result = new HashMap<>();
        for (Map.Entry<BlockPos, BlockInformation> entry : pattern.entrySet()) {
            BlockInformation info = entry.getValue();
            BlockPos at = center.add(entry.getKey());
            IBlockState state = info.state;
            world.setBlockState(at, state, 3);
            result.put(at, state);

            if(state.getBlock() instanceof BlockLiquid || state.getBlock() instanceof BlockFluidBase) {
                world.neighborChanged(at, state.getBlock(), at);
            }

            TileEntity placed = world.getTileEntity(at);
            if(tileCallbacks.containsKey(entry.getKey())) {
                TileEntityCallback callback = tileCallbacks.get(entry.getKey());
                if(callback.isApplicable(placed)) {
                    callback.onPlace(world, at, placed);
                }
            }
        }
        return result;
    }

    public List<ItemStack> getAsDescriptiveStacks() {
        List<ItemStack> out = new LinkedList<>();
        for (BlockInformation info : pattern.values()) {
            int meta = info.metadata;
            ItemStack s;
            if(info.type instanceof BlockFluidBase) {
                s = UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, ((BlockFluidBase) info.type).getFluid());
            } else if(info.type instanceof BlockStructural) {
                continue;
                //IBlockState otherState = info.state.getValue(BlockStructural.BLOCK_TYPE).getSupportedState();
                //Item i = Item.getItemFromBlock(otherState.getBlock());
                //if(i == null) continue;
                //s = new ItemStack(i, 1, otherState.getBlock().getMetaFromState(otherState));
            } else if(info.type instanceof ISpecialStackDescriptor) {
                s = ((ISpecialStackDescriptor) info.type).getDecriptor(info.state);
            } else {
                Item i = Item.getItemFromBlock(info.type);
                if(i == Items.AIR) continue;
                s = new ItemStack(i, 1, meta);
            }
            if(!s.isEmpty()) {
                boolean found = false;
                for (ItemStack stack : out) {
                    if(stack.getItem().getRegistryName().equals(s.getItem().getRegistryName()) && stack.getItemDamage() == s.getItemDamage()) {
                        stack.setCount(stack.getCount() + 1);
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    out.add(s);
                }
            }
        }
        return out;
    }

    public static interface TileEntityCallback {

        public boolean isApplicable(TileEntity te);

        public void onPlace(IBlockAccess access, BlockPos at, TileEntity te);

    }

    public static class BlockInformation {

        public final Block type;
        public final IBlockState state;
        public final int metadata;
        public final BlockStateCheck matcher;

        protected BlockInformation(Block type, IBlockState state) {
            this(type, state, new BlockStateCheck.Meta(type, type.getMetaFromState(state)));
        }

        protected BlockInformation(Block type, IBlockState state, BlockStateCheck matcher) {
            this.type = type;
            this.state = state;
            this.metadata = type.getMetaFromState(state);
            this.matcher = matcher;
        }

    }

}
