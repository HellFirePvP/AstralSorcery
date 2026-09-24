/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.init;

import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: InitBlockRenderTypes
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class InitBlockRenderTypes {

    public static void init() {
        ItemBlockRenderTypes.setRenderLayer(FluidsAS.LIQUID_STARLIGHT.getFlowing().get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(FluidsAS.LIQUID_STARLIGHT.getSource().get(), RenderType.translucent());
    }

}
