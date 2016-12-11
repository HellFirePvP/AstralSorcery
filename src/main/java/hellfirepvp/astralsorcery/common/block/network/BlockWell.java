package hellfirepvp.astralsorcery.common.block.network;

import hellfirepvp.astralsorcery.common.item.base.ItemWellCatalyst;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.registry.RegistryAchievements;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileWell;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockWell
 * Created by HellFirePvP
 * Date: 18.10.2016 / 12:43
 */
public class BlockWell extends BlockStarlightNetwork {

    private static final AxisAlignedBB boxWell = new AxisAlignedBB(1D / 16D, 0D, 1D / 16D, 15D / 16D, 1, 15D / 16D);
    private static List<AxisAlignedBB> collisionBoxes;

    public BlockWell() {
        super(Material.ROCK, MapColor.QUARTZ);
        setHardness(3.0F);
        setSoundType(SoundType.STONE);
        setResistance(25.0F);
        setHarvestLevel("pickaxe", 3);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
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
        return new TileWell();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote) {

            if(heldItem != null && heldItem.getItem() != null) {
                TileWell tw = MiscUtils.getTileAt(worldIn, pos, TileWell.class, false);
                if(tw == null) return false;

                if(heldItem.getItem() instanceof ItemWellCatalyst) {
                    ItemStackHandler handle = tw.getInventoryHandler();
                    if(handle.getStackInSlot(0) != null) return false;

                    ItemWellCatalyst catalyst = (ItemWellCatalyst) heldItem.getItem();
                    if(catalyst.isCatalyst(heldItem)) {
                        if(!worldIn.isAirBlock(pos.up())) {
                            return false;
                        }

                        handle.setStackInSlot(0, ItemUtils.copyStackWithSize(heldItem, 1));
                        worldIn.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);

                        //Lets assume it starts collecting right away...
                        playerIn.addStat(RegistryAchievements.achvLiqStarlight);

                        if(!playerIn.isCreative()) {
                            heldItem.stackSize--;
                        }
                        if(heldItem.stackSize <= 0) {
                            playerIn.setHeldItem(hand, null);
                        }
                    }
                }

                if(FluidUtil.tryFillContainerAndStow(heldItem, tw, new InvWrapper(playerIn.inventory), 1000, playerIn)) {
                    tw.markForUpdate();
                }
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileWell tw = MiscUtils.getTileAt(worldIn, pos, TileWell.class, true);
        if(tw != null && !worldIn.isRemote) {
            ItemStack stack = tw.getInventoryHandler().getStackInSlot(0);
            if(stack != null) {
                tw.breakCatalyst();
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side != EnumFacing.UP;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn) {
        for (AxisAlignedBB box : collisionBoxes) {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, box);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return boxWell;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    static {
        List<AxisAlignedBB> boxes = new LinkedList<>();

        boxes.add(new AxisAlignedBB( 1D / 16D,       0D,  1D / 16D, 15D / 16D, 5D / 16D, 15D / 16D));

        boxes.add(new AxisAlignedBB( 1D / 16D, 5D / 16D,  1D / 16D,  2D / 16D,       1D, 15D / 16D));
        boxes.add(new AxisAlignedBB( 1D / 16D, 5D / 16D,  1D / 16D, 15D / 16D,       1D,  2D / 16D));
        boxes.add(new AxisAlignedBB(14D / 16D, 5D / 16D,  1D / 16D, 15D / 16D,       1D, 15D / 16D));
        boxes.add(new AxisAlignedBB( 1D / 16D, 5D / 16D, 14D / 16D, 15D / 16D,       1D, 15D / 16D));

        collisionBoxes = Collections.unmodifiableList(boxes);
    }

}
