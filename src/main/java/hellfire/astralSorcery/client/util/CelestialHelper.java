package hellfire.astralSorcery.client.util;

import hellfire.astralSorcery.common.util.Vector3;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 06.02.2016 13:55
 */
public class CelestialHelper {

    //minecrafts celestial angle is from [0.0-1.0[ so we have to multiply it with 2*PI to get actually valid radians
    //the "offset" for minecrafts celestial angle is sun position, angle also points to sun. 0.0 == noon

    public static Vector3 getMoonVec(World world, float partialTicks) {
        double angle = world.getCelestialAngle(partialTicks) * 2 * Math.PI;
        return new Vector3(Math.sin(angle), -Math.cos(angle), 0);
    }

    public static Vector3 getSunVec(World world, float partialTicks) {
        double angle = world.getCelestialAngle(partialTicks) * 2 * Math.PI;
        return new Vector3(-Math.sin(angle), Math.cos(angle), 0);
    }

    //actually works.
    public static boolean doesPlayerLookAtMoon(EntityPlayer player, World world, float partialTicks, boolean respectDay) {
        long partWTime = world.getWorldTime() % 24000L;
        if(respectDay && (partWTime < 13000 || partWTime > 23000)) return false;
        Vector3 plView = new Vector3(player.getLook(partialTicks));
        Vector3 moon = getMoonVec(world, partialTicks);
        return plView.angle(moon) <= 0.1;
    }

}
