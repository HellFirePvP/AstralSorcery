/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerRedirect
 * Created by HellFirePvP
 * Date: 07.05.2016 / 02:51
 */
public class EventHandlerRedirect {

    /*

    public static float getWorldBrightness(World world, float parTicks) {
        return getWorldBrightnessRedirect(world, parTicks);
    }

    public static float getClientWorldBrightness(World world, float parTicks) {
        return getClientWorldBrightnessRedirect(world, parTicks);
    }

    public static float getClientWorldBrightnessRedirect(World world, float parTicks) {
        if (CelestialHandler.dayOfSolarEclipse && CelestialHandler.solarEclipse) {
            float sunBr = getDefaultClientSunBrightness(world, parTicks);
            int eclTick = CelestialHandler.solarEclipseTick;
            if (eclTick >= CelestialHandler.SOLAR_ECLIPSE_HALF_DUR) { //fading out
                eclTick -= CelestialHandler.SOLAR_ECLIPSE_HALF_DUR;
            } else {
                eclTick = CelestialHandler.SOLAR_ECLIPSE_HALF_DUR - eclTick;
            }
            float perc = ((float) eclTick) / CelestialHandler.SOLAR_ECLIPSE_HALF_DUR;
            return sunBr * (0.15F + (0.85F * perc));
        }
        return getDefaultClientSunBrightness(world, parTicks);
    }

    private static float getDefaultClientSunBrightness(World world, float parTicks) {
        float f = world.getCelestialAngle(parTicks);
        float f1 = 1.0F - (MathHelper.cos(f * ((float) Math.PI * 2F)) * 2.0F + 0.2F);
        f1 = MathHelper.clamp_float(f1, 0.0F, 1.0F);
        f1 = 1.0F - f1;
        f1 = (float) ((double) f1 * (1.0D - (double) (world.getRainStrength(parTicks) * 5.0F) / 16.0D));
        f1 = (float) ((double) f1 * (1.0D - (double) (world.getThunderStrength(parTicks) * 5.0F) / 16.0D));
        return f1 * 0.8F + 0.2F;
    }

    public static float getWorldBrightnessRedirect(World world, float parTicks) {
        if (CelestialHandler.dayOfSolarEclipse && CelestialHandler.solarEclipse) {
            float sunBr = getDefaultSunBrightness(world, parTicks);
            int eclTick = CelestialHandler.solarEclipseTick;
            if (eclTick >= CelestialHandler.SOLAR_ECLIPSE_HALF_DUR) { //fading out
                eclTick -= CelestialHandler.SOLAR_ECLIPSE_HALF_DUR;
            } else {
                eclTick = CelestialHandler.SOLAR_ECLIPSE_HALF_DUR - eclTick;
            }
            float perc = ((float) eclTick) / CelestialHandler.SOLAR_ECLIPSE_HALF_DUR;
            return sunBr * (0.15F + (0.85F * perc));
        }
        return getDefaultSunBrightness(world, parTicks);
    }

    private static float getDefaultSunBrightness(World world, float parTicks) {
        float f = world.getCelestialAngle(parTicks);
        float f1 = 1.0F - (MathHelper.cos(f * ((float) Math.PI * 2F)) * 2.0F + 0.5F);
        f1 = MathHelper.clamp_float(f1, 0.0F, 1.0F);
        f1 = 1.0F - f1;
        f1 = (float) ((double) f1 * (1.0D - (double) (world.getRainStrength(parTicks) * 5.0F) / 16.0D));
        f1 = (float) ((double) f1 * (1.0D - (double) (world.getThunderStrength(parTicks) * 5.0F) / 16.0D));
        return f1;
    }*/

}
