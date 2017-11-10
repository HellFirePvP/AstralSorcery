/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileBore;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockBoreHead
 * Created by HellFirePvP
 * Date: 07.11.2017 / 20:22
 */
public class BlockBoreHead extends Block implements BlockCustomName, BlockVariants {

    public static final PropertyEnum<TileBore.BoreType> BORE_TYPE = PropertyEnum.create("type", TileBore.BoreType.class);

    public BlockBoreHead() {
        super(Material.IRON, MapColor.GOLD);
        setHarvestLevel("pickaxe", 2);
        setHardness(10F);
        setResistance(15F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        setDefaultState(this.blockState.getBaseState().withProperty(BORE_TYPE, TileBore.BoreType.LIQUID));
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (TileBore.BoreType bt : TileBore.BoreType.values()) {
            items.add(new ItemStack(this, 1, bt.ordinal()));
        }
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return super.canPlaceBlockAt(worldIn, pos) &&
                side == EnumFacing.DOWN &&
                worldIn.getBlockState(pos.offset(EnumFacing.UP)).getBlock() instanceof BlockBore;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BORE_TYPE).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(BORE_TYPE,
                TileBore.BoreType.values()[MathHelper.clamp(meta, 0, TileBore.BoreType.values().length - 1)]);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BORE_TYPE);
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        TileBore.BoreType bt = getStateFromMeta(meta).getValue(BORE_TYPE);
        return bt.getName();
    }

    @Override
    public List<IBlockState> getValidStates() {
        return singleEnumPropertyStates(getDefaultState(), BORE_TYPE, TileBore.BoreType.values());
    }

    @Override
    public String getStateName(IBlockState state) {
        return extractEnumPropertyString(state, BORE_TYPE);
    }
}
