/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.handler;

import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraftforge.event.TickEvent;

import java.util.EnumSet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectUpdater
 * Created by HellFirePvP
 * Date: 30.05.2019 / 13:46
 */
public class EffectUpdater implements ITickHandler {

    private static EffectUpdater INSTANCE = new EffectUpdater();

    public static EffectUpdater getInstance() {
        return INSTANCE;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        EffectHandler.getInstance().tick();
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.CLIENT);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "EffectUpdater";
    }
}
