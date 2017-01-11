/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.block.network.BlockStarlightNetwork;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileTreeBeacon;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTreeBeacon
 * Created by HellFirePvP
 * Date: 30.12.2016 / 13:26
 */
public class BlockTreeBeacon extends BlockStarlightNetwork {

    private static final AxisAlignedBB box = new AxisAlignedBB(3D / 16D, 0D, 3D / 16D, 13D / 16D, 1D, 13D / 16D);

    public BlockTreeBeacon() {
        super(Material.ROCK, MapColor.QUARTZ);
        setHardness(1.0F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe", 1);
        setSoundType(SoundType.PLANT);
        setLightLevel(0.7F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return box;
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
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileTreeBeacon();
    }

}
