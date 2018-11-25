/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import hellfirepvp.astralsorcery.core.ASMCallHook;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RunicShieldingCalculateEvent
 * Created by HellFirePvP
 * Date: 18.11.2018 / 21:17
 */
public class RunicShieldingCalculateEvent extends PlayerEvent {

    private int value;

    public RunicShieldingCalculateEvent(EntityPlayer player, int value) {
        super(player);
        this.value = value;
    }

    public int getRunicShieldingValue() {
        return value;
    }

    public void setRunicShieldingValue(int value) {
        this.value = value;
    }

    @ASMCallHook
    public static int fire(EntityPlayer player, int value) {
        RunicShieldingCalculateEvent ev = new RunicShieldingCalculateEvent(player, value);
        MinecraftForge.EVENT_BUS.post(ev);
        return ev.getRunicShieldingValue();
    }

}
