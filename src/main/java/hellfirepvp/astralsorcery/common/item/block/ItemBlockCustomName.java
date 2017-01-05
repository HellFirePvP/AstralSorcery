/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.block.BlockCustomName;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemBlockMultiState
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:30
 */
public class ItemBlockCustomName extends ItemBlock {

    public ItemBlockCustomName(Block block) {
        super(block);
        setHasSubtypes(true); //Normally the case if you're using multi-type blocks.
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        Block b = getBlock();
        if (b instanceof BlockCustomName) {
            String identifier = ((BlockCustomName) b).getIdentifierForMeta(stack.getItemDamage());
            return super.getUnlocalizedName(stack) + "." + identifier;
        }
        return super.getUnlocalizedName(stack);
    }
}
