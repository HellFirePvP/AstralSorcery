/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.util.MiscUtil;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileDataLumenContainer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface TileDataLumenContainer {

    default <T extends TileEntitySynchronized.Data> T self() {
        return MiscUtil.cast(this);
    }

    default void markForUpdate() {
        this.self().markForUpdate();
    }

    default void markDirty() {
        this.self().markDirty();
    }

    Lumen getLumen();

    void setLumen(Lumen lumen);
}
