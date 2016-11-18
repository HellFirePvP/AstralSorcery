package hellfirepvp.astralsorcery.common.util.data;

import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TaskTimeoutMap
 * Created by HellFirePvP
 * Date: 17.11.2016 / 22:58
 */
public class TaskTimeoutMap extends TimeoutList<Runnable> {

    public TaskTimeoutMap(@Nonnull TickEvent.Type first, @Nonnull TickEvent.Type... types) {
        super(Runnable::run, first, types);
    }

}
