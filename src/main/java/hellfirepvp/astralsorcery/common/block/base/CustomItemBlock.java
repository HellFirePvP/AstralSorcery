/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.base;

import hellfirepvp.astralsorcery.common.item.block.ItemBlockCustom;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomItemBlock
 * Created by HellFirePvP
 * Date: 01.06.2019 / 15:32
 */
public interface CustomItemBlock {

    default Class<? extends BlockItem> getItemBlockClass() {
        return ItemBlockCustom.class;
    }

    default BlockItem createItemBlock(Item.Properties properties) {
        Class<?> itemBlockClass = getItemBlockClass();
        try {
            return (BlockItem) itemBlockClass.getConstructor(Block.class, Item.Properties.class)
                    .newInstance(this, properties);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot instantiate ItemBlock!", e);
        }
    }

}
