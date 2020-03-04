/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import hellfirepvp.astralsorcery.common.data.sync.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.sync.client.ClientTimeFreezeEffects;
import hellfirepvp.astralsorcery.common.util.time.TimeStopEffectHelper;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;

import java.util.EnumSet;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TimeStopEffectClientHandler
 * Created by HellFirePvP
 * Date: 22.02.2020 / 20:56
 */
public class TimeStopEffectHandler implements ITickHandler {

    public static final TimeStopEffectHandler INSTANCE = new TimeStopEffectHandler();

    private TimeStopEffectHandler() {}

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        if (Minecraft.getInstance().world == null) {
            return;
        }

        SyncDataHolder.executeClient(SyncDataHolder.DATA_TIME_FREEZE_EFFECTS, ClientTimeFreezeEffects.class, effects -> {
            List<TimeStopEffectHelper> zoneEffects = effects.getTimeStopEffects(Minecraft.getInstance().world);
            zoneEffects.forEach(TimeStopEffectHelper::playClientTickEffect);
        });
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
        return "TimeStop EffectHandler";
    }
}
