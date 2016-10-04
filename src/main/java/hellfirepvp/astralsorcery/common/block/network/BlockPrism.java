package hellfirepvp.astralsorcery.common.block.network;

import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.network.TileCrystalPrismLens;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockTest2
 * Created by HellFirePvP
 * Date: 07.08.2016 / 22:37
 */
public class BlockPrism extends BlockStarlightNetwork {

    public BlockPrism() {
        super(Material.ROCK, MapColor.BLACK);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    //TODO
    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCrystalPrismLens();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileCrystalPrismLens te = MiscUtils.getTileAt(worldIn, pos, TileCrystalPrismLens.class, true);
        if(te == null) return;
        te.onPlace(CrystalProperties.getMaxRockProperties());
    }

}
