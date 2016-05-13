package hellfirepvp.astralsorcery.common.item.block;

import hellfirepvp.astralsorcery.common.block.BlockCustomName;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

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
