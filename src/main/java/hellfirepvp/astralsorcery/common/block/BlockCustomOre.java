package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.RockCrystalBuffer;
import hellfirepvp.astralsorcery.common.item.crystal.ItemRockCrystalBase;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCrystalOre
 * Created by HellFirePvP
 * Date: 07.05.2016 / 18:03
 */
public class BlockCustomOre extends Block implements BlockCustomName, BlockVariants {

    public static PropertyEnum<OreType> ORE_TYPE = PropertyEnum.create("oretype", OreType.class);

    public BlockCustomOre() {
        super(Material.ROCK, MapColor.LAPIS);
        setHardness(3.0F);
        setHarvestLevel("pickaxe", 3);
        setResistance(25.0F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (OreType t : OreType.values()) {
            list.add(new ItemStack(item, 1, t.ordinal()));
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        OreType type = state.getValue(ORE_TYPE);
        return type == null ? 0 : type.ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta < OreType.values().length ? getDefaultState().withProperty(ORE_TYPE, OreType.values()[meta]) : getDefaultState();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ORE_TYPE);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        OreType type = state.getValue(ORE_TYPE);
        List<ItemStack> drops = new ArrayList<>();
        switch (type) {
            case ROCK_CRYSTAL:
                drops.add(ItemRockCrystalBase.createRandomBaseCrystal());
                break;
        }
        return drops;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if(!worldIn.isRemote) {
            RockCrystalBuffer buffer = WorldCacheManager.getData(worldIn, WorldCacheManager.SaveKey.ROCK_CRYSTAL);
            buffer.removeOre(pos);
        }
        super.breakBlock(worldIn, pos, state);
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
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        OreType ot = getStateFromMeta(meta).getValue(ORE_TYPE);
        return ot == null ? "null" : ot.getName();
    }

    @Override
    public List<IBlockState> getValidStates() {
        List<IBlockState> ret = new LinkedList<>();
        for (OreType type : OreType.values()) {
            ret.add(getDefaultState().withProperty(ORE_TYPE, type));
        }
        return ret;
    }

    @Override
    public String getStateName(IBlockState state) {
        return state.getValue(ORE_TYPE).getName();
    }

    public static enum OreType implements IStringSerializable {

        ROCK_CRYSTAL;

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }


}
