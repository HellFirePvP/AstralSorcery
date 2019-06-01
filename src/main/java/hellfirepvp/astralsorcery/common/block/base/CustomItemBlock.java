/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.base;

import hellfirepvp.astralsorcery.common.item.block.ItemBlockCustom;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomItemBlock
 * Created by HellFirePvP
 * Date: 01.06.2019 / 15:32
 */
public interface CustomItemBlock {

    default Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockCustom.class;
    }

    default ItemBlock createItemBlock(Item.Properties properties) {
        Class<?> itemBlockClass = getItemBlockClass();
        try {
            return (ItemBlock) itemBlockClass.getConstructor(Block.class, Item.Properties.class)
                    .newInstance(this, properties);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot instantiate ItemBlock!", e);
        }
    }

}
