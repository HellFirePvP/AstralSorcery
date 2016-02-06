package hellfire.astralSorcery.common.util;

import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 06.02.2016 01:41
 */
public class DateHelper {

    public static int getDay(World world) {
        return getDay(world.getWorldTime());
    }

    public static int getDay(long worldTime) {
        return (int) (worldTime / 24000L);
    }

}
