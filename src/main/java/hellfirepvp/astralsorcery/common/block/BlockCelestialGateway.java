package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.GatewayCache;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCelestialGateway
 * Created by HellFirePvP
 * Date: 16.04.2017 / 20:19
 */
public class BlockCelestialGateway extends BlockContainer {

    public BlockCelestialGateway() {
        super(Material.ROCK, MapColor.BLACK);
        setSoundType(SoundType.STONE);
        setHardness(4F);
        setResistance(40F);
        setHarvestLevel("pickaxe", 2);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);

        GatewayCache cache = WorldCacheManager.getOrLoadData(worldIn, WorldCacheManager.SaveKey.GATEWAY_DATA);
        cache.removePosition(worldIn, pos);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean hasTileEntity() {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCelestialGateway();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCelestialGateway();
    }

}
