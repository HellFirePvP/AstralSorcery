package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.item.ItemRockCrystalBase;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
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
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

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
        super(Material.rock, MapColor.lapisColor);
        setHardness(3.0F);
        setHarvestLevel("pickaxe", 3);
        setResistance(25.0F);
        setStepSound(SoundType.STONE);
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
        List<ItemStack> drops = new ArrayList<ItemStack>();
        int size = (Block.RANDOM.nextInt(ItemRockCrystalBase.CrystalProperties.MAX_SIZE)
                + Block.RANDOM.nextInt(ItemRockCrystalBase.CrystalProperties.MAX_SIZE)) / 2;
        int purity = (Block.RANDOM.nextInt(101) + Block.RANDOM.nextInt(101)) / 2;

        ItemRockCrystalBase.CrystalProperties prop =
                new ItemRockCrystalBase.CrystalProperties(size, purity, Block.RANDOM.nextInt(31));
        ItemStack crystal = new ItemStack(ItemsAS.rockCrystal);
        ItemRockCrystalBase.applyCrystalProperties(crystal, prop);
        drops.add(crystal);
        return drops;
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

        CRYSTAL;

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }


}
