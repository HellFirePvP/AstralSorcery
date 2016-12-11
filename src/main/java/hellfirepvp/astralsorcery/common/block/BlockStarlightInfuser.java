package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.common.block.network.BlockStarlightNetwork;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileStarlightInfuser;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockStarlightInfuser
 * Created by HellFirePvP
 * Date: 11.12.2016 / 17:05
 */
public class BlockStarlightInfuser extends BlockStarlightNetwork {

    public BlockStarlightInfuser() {
        super(Material.ROCK, MapColor.QUARTZ);
        setHardness(1.0F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe", 1);
        setSoundType(SoundType.STONE);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote && hand.equals(EnumHand.MAIN_HAND)) {
            TileStarlightInfuser infuser = MiscUtils.getTileAt(worldIn, pos, TileStarlightInfuser.class, true);
            if(infuser != null) {
                infuser.onInteract(playerIn, hand, heldItem);
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if(!worldIn.isRemote) {
            TileStarlightInfuser infuser = MiscUtils.getTileAt(worldIn, pos, TileStarlightInfuser.class, true);
            if(infuser != null && infuser.getInputStack() != null) {
                ItemUtils.dropItemNaturally(worldIn,
                        infuser.getPos().getX() + 0.5,
                        infuser.getPos().getY() + 0.9,
                        infuser.getPos().getZ() + 0.5,
                        infuser.getInputStack());
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileStarlightInfuser();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileStarlightInfuser();
    }
}
