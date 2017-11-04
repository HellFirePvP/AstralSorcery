/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.item.ItemBoreUpgrade;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileBore;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockBore
 * Created by HellFirePvP
 * Date: 03.11.2017 / 14:49
 */
public class BlockBore extends BlockContainer {

    public static final PropertyEnum<TileBore.BoreType> BORE_TYPE = PropertyEnum.create("type", TileBore.BoreType.class);

    public BlockBore() {
        super(Material.WOOD, MapColor.GOLD);
        setDefaultState(this.blockState.getBaseState().withProperty(BORE_TYPE, TileBore.BoreType.NONE));
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote) {
            TileBore tb = MiscUtils.getTileAt(worldIn, pos, TileBore.class, true);
            ItemStack held = playerIn.getHeldItem(hand);
            if(tb != null && (held.isEmpty() || held.getItem() instanceof ItemBoreUpgrade)) {
                TileBore.BoreType prev = tb.trySetBoreUpgrade(held);
                if(!held.isEmpty() && !playerIn.isCreative()) {
                    held.shrink(1);
                }
                if(prev != null && prev != TileBore.BoreType.NONE) {
                    ItemUtils.dropItem(worldIn, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
                            new ItemStack(ItemsAS.boreUpgrade, 1, prev.ordinal()));
                }
            }
        }
        return true;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(BORE_TYPE,
                TileBore.BoreType.values()[MathHelper.clamp(meta, 0, TileBore.BoreType.values().length - 1)]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BORE_TYPE).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BORE_TYPE);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileBore();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }


}
