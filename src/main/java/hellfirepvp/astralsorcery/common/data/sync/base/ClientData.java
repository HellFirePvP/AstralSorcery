/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.base;

import net.minecraft.world.dimension.DimensionType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientData
 * Created by HellFirePvP
 * Date: 27.08.2019 / 06:22
 */
public abstract class ClientData<C extends ClientData<C>> {

    public abstract void clear(DimensionType dimType);

    public abstract void clearClient();
}
