package hellfirepvp.astralsorcery.common.block.network;

import hellfirepvp.astralsorcery.common.block.BlockVariants;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalLens;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockLens
 * Created by HellFirePvP
 * Date: 07.08.2016 / 22:31
 */
public class BlockLens extends BlockStarlightNetwork implements BlockVariants {

    private static final AxisAlignedBB boxLens = new AxisAlignedBB(2.5D/16D, 0, 2.5D/16D, 13.5D/16D, 14.5D/16D, 13.5D/16D);

    public static PropertyBool RENDER_FULLY = PropertyBool.create("render");

    public BlockLens() {
        super(Material.ROCK, MapColor.BLACK);
        setHardness(3.0F);
        setSoundType(SoundType.GLASS);
        setResistance(12.0F);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        setDefaultState(this.blockState.getBaseState().withProperty(RENDER_FULLY, true));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        ItemStack stack = new ItemStack(itemIn);
        CrystalProperties.applyCrystalProperties(stack, CrystalProperties.getMaxCelestialProperties());
        list.add(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        CrystalProperties.addPropertyTooltip(CrystalProperties.getCrystalProperties(stack), tooltip);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return boxLens;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) { return false; }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCrystalLens();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, RENDER_FULLY);
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.DOWN;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        ItemStack stack = super.getPickBlock(getActualState(state, world, pos), target, world, pos, player);
        TileCrystalLens lens = MiscUtils.getTileAt(world, pos, TileCrystalLens.class, true);
        if(lens != null) {
            CrystalProperties.applyCrystalProperties(stack, lens.getCrystalProperties());
        } else {
            CrystalProperties.applyCrystalProperties(stack, CrystalProperties.getMaxCelestialProperties());
        }
        return stack;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileCrystalLens lens = MiscUtils.getTileAt(worldIn, pos, TileCrystalLens.class, true);
        if(lens != null && lens.getLensColor() != null) {
            ItemStack drop = lens.getLensColor().asStack();
            ItemUtils.dropItemNaturally(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote && playerIn.isSneaking()) {
            TileCrystalLens lens = MiscUtils.getTileAt(worldIn, pos, TileCrystalLens.class, true);
            if(lens != null && lens.getLensColor() != null) {
                ItemStack drop = lens.getLensColor().asStack();
                ItemUtils.dropItemNaturally(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
                lens.setLensColor(null);
                return true;
            }
        }
        return false;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.withProperty(RENDER_FULLY, false);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileCrystalLens te = MiscUtils.getTileAt(worldIn, pos, TileCrystalLens.class, true);
        if(te == null) return;
        te.onPlace(CrystalProperties.getCrystalProperties(stack));
    }

    @Override
    public List<IBlockState> getValidStates() {
        return Arrays.asList(getDefaultState().withProperty(RENDER_FULLY, false), getDefaultState().withProperty(RENDER_FULLY, true));
    }

    @Override
    public String getStateName(IBlockState state) {
        return state.getValue(RENDER_FULLY).toString();
    }
}
