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
 * Class: BlockBlackMarble
 * Created by HellFirePvP
 * Date: 21.10.2016 / 23:02
 */
public class BlockBlackMarble extends Block implements BlockCustomName, BlockVariants {

    public static PropertyEnum<BlackMarbleBlockType> BLACK_MARBLE_TYPE = PropertyEnum.create("marbletype", BlackMarbleBlockType.class);

    public BlockBlackMarble() {
        super(Material.ROCK, MapColor.BLACK);
        setHardness(1.0F);
        setHarvestLevel("pickaxe", 2);
        setResistance(10.0F);
        setSoundType(SoundType.STONE);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        setDefaultState(this.blockState.getBaseState().withProperty(BLACK_MARBLE_TYPE, BlackMarbleBlockType.RAW));
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (BlackMarbleBlockType t : BlackMarbleBlockType.values()) {
            list.add(new ItemStack(item, 1, t.ordinal()));
        }
    }

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
        BlackMarbleBlockType mt = getStateFromMeta(meta).getValue(BLACK_MARBLE_TYPE);
        return mt == null ? "null" : mt.getName();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        BlackMarbleBlockType type = state.getValue(BLACK_MARBLE_TYPE);
        return type == null ? 0 : type.ordinal();
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

        RAW;

        @Override
        public String getName() {
            return name().toLowerCase();
        }

    }

}
