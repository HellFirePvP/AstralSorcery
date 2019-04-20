/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.network.FMLPlayMessages;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientGuiHandler
 * Created by HellFirePvP
 * Date: 19.04.2019 / 22:22
 */
public class ClientGuiHandler implements Function<FMLPlayMessages.OpenContainer, GuiScreen> {

    @Override
    public GuiScreen apply(FMLPlayMessages.OpenContainer openContainer) {
        return null;
    }

}
