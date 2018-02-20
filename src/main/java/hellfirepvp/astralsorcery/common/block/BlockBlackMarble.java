/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidBase;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockBlackMarble
 * Created by HellFirePvP
 * Date: 21.10.2016 / 23:02
 */
public class BlockBlackMarble extends Block implements BlockCustomName, BlockVariants {

    public static PropertyEnum<BlackMarbleBlockType> BLACK_MARBLE_TYPE = PropertyEnum.create("marbletype", BlackMarbleBlockType.class);

    public BlockBlackMarble() {
        super(Material.ROCK, MapColor.BLACK);
        setHardness(1.0F);
        setHarvestLevel("pickaxe", 1);
        setResistance(3.0F);
        setSoundType(SoundType.STONE);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        setDefaultState(this.blockState.getBaseState().withProperty(BLACK_MARBLE_TYPE, BlackMarbleBlockType.RAW));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (BlackMarbleBlockType t : BlackMarbleBlockType.values()) {
            if(!t.obtainableInCreative()) continue;
            list.add(new ItemStack(this, 1, t.ordinal()));
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        //return super.getActualState(state, worldIn, pos);
        if(state.getValue(BLACK_MARBLE_TYPE).isPillar()) {
            IBlockState st = worldIn.getBlockState(pos.up());
            boolean top = false;
            if(st.getBlock() instanceof BlockBlackMarble && st.getValue(BLACK_MARBLE_TYPE).isPillar()) {
                top = true;
            }
            st = worldIn.getBlockState(pos.down());
            boolean down = false;
            if(st.getBlock() instanceof BlockBlackMarble && st.getValue(BLACK_MARBLE_TYPE).isPillar()) {
                down = true;
            }
            if(top && down) {
                return state.withProperty(BLACK_MARBLE_TYPE, BlackMarbleBlockType.PILLAR);
            } else if(top) {
                return state.withProperty(BLACK_MARBLE_TYPE, BlackMarbleBlockType.PILLAR_BOTTOM);
            } else if(down) {
                return state.withProperty(BLACK_MARBLE_TYPE, BlackMarbleBlockType.PILLAR_TOP);
            } else {
                return state.withProperty(BLACK_MARBLE_TYPE, BlackMarbleBlockType.PILLAR);
            }
        }
        return super.getActualState(state, worldIn, pos);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return p_193383_2_.getValue(BLACK_MARBLE_TYPE).isPillar() ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        BlackMarbleBlockType marbleType = state.getValue(BLACK_MARBLE_TYPE);
        if(marbleType == BlackMarbleBlockType.PILLAR_TOP || marbleType == BlackMarbleBlockType.PILLAR || marbleType == BlackMarbleBlockType.PILLAR_BOTTOM) {
            return 0;
        }
        return super.getLightOpacity(state, world, pos);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        BlackMarbleBlockType marbleType = state.getValue(BLACK_MARBLE_TYPE);
        return marbleType != BlackMarbleBlockType.PILLAR && marbleType != BlackMarbleBlockType.PILLAR_BOTTOM && marbleType != BlackMarbleBlockType.PILLAR_TOP;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        BlackMarbleBlockType marbleType = state.getValue(BLACK_MARBLE_TYPE);
        return marbleType != BlackMarbleBlockType.PILLAR && marbleType != BlackMarbleBlockType.PILLAR_BOTTOM && marbleType != BlackMarbleBlockType.PILLAR_TOP;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        BlackMarbleBlockType marbleType = state.getValue(BLACK_MARBLE_TYPE);
        return marbleType != BlackMarbleBlockType.PILLAR && marbleType != BlackMarbleBlockType.PILLAR_BOTTOM && marbleType != BlackMarbleBlockType.PILLAR_TOP;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        BlackMarbleBlockType marbleType = state.getValue(BLACK_MARBLE_TYPE);
        IBlockState other = world.getBlockState(pos.offset(face));
        if((other.getBlock() instanceof BlockLiquid || other.getBlock() instanceof BlockFluidBase) &&
                (marbleType == BlackMarbleBlockType.PILLAR || marbleType == BlackMarbleBlockType.PILLAR_BOTTOM || marbleType == BlackMarbleBlockType.PILLAR_TOP)) {
            return true;
        }
        if(marbleType == BlackMarbleBlockType.PILLAR) {
            return false;
        }
        if(marbleType == BlackMarbleBlockType.PILLAR_TOP) {
            return face == EnumFacing.UP;
        }
        if(marbleType == BlackMarbleBlockType.PILLAR_BOTTOM) {
            return face == EnumFacing.DOWN;
        }
        return state.isOpaqueCube();
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return true;
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        BlackMarbleBlockType mt = getStateFromMeta(meta).getValue(BLACK_MARBLE_TYPE);
        return mt.getName();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        BlackMarbleBlockType type = state.getValue(BLACK_MARBLE_TYPE);
        return type.getMeta();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta < BlackMarbleBlockType.values().length ? getDefaultState().withProperty(BLACK_MARBLE_TYPE, BlackMarbleBlockType.values()[meta]) : getDefaultState();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BLACK_MARBLE_TYPE);
    }

    @Override
    public List<IBlockState> getValidStates() {
        List<IBlockState> ret = new LinkedList<>();
        for (BlackMarbleBlockType type : BlackMarbleBlockType.values()) {
            ret.add(getDefaultState().withProperty(BLACK_MARBLE_TYPE, type));
        }
        return ret;
    }

    @Override
    public String getStateName(IBlockState state) {
        return state.getValue(BLACK_MARBLE_TYPE).getName();
    }

    public static enum BlackMarbleBlockType implements IStringSerializable {

        RAW(0),
        BRICKS(1),
        PILLAR(2),
        ARCH(3),
        CHISELED(4),
        ENGRAVED(5),
        RUNED(6),

        PILLAR_TOP(2),
        PILLAR_BOTTOM(2);

        private final int meta;

        private BlackMarbleBlockType(int meta) {
            this.meta = meta;
        }

        public ItemStack asStack() {
            return new ItemStack(BlocksAS.blockBlackMarble, 1, meta);
        }

        public IBlockState asBlock() {
            return BlocksAS.blockBlackMarble.getStateFromMeta(meta);
        }

        public boolean isPillar() {
            return this == PILLAR_BOTTOM || this == PILLAR || this == PILLAR_TOP;
        }

        public boolean obtainableInCreative() {
            return this != PILLAR_TOP && this != PILLAR_BOTTOM;
        }

        public int getMeta() {
            return meta;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

    }

}
