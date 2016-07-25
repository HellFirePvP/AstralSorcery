package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.CommonProxy;
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

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockMarble
 * Created by HellFirePvP
 * Date: 22.05.2016 / 16:13
 */
public class BlockMarble extends Block implements BlockCustomName {

    public static PropertyEnum<MarbleBlockType> MARBLE_TYPE = PropertyEnum.create("marbletype", MarbleBlockType.class);

    public BlockMarble() {
        super(Material.rock, MapColor.grayColor);
        setHardness(2.0F);
        setHarvestLevel("pickaxe", 2);
        setResistance(40.0F);
        setStepSound(SoundType.STONE);
        setCreativeTab(CommonProxy.creativeTabAstralSorcery);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (MarbleBlockType t : MarbleBlockType.values()) {
            list.add(new ItemStack(item, 1, t.ordinal()));
        }
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

    public static enum MarbleBlockType implements IStringSerializable {

        RAW;

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }

}
