package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.block.tile.IVariantTileProvider;
import hellfirepvp.astralsorcery.common.block.tile.TileAltar;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStoneMachine
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:11
 */
public class BlockStoneMachine extends BlockContainer implements BlockCustomName {

    public static PropertyEnum<MachineType> MACHINE_TYPE = PropertyEnum.create("machine", MachineType.class);

    public BlockStoneMachine() {
        super(Material.rock, MapColor.grayColor);
        setHardness(3.0F);
        setResistance(25.0F);
        setStepSound(SoundType.STONE);
        setCreativeTab(CommonProxy.creativeTabAstralSorcery);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(MACHINE_TYPE) != null;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        MachineType type = state.getValue(MACHINE_TYPE);
        if (type == null) return null;
        return type.provideTileEntity(world, state);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }


    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta < MachineType.values().length ? getDefaultState().withProperty(MACHINE_TYPE, MachineType.values()[meta]) : getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        MachineType type = state.getValue(MACHINE_TYPE);
        return type == null ? 0 : type.ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, MACHINE_TYPE);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL; //Default one for now....
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        MachineType mt = getStateFromMeta(meta).getValue(MACHINE_TYPE);
        return mt == null ? "null" : mt.getName();
    }

    public static enum MachineType implements IStringSerializable, IVariantTileProvider {

        ALTAR() {
            @Override
            public TileEntity provideTileEntity(World world, IBlockState state) {
                return new TileAltar();
            }
        };

        @Override
        public String getName() {
            return name().toLowerCase();
        }

        @Override
        public String toString() {
            return getName();
        }
    }

}
