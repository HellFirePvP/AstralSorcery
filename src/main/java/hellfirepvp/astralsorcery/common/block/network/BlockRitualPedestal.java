/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.network;

import hellfirepvp.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockRitualPedestal
 * Created by HellFirePvP
 * Date: 28.09.2016 / 13:45
 */
public class BlockRitualPedestal extends BlockStarlightNetwork {

    private static final AxisAlignedBB box = new AxisAlignedBB(0, 0, 0, 1, 13D/16D, 1);

    public BlockRitualPedestal() {
        super(Material.ROCK, MapColor.QUARTZ);
        setHardness(3.0F);
        setSoundType(SoundType.STONE);
        setResistance(25.0F);
        setHarvestLevel("pickaxe", 3);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        ItemStack pedestal = new ItemStack(itemIn);
        list.add(pedestal);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileRitualPedestal();
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        RenderingUtils.playBlockBreakParticles(pos,
                BlocksAS.blockMarble.getDefaultState()
                        .withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW));
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        return true;
    }*/

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileRitualPedestal pedestal = MiscUtils.getTileAt(worldIn, pos, TileRitualPedestal.class, true);
        if(pedestal == null) {
            return false;
        }
        ItemStackHandler handle = pedestal.getInventoryHandler();
        ItemStack in = handle.getStackInSlot(0);
        if(heldItem != null) {
            Item i = heldItem.getItem();
            if(i != null && i instanceof ItemTunedCrystalBase) {
                if(in == null) {
                    handle.setStackInSlot(0, ItemUtils.copyStackWithSize(heldItem, 1));
                    playerIn.setHeldItem(hand, null);
                }
            }
        }
        if(in != null && playerIn.isSneaking()) {
            ItemUtils.dropItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.75, pos.getZ() + 0.5, in);
            handle.setStackInSlot(0, null);
        }
        return true;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
        TileRitualPedestal te = MiscUtils.getTileAt(worldIn, pos, TileRitualPedestal.class, true);
        if(te != null && !worldIn.isRemote) {
            BlockPos toCheck = pos.up();
            IBlockState other = worldIn.getBlockState(toCheck);
            if(other.isSideSolid(worldIn, toCheck, EnumFacing.DOWN)) {
                TileReceiverBaseInventory.ItemHandlerTile handle = te.getInventoryHandler();
                ItemUtils.dropInventory(te.getInventoryHandler(), worldIn, pos);
                handle.clearInventory();
                te.markForUpdate();
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileRitualPedestal te = MiscUtils.getTileAt(worldIn, pos, TileRitualPedestal.class, true);
        if(te != null && !worldIn.isRemote) {
            if(placer != null && placer instanceof EntityPlayer) {
                te.setOwner(placer.getUniqueID());
            }
        }
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.DOWN;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return box;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean canRenderInLayer(BlockRenderLayer layer) {
        return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
    }
}
