/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileObservatory;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockObservatory
 * Created by HellFirePvP
 * Date: 26.05.2018 / 14:32
 */
public class BlockObservatory extends BlockContainer {

    public BlockObservatory() {
        super(Material.ROCK, MapColor.GRAY);
        setHardness(3.5F);
        setHarvestLevel("pickaxe", 1);
        setResistance(3.0F);
        setSoundType(SoundType.STONE);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        BlockPos.PooledMutableBlockPos mut = BlockPos.PooledMutableBlockPos.retain();
        for (int xx = -1; xx <= 1; xx++) {
            for (int yy = 0; yy <= 3; yy++) {
                for (int zz = -1; zz <= 1; zz++) {
                    mut.setPos(pos.getX() + xx, pos.getY() + yy, pos.getZ() + zz);
                    if (!world.isAirBlock(mut) && !world.getBlockState(mut).getBlock().isReplaceable(world, mut)) {
                        mut.release();
                        return false;
                    }
                }
            }
        }
        mut.release();
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            TileObservatory to = MiscUtils.getTileAt(world, pos, TileObservatory.class, false);
            if (to != null && to.isUsable()) {
                Entity e = to.findRidableObservatoryEntity();
                if (e != null) {
                    if(player.getRidingEntity() == null) {
                        player.startRiding(e);
                    } else if(!player.getRidingEntity().equals(e)) {
                        return true;
                    }
                    player.openGui(AstralSorcery.instance, CommonProxy.EnumGuiId.OBSERVATORY.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
                }
            }
        }
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
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
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileObservatory();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileObservatory();
    }

}
