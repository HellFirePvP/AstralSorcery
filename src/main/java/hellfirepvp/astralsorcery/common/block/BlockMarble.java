/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import com.google.common.collect.Maps;
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
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidBase;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockMarble
 * Created by HellFirePvP
 * Date: 22.05.2016 / 16:13
 */
public class BlockMarble extends Block implements BlockCustomName, BlockVariants, BlockDynamicStateMapper.Festive {

    //private static final int RAND_MOSS_CHANCE = 10;

    public static PropertyEnum<MarbleBlockType> MARBLE_TYPE = PropertyEnum.create("marbletype", MarbleBlockType.class);

    public BlockMarble() {
        super(Material.ROCK, MapColor.GRAY);
        setHardness(1.0F);
        setHarvestLevel("pickaxe", 1);
        setResistance(3.0F);
        setSoundType(SoundType.STONE);
        //setTickRandomly(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        setDefaultState(this.blockState.getBaseState().withProperty(MARBLE_TYPE, MarbleBlockType.RAW));
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (MarbleBlockType t : MarbleBlockType.values()) {
            if(!t.obtainableInCreative()) continue;
            list.add(new ItemStack(this, 1, t.getMeta()));
        }
    }

    /*@Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote && worldIn.isRaining() && rand.nextInt(RAND_MOSS_CHANCE) == 0) {
            MarbleBlockType type = state.getValue(MARBLE_TYPE);
            if (type.canTurnMossy() && worldIn.isRainingAt(pos)) {
                worldIn.setBlockState(pos, state.withProperty(MARBLE_TYPE, type.getMossyEquivalent()), 3);
            }
        }
    }*/

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        //return super.getActualState(state, worldIn, pos);
        if(state.getValue(MARBLE_TYPE).isPillar()) {
            IBlockState st = worldIn.getBlockState(pos.up());
            boolean top = false;
            if(st.getBlock() instanceof BlockMarble && st.getValue(MARBLE_TYPE).isPillar()) {
                top = true;
            }
            st = worldIn.getBlockState(pos.down());
            boolean down = false;
            if(st.getBlock() instanceof BlockMarble && st.getValue(MARBLE_TYPE).isPillar()) {
                down = true;
            }
            if(top && down) {
                return state.withProperty(MARBLE_TYPE, MarbleBlockType.PILLAR);
            } else if(top) {
                return state.withProperty(MARBLE_TYPE, MarbleBlockType.PILLAR_BOTTOM);
            } else if(down) {
                return state.withProperty(MARBLE_TYPE, MarbleBlockType.PILLAR_TOP);
            } else {
                return state.withProperty(MARBLE_TYPE, MarbleBlockType.PILLAR);
            }
        }
        return super.getActualState(state, worldIn, pos);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return p_193383_2_.getValue(MARBLE_TYPE).isPillar() ? BlockFaceShape.UNDEFINED : BlockFaceShape.SOLID;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        MarbleBlockType marbleType = state.getValue(MARBLE_TYPE);
        if(marbleType == MarbleBlockType.PILLAR_TOP || marbleType == MarbleBlockType.PILLAR || marbleType == MarbleBlockType.PILLAR_BOTTOM) {
            return 0;
        }
        return super.getLightOpacity(state, world, pos);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        MarbleBlockType marbleType = state.getValue(MARBLE_TYPE);
        return marbleType != MarbleBlockType.PILLAR && marbleType != MarbleBlockType.PILLAR_BOTTOM && marbleType != MarbleBlockType.PILLAR_TOP;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        MarbleBlockType marbleType = state.getValue(MARBLE_TYPE);
        return marbleType != MarbleBlockType.PILLAR && marbleType != MarbleBlockType.PILLAR_BOTTOM && marbleType != MarbleBlockType.PILLAR_TOP;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        MarbleBlockType marbleType = state.getValue(MARBLE_TYPE);
        return marbleType != MarbleBlockType.PILLAR && marbleType != MarbleBlockType.PILLAR_BOTTOM && marbleType != MarbleBlockType.PILLAR_TOP;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        MarbleBlockType marbleType = state.getValue(MARBLE_TYPE);
        IBlockState other = world.getBlockState(pos.offset(face));
        if((other.getBlock() instanceof BlockLiquid || other.getBlock() instanceof BlockFluidBase) &&
                (marbleType == MarbleBlockType.PILLAR || marbleType == MarbleBlockType.PILLAR_BOTTOM || marbleType == MarbleBlockType.PILLAR_TOP)) {
            return false;
        }
        if(marbleType == MarbleBlockType.PILLAR_TOP) {
            return face == EnumFacing.UP;
        }
        if(marbleType == MarbleBlockType.PILLAR_BOTTOM) {
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
        MarbleBlockType mt = getStateFromMeta(meta).getValue(MARBLE_TYPE);
        return mt.getName();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        MarbleBlockType type = state.getValue(MARBLE_TYPE);
        return type.getMeta();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta < MarbleBlockType.values().length ? getDefaultState().withProperty(MARBLE_TYPE, MarbleBlockType.values()[meta]) : getDefaultState();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, MARBLE_TYPE);
    }

    @Override
    public List<IBlockState> getValidStates() {
        List<IBlockState> ret = new LinkedList<>();
        for (MarbleBlockType type : MarbleBlockType.values()) {
            ret.add(getDefaultState().withProperty(MARBLE_TYPE, type));
        }
        return ret;
    }

    @Override
    public String getStateName(IBlockState state) {
        return state.getValue(MARBLE_TYPE).getName() + (handleRegisterStateMapper() ? "_festive" : "");
    }

    @Override
    public Map<IBlockState, ModelResourceLocation> getModelLocations(Block blockIn) {
        ResourceLocation rl = Block.REGISTRY.getNameForObject(blockIn);
        rl = new ResourceLocation(rl.getResourceDomain(), rl.getResourcePath() + "_festive");
        Map<IBlockState, ModelResourceLocation> out = Maps.newHashMap();
        for (IBlockState state : getValidStates()) {
            out.put(state, new ModelResourceLocation(rl, getPropertyString(state.getProperties())));
        }
        return out;
    }

    public static enum MarbleBlockType implements IStringSerializable {

        RAW(0),
        BRICKS(1),
        PILLAR(2),
        ARCH(3),
        CHISELED(4),
        ENGRAVED(5),
        RUNED(6),

        PILLAR_TOP(2),
        PILLAR_BOTTOM(2);

        //BRICKS_MOSSY,
        //PILLAR_MOSSY,
        //CRACK_MOSSY;

        private final int meta;

        private MarbleBlockType(int meta) {
            this.meta = meta;
        }

        public ItemStack asStack() {
            return new ItemStack(BlocksAS.blockMarble, 1, meta);
        }

        public IBlockState asBlock() {
            return BlocksAS.blockMarble.getStateFromMeta(meta);
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

        /*public boolean canTurnMossy() {
            return this == BRICKS || this == PILLAR || this == CRACKED;
        }

        public MarbleBlockType getMossyEquivalent() {
            if(!canTurnMossy()) return null;
            switch (this) {
                case BRICKS:
                    return BRICKS_MOSSY;
                case PILLAR:
                    return PILLAR_MOSSY;
                case CRACKED:
                    return CRACK_MOSSY;
            }
            return null;
        }*/
    }

}
