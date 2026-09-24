/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import hellfirepvp.astralsorcery.common.component.AttunedConstellationComponent;
import hellfirepvp.astralsorcery.common.component.ConstellationPaperComponent;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileDataConstellationContainer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface TileDataConstellationContainer {

    default <T extends TileEntitySynchronized.Data> T self() {
        return MiscUtil.cast(this);
    }

    default Supplier<? extends DataComponentType<? extends ConstellationPaperComponent>> getComponent() {
        return DataComponentsAS.ATTUNED_CONSTELLATION;
    }

    default void setAsComponent(ItemStack stack) {
        stack.set(DataComponentsAS.ATTUNED_CONSTELLATION, new AttunedConstellationComponent(this.getConstellation().orElse(null)));
    }

    default void markForUpdate() {
        this.self().markForUpdate();
    }

    default void markDirty() {
        this.self().markDirty();
    }

    Optional<BaseConstellation> getConstellation();

    void setConstellation(@Nullable BaseConstellation cst);
}
