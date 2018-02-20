/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileStructuralConnector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStructural
 * Created by HellFirePvP
 * Date: 30.07.2016 / 21:50
 */
public class BlockStructural extends BlockContainer implements BlockCustomName, BlockVariants {

    public static PropertyEnum<BlockType> BLOCK_TYPE = PropertyEnum.create("blocktype", BlockType.class);

    public BlockStructural() {
        super(Material.BARRIER, MapColor.AIR);
        setBlockUnbreakable();
        setSoundType(SoundType.GLASS);
    }

    @Override
    public SoundType getSoundType(IBlockState state, World world, BlockPos pos, @Nullable Entity entity) {
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                IBlockState downState = world.getBlockState(pos.down());
                return BlockType.TELESCOPE_STRUCT.getSupportedState().getBlock().getSoundType(downState, world, pos, entity);
            //case ATTUNEMENT_ALTAR_STRUCT:
            //    downState = world.getBlockState(pos.down());
            //    return BlockType.ATTUNEMENT_ALTAR_STRUCT.getSupportedState().getBlock().getSoundType(downState, world, pos, entity);
        }
        return super.getSoundType(state, world, pos, entity);
    }

    @Override
    public String getHarvestTool(IBlockState state) {
        return state.getValue(BLOCK_TYPE).getSupportedState().getBlock().getHarvestTool(state.getValue(BLOCK_TYPE).getSupportedState());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        IBlockState state = world.getBlockState(pos);
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                BlockType.TELESCOPE_STRUCT.getSupportedState().getBlock().addDestroyEffects(world, pos.down(), manager);
                return true;
            //case ATTUNEMENT_ALTAR_STRUCT:
            //    BlockType.ATTUNEMENT_ALTAR_STRUCT.getSupportedState().getBlock().addDestroyEffects(world, pos.down(), manager);
            //    return true;
        }
        return super.addDestroyEffects(world, pos, manager);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                return new AxisAlignedBB(0, -1, 0, 1, 1, 1);
            //case ATTUNEMENT_ALTAR_STRUCT:
            //    return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
        }
        return FULL_BLOCK_AABB;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (BlockType bt : BlockType.values()) {
            list.add(new ItemStack(this, 1, bt.ordinal()));
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                return BlockType.TELESCOPE_STRUCT.getSupportedState().getBlock().onBlockActivated(worldIn, pos.down(), BlockType.TELESCOPE_STRUCT.getSupportedState(), playerIn, hand, facing, hitX, hitY, hitZ);
            //case ATTUNEMENT_ALTAR_STRUCT:
            //    return BlockType.ATTUNEMENT_ALTAR_STRUCT.getSupportedState().getBlock().onBlockActivated(worldIn, pos.down(), BlockType.ATTUNEMENT_ALTAR_STRUCT.getSupportedState(), playerIn, hand, heldItem, side, hitX, hitY, hitZ);
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> out = new LinkedList<>();
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                out.add(BlockMachine.MachineType.TELESCOPE.asStack());
                break;
            ///case ATTUNEMENT_ALTAR_STRUCT:
            ///    out.add(new ItemStack(BlocksAS.attunementAltar));
            ///    break;
        }
        return out;
    }

    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        switch (blockState.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                return BlockType.TELESCOPE_STRUCT.getSupportedState().getBlockHardness(worldIn, pos.down());
            //case ATTUNEMENT_ALTAR_STRUCT:
            //    return BlockType.ATTUNEMENT_ALTAR_STRUCT.getSupportedState().getBlockHardness(worldIn, pos.down());
        }
        return super.getBlockHardness(blockState, worldIn, pos);
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        IBlockState state = world.getBlockState(pos);
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                return BlockType.TELESCOPE_STRUCT.getSupportedState().getBlockHardness(world, pos.down());
            //case ATTUNEMENT_ALTAR_STRUCT:
            //    return BlockType.ATTUNEMENT_ALTAR_STRUCT.getSupportedState().getBlockHardness(world, pos.down());
        }
        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        state = world.getBlockState(pos);
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                return BlockType.TELESCOPE_STRUCT.getSupportedState().getBlock().getPickBlock(BlockType.TELESCOPE_STRUCT.getSupportedState(), target, world, pos.down(), player);
            //case ATTUNEMENT_ALTAR_STRUCT:
            //    return BlockType.ATTUNEMENT_ALTAR_STRUCT.getSupportedState().getBlock().getPickBlock(BlockType.ATTUNEMENT_ALTAR_STRUCT.getSupportedState(), target, world, pos.down(), player);
        }
        return super.getPickBlock(state, target, world, pos, player);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                if(world.isAirBlock(pos.down())) {
                    world.setBlockToAir(pos);
                }
                break;
            //case ATTUNEMENT_ALTAR_STRUCT:
            //    if(world.isAirBlock(pos.down())) {
            //        world.setBlockToAir(pos);
            //    }
            //    break;
        }
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        if(!(world instanceof World)) {
            super.onNeighborChange(world, pos, neighbor);
            return;
        }
        IBlockState state = world.getBlockState(pos);
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                if(world.isAirBlock(pos.down())) {
                    ((World) world).setBlockToAir(pos);
                }
                break;
            //case ATTUNEMENT_ALTAR_STRUCT:
            //    if(world.isAirBlock(pos.down())) {
            //        ((World) world).setBlockToAir(pos);
            //    }
            //    break;
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return meta < BlockType.values().length ? getDefaultState().withProperty(BLOCK_TYPE, BlockType.values()[meta]) : getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        BlockType type = state.getValue(BLOCK_TYPE);
        return type.ordinal();
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
    @SideOnly(Side.CLIENT)
    public boolean isTranslucent(IBlockState state) {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return state.getValue(BLOCK_TYPE).getSupportedState().isOpaqueCube();
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return state.getValue(BLOCK_TYPE).getSupportedState().isFullCube();
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        switch (state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                return BlockType.TELESCOPE_STRUCT.getSupportedState().isNormalCube();
            //case ATTUNEMENT_ALTAR_STRUCT:
            //    return BlockType.ATTUNEMENT_ALTAR_STRUCT.getSupportedState().isNormalCube();
        }
        return super.isNormalCube(state);
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        switch (base_state.getValue(BLOCK_TYPE)) {
            case TELESCOPE_STRUCT:
                return BlockType.TELESCOPE_STRUCT.getSupportedState().isSideSolid(world, pos.down(), side);
            //case ATTUNEMENT_ALTAR_STRUCT:
            //    return BlockType.ATTUNEMENT_ALTAR_STRUCT.getSupportedState().isSideSolid(world, pos.down(), side);
        }
        return super.isSideSolid(base_state, world, pos, side);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public String getIdentifierForMeta(int meta) {
        BlockType mt = getStateFromMeta(meta).getValue(BLOCK_TYPE);
        return mt.getName();
    }

    @Override
    public List<IBlockState> getValidStates() {
        List<IBlockState> li = new ArrayList<>(BlockType.values().length);
        for (BlockType bt : BlockType.values()) li.add(getDefaultState().withProperty(BLOCK_TYPE, bt));
        return li;
    }

    /*@Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn) {
        state = worldIn.getBlockState(pos);
        BlockType bt = state.getValue(BLOCK_TYPE);
        if(bt.equals(BlockType.ATTUNEMENT_ALTAR_STRUCT)) return;

        super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn);
    }*/

    @Override
    public String getStateName(IBlockState state) {
        return state.getValue(BLOCK_TYPE).getName();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileStructuralConnector();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileStructuralConnector();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    public static enum BlockType implements IStringSerializable {

        TELESCOPE_STRUCT(BlocksAS.blockMachine.getDefaultState().withProperty(BlockMachine.MACHINE_TYPE, BlockMachine.MachineType.TELESCOPE));
        //ATTUNEMENT_ALTAR_STRUCT(BlocksAS.attunementAltar.getDefaultState());

        private final IBlockState supportedState;

        private BlockType(IBlockState supportedState) {
            this.supportedState = supportedState;
        }

        public IBlockState getSupportedState() {
            return supportedState;
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

    }

}
