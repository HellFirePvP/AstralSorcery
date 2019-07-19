/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.common.util.fluid.handler.ICompatFluidHandler;
import hellfirepvp.astralsorcery.common.util.fluid.handler.ICompatFluidHandlerItem;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CapabilitiesAS
 * Created by HellFirePvP
 * Date: 19.07.2019 / 13:59
 */
public class CapabilitiesAS {

    private CapabilitiesAS() {}

    @CapabilityInject(ICompatFluidHandler.class)
    public static Capability<ICompatFluidHandler> FLUID_HANDLER_COMPAT = null;

    @CapabilityInject(ICompatFluidHandlerItem.class)
    public static Capability<ICompatFluidHandlerItem> FLUID_HANDLER_ITEM_COMPAT = null;

}
