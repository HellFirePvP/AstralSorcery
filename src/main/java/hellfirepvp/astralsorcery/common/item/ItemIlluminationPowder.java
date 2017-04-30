/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.entities.EntityIlluminationSpark;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemIlluminationPowder
 * Created by HellFirePvP
 * Date: 07.04.2017 / 23:04
 */
public class ItemIlluminationPowder extends Item {

    public ItemIlluminationPowder() {
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World worldIn, EntityPlayer player, EnumHand hand) {
        if (stack == null || stack.getItem() == null || worldIn.isRemote) {
            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
        }
        worldIn.spawnEntityInWorld(new EntityIlluminationSpark(worldIn, player));
        if(!player.isCreative()) {
            stack.stackSize--;
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (stack == null || stack.getItem() == null || worldIn.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();
        if (!block.isReplaceable(worldIn, pos)) {
            pos = pos.offset(facing);
        }
        if(playerIn.canPlayerEdit(pos, facing, stack) && worldIn.canBlockBePlaced(BlocksAS.blockVolatileLight, pos, true, facing, playerIn, stack)) {
            if (worldIn.setBlockState(pos, BlocksAS.blockVolatileLight.getDefaultState(), 3)) {
                SoundType soundtype = worldIn.getBlockState(pos).getBlock().getSoundType(worldIn.getBlockState(pos), worldIn, pos, playerIn);
                worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                if(!playerIn.isCreative()) {
                    stack.stackSize--;
                }
                if(stack.stackSize <= 0) {
                    playerIn.setHeldItem(hand, null);
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }

}
