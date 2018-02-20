/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockChalice
 * Created by HellFirePvP
 * Date: 18.10.2017 / 19:58
 */
public class BlockChalice extends BlockContainer {

    public static PropertyBool ACTIVE = PropertyBool.create("active");

    public BlockChalice() {
        super(Material.IRON, MapColor.GOLD);
        setHardness(2.0F);
        setSoundType(SoundType.METAL);
        setResistance(15.0F);
        setHarvestLevel("pickaxe", 1);
        setLightLevel(0.3F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
        setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE, true));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        IBlockState state = world.getBlockState(pos);
        if(state.getValue(ACTIVE)) {
            RenderingUtils.playBlockBreakParticles(pos, getDefaultState());
            RenderingUtils.playBlockBreakParticles(pos.up(), getDefaultState());
        } else {
            RenderingUtils.playBlockBreakParticles(pos, getDefaultState());
            RenderingUtils.playBlockBreakParticles(pos.down(), getDefaultState());
        }
        return true;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!state.getValue(ACTIVE)) {
            return onBlockActivated(worldIn, pos.down(), worldIn.getBlockState(pos.down()), playerIn, hand, facing, hitX, hitY, hitZ);
        }
        ItemStack interact = playerIn.getHeldItem(hand);
        TileChalice tc = MiscUtils.getTileAt(worldIn, pos, TileChalice.class, true);
        if(tc != null) {
            IFluidHandlerItem fhi = FluidUtil.getFluidHandler(interact);
            if(fhi != null) {
                FluidStack st = FluidUtil.getFluidContained(interact);
                if(st != null) {
                    if(!worldIn.isRemote) {
                        FluidActionResult far = FluidUtil.tryEmptyContainer(interact, tc.getTank(), 1000, playerIn, true);
                        if(far.isSuccess()) {
                            if(!playerIn.isCreative()) {
                                interact.shrink(1);
                                playerIn.setHeldItem(hand, interact);
                                playerIn.inventory.placeItemBackInInventory(worldIn, far.getResult());
                            }
                        }
                        tc.markForUpdate();
                    }
                } else {
                    if(!worldIn.isRemote) {
                        FluidActionResult far = FluidUtil.tryFillContainer(interact, tc.getTank(), 1000, playerIn, true);
                        if(far.isSuccess()) {
                            if(!playerIn.isCreative()) {
                                interact.shrink(1);
                                playerIn.setHeldItem(hand, interact);
                                playerIn.inventory.placeItemBackInInventory(worldIn, far.getResult());
                            }
                        }
                        tc.markForUpdate();
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if(state.getValue(ACTIVE)) {
            return new AxisAlignedBB(2D / 16D, 0, 2D / 16D, 14D / 16D, 2, 14D / 16D);
        } else {
            return new AxisAlignedBB(2D / 16D, -1, 2D / 16D, 14D / 16D, 1, 14D / 16D);
        }
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if(state.getValue(ACTIVE)) {
            worldIn.setBlockState(pos.up(), BlocksAS.blockChalice.getDefaultState().withProperty(ACTIVE, false));
        }
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos.up());
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if(state.getValue(ACTIVE)) {
            if(world.isAirBlock(pos.up())) {
                world.setBlockToAir(pos);
            }
        } else {
            if(world.isAirBlock(pos.down())) {
                world.setBlockToAir(pos);
            }
        }
        super.neighborChanged(state, world, pos, blockIn, fromPos);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        if(!(world instanceof World)) {
            super.onNeighborChange(world, pos, neighbor);
            return;
        }
        IBlockState state = world.getBlockState(pos);
        if(state.getValue(ACTIVE)) {
            if(world.isAirBlock(pos.up())) {
                ((World) world).setBlockToAir(pos);
            }
        } else {
            if(world.isAirBlock(pos.down())) {
                ((World) world).setBlockToAir(pos);
            }
        }
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(ACTIVE) ? 0 : 1;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(ACTIVE, meta == 0);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return state.getValue(ACTIVE);
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        if(meta == 1) {
            return new TileChalice();
        }
        return null;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        if(state.getValue(ACTIVE)) {
            return new TileChalice();
        }
        return null;
    }
}
