/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.base;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CustomItemBlockProperties
 * Created by HellFirePvP
 * Date: 01.06.2019 / 14:23
 */
public interface CustomItemBlockProperties extends CustomItemBlock {

    default int getItemMaxStackSize() {
        return getItemMaxDamage() > 0 ? 1 : 64;
    }

    default int getItemMaxDamage() {
        return 0;
    }

    @Nullable
    default Item getContainerItem() {
        return null;
    }

    @Nullable
    default ItemGroup getItemGroup() {
        return null;
    }

    @Nonnull
    default Rarity getItemRarity() {
        return Rarity.COMMON;
    }

    default boolean canItemBeRepaired() {
        return false;
    }

    @Nonnull
    default Map<ToolType, Integer> getItemToolLevels() {
        return Collections.emptyMap();
    }

    @Nullable
    default Supplier<Callable<ItemStackTileEntityRenderer>> getItemTEISR() {
        return null;
    }

}
