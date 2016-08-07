package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.block.network.BlockStarlightNetwork;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalLens;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTest
 * Created by HellFirePvP
 * Date: 07.08.2016 / 22:31
 */
public class BlockTest extends BlockStarlightNetwork {

    public BlockTest() {
        super(Material.ROCK, MapColor.BLACK);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCrystalLens();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileCrystalLens te = MiscUtils.getTileAt(worldIn, pos, TileCrystalLens.class);
        if(te == null) return;
        te.onPlace(CrystalProperties.getMaxProperties());
    }
}
