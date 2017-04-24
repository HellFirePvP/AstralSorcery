/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.Sounds;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileMapDrawingTable;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.SoundUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockMapDrawingTable
 * Created by HellFirePvP
 * Date: 18.03.2017 / 17:32
 */
public class BlockMapDrawingTable extends BlockContainer {

    private static final AxisAlignedBB drawingTableBox = new AxisAlignedBB(-1.0 / 2.0, 0, -1.0 / 2.0, 3.0 / 2.0, 3.0 / 2.0, 3.0 / 2.0);

    public BlockMapDrawingTable() {
        super(Material.ROCK);
        setHardness(2F);
        setSoundType(SoundType.WOOD);
        setResistance(15F);
        setHarvestLevel("axe", 1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            ItemStack held = playerIn.getHeldItem(hand);
            if (!held.isEmpty() && held.getItem() instanceof ItemCraftingComponent && held.getItemDamage() == ItemCraftingComponent.MetaType.PARCHMENT.getMeta()) {
                TileMapDrawingTable tm = MiscUtils.getTileAt(worldIn, pos, TileMapDrawingTable.class, true);
                if (tm != null && !tm.hasParchment()) {
                    tm.addParchment();
                    worldIn.playSound(null, pos, Sounds.bookFlip, Sounds.bookFlip.getCategory(), 1F, 1F);
                    if (!playerIn.isCreative()) {
                        held.setCount(held.getCount() - 1);
                        if (held.getCount() <= 0) {
                            playerIn.setHeldItem(hand, ItemStack.EMPTY);
                        }
                    }
                }
            }
        } else {
            ItemStack held = playerIn.getHeldItem(hand);
            if (!held.isEmpty() && held.getItem() instanceof ItemCraftingComponent && held.getItemDamage() == ItemCraftingComponent.MetaType.PARCHMENT.getMeta()) {
                return true;
            }
            AstralSorcery.proxy.openGui(CommonProxy.EnumGuiId.MAP_DRAWING, playerIn, worldIn, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return drawingTableBox;
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
        return new TileMapDrawingTable();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileMapDrawingTable();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

}
