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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStructural
 * Created by HellFirePvP
 * Date: 30.07.2016 / 21:50
 */
public class BlockStructural extends Block implements BlockCustomName {

    public static PropertyEnum<BlockType> BLOCK_TYPE = PropertyEnum.create("blocktype", BlockType.class);

    public BlockStructural() {
        super(Material.IRON, MapColor.BLACK);
        setBlockUnbreakable();
        setSoundType(SoundType.GLASS);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (BlockType bt : BlockType.values()) {
            list.add(new ItemStack(item, 1, bt.ordinal()));
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta < BlockType.values().length ? getDefaultState().withProperty(BLOCK_TYPE, BlockType.values()[meta]) : getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        BlockType type = state.getValue(BLOCK_TYPE);
        return type == null ? 0 : type.ordinal();
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BLOCK_TYPE);
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        BlockType mt = getStateFromMeta(meta).getValue(BLOCK_TYPE);
        return mt == null ? "null" : mt.getName();
    }

    public static enum BlockType implements IStringSerializable {

        NONE;

        @Override
        public String getName() {
            return name().toLowerCase();
        }

    }

}
