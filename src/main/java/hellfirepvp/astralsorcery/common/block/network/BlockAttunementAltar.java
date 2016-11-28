package hellfirepvp.astralsorcery.common.block.network;

import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockAttunementAltar
 * Created by HellFirePvP
 * Date: 28.11.2016 / 10:20
 */
public class BlockAttunementAltar extends BlockStarlightNetwork {

    public BlockAttunementAltar() {
        super(Material.ROCK, MapColor.QUARTZ);
        setHardness(3.0F);
        setSoundType(SoundType.STONE);
        setResistance(25.0F);
        setHarvestLevel("pickaxe", 3);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
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

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileAttunementAltar();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileAttunementAltar();
    }

}
