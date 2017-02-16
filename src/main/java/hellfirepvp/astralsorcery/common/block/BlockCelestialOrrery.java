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
import hellfirepvp.astralsorcery.common.tile.TileCelestialOrrery;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCelestialOrrery
 * Created by HellFirePvP
 * Date: 15.02.2017 / 22:42
 */
public class BlockCelestialOrrery extends BlockStarlightNetwork {

    public BlockCelestialOrrery() {
        super(Material.ROCK, MapColor.QUARTZ);
        setHardness(0.5F);
        setHarvestLevel("axe", 1);
        setResistance(10F);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCelestialOrrery();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCelestialOrrery();
    }

}
