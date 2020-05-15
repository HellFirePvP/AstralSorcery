/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.input;

import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.client.PktToggleClientOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KeyDisablePerkAbilities
 * Created by HellFirePvP
 * Date: 13.05.2020 / 19:21
 */
public class KeyDisablePerkAbilities extends KeyBindingWrapper {

    public KeyDisablePerkAbilities(KeyBinding keyBinding) {
        super(keyBinding);
    }

    @Override
    public void onKeyDown() {
        if (!Minecraft.getInstance().isGamePaused()) {
            PktToggleClientOption pkt = new PktToggleClientOption(PktToggleClientOption.Option.DISABLE_PERK_ABILITIES);
            PacketChannel.CHANNEL.sendToServer(pkt);
        }
    }

    @Override
    public void onKeyUp() {}
}
