package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCustomSandOre
 * Created by HellFirePvP
 * Date: 17.08.2016 / 13:07
 */
public class BlockCustomSandOre extends BlockFalling implements BlockCustomName, BlockVariants {

    private static final Random rand = new Random();

    public static PropertyEnum<OreType> ORE_TYPE = PropertyEnum.create("oretype", OreType.class);

    public BlockCustomSandOre() {
        super(Material.SAND);
        setHardness(0.5F);
        setSoundType(SoundType.SAND);
        setHarvestLevel("shovel", 1);
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
        return type == null ? 0 : type.getMeta();
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
            case AQUAMARINE:
                int f = fortune + 1;
                int i = rand.nextInt(f * 2) - 1;
                if(i < 0) {
                    i = 0;
                }
                for (int j = 0; j < (i + 1); j++) {
                    drops.add(ItemCraftingComponent.MetaType.AQUAMARINE.asStack());
                }
                break;
        }
        return drops;
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

        AQUAMARINE(0);

        private final int meta;

        private OreType(int meta) {
            this.meta = meta;
        }

        public ItemStack asStack() {
            return new ItemStack(BlocksAS.customSandOre, 1, meta);
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
