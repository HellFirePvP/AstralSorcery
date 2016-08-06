package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockMarble
 * Created by HellFirePvP
 * Date: 22.05.2016 / 16:13
 */
public class BlockMarble extends Block implements BlockCustomName, BlockVariants {

    //private static final int RAND_MOSS_CHANCE = 10;

    public static PropertyEnum<MarbleBlockType> MARBLE_TYPE = PropertyEnum.create("marbletype", MarbleBlockType.class);

    public BlockMarble() {
        super(Material.ROCK, MapColor.GRAY);
        setHardness(2.0F);
        setHarvestLevel("pickaxe", 2);
        setResistance(40.0F);
        setSoundType(SoundType.STONE);
        //setTickRandomly(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        setDefaultState(this.blockState.getBaseState().withProperty(MARBLE_TYPE, MarbleBlockType.RAW));
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (MarbleBlockType t : MarbleBlockType.values()) {
            list.add(new ItemStack(item, 1, t.ordinal()));
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
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return true;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return true;
    }

    @Override
    public boolean isFullyOpaque(IBlockState state) {
        return true;
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        MarbleBlockType mt = getStateFromMeta(meta).getValue(MARBLE_TYPE);
        return mt == null ? "null" : mt.getName();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        MarbleBlockType type = state.getValue(MARBLE_TYPE);
        return type == null ? 0 : type.ordinal();
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
        return state.getValue(MARBLE_TYPE).getName();
    }

    public static enum MarbleBlockType implements IStringSerializable {

        RAW,
        BRICKS,
        PILLAR,
        ARCH,
        CHISELED,
        ENGRAVED,
        RUNED;

        //BRICKS_MOSSY,
        //PILLAR_MOSSY,
        //CRACK_MOSSY;

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
