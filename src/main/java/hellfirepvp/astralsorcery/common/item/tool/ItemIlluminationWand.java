/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.block.BlockTranslucentBlock;
import hellfirepvp.astralsorcery.common.item.ItemAlignmentChargeConsumer;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.tile.TileTranslucent;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemIlluminationWand
 * Created by HellFirePvP
 * Date: 17.01.2017 / 15:09
 */
public class ItemIlluminationWand extends Item implements ItemAlignmentChargeConsumer {

    public ItemIlluminationWand() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote) {
            IBlockState at = worldIn.getBlockState(pos);
            if(playerIn.isSneaking()) {
                IBlockState iblockstate = worldIn.getBlockState(pos);
                Block block = iblockstate.getBlock();
                if (!block.isReplaceable(worldIn, pos)) {
                    pos = pos.offset(facing);
                }
                if(playerIn.canPlayerEdit(pos, facing, stack) && worldIn.canBlockBePlaced(BlocksAS.blockVolatileLight, pos, false, facing, null, stack)) {
                    if (worldIn.setBlockState(pos, BlocksAS.blockVolatileLight.getDefaultState(), 3)) {
                        SoundType soundtype = worldIn.getBlockState(pos).getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, playerIn);
                        worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                        --stack.stackSize;
                    }
                }
            } else {
                if(at.isNormalCube()) {
                    TileEntity te = worldIn.getTileEntity(pos);
                    if(te == null && !at.getBlock().hasTileEntity(at)) {
                        worldIn.setBlockState(pos, BlocksAS.translucentBlock.getDefaultState(), 3);
                        TileTranslucent tt = MiscUtils.getTileAt(worldIn, pos, TileTranslucent.class, true);
                        if(tt == null) {
                            worldIn.setBlockState(pos, at, 3);
                        } else {
                            tt.setFakedState(at);
                        }
                    }
                } else if(at.getBlock() instanceof BlockTranslucentBlock) {
                    TileTranslucent tt = MiscUtils.getTileAt(worldIn, pos, TileTranslucent.class, true);
                    if(tt != null && tt.getFakedState() != null) {
                        worldIn.setBlockState(pos, tt.getFakedState(), 3);
                    }
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }

}
