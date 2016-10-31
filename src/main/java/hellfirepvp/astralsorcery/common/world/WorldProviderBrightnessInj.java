package hellfirepvp.astralsorcery.common.world;

import hellfirepvp.astralsorcery.common.constellation.CelestialHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderSurface;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldProviderBrightnessInj
 * Created by HellFirePvP
 * Date: 14.05.2016 / 23:32
 */
public class WorldProviderBrightnessInj extends WorldProviderSurface {

    @Override
    public float getSunBrightness(float parTicks) {
        if (CelestialHandler.dayOfSolarEclipse && CelestialHandler.solarEclipse) {
            float sunBr = getDefaultClientSunBrightness(parTicks);
            int eclTick = CelestialHandler.solarEclipseTick;
            if (eclTick >= CelestialHandler.SOLAR_ECLIPSE_HALF_DUR) { //fading out
                eclTick -= CelestialHandler.SOLAR_ECLIPSE_HALF_DUR;
            } else {
                eclTick = CelestialHandler.SOLAR_ECLIPSE_HALF_DUR - eclTick;
            }
            float perc = ((float) eclTick) / CelestialHandler.SOLAR_ECLIPSE_HALF_DUR;
            return sunBr * (0.15F + (0.85F * perc));
        }
        return getDefaultClientSunBrightness(parTicks);
    }

    @Override
    public float getSunBrightnessFactor(float parTicks) {
        if (CelestialHandler.dayOfSolarEclipse && CelestialHandler.solarEclipse) {
            float sunBr = getDefaultSunBrightness(parTicks);
            int eclTick = CelestialHandler.solarEclipseTick;
            if (eclTick >= CelestialHandler.SOLAR_ECLIPSE_HALF_DUR) { //fading out
                eclTick -= CelestialHandler.SOLAR_ECLIPSE_HALF_DUR;
            } else {
                eclTick = CelestialHandler.SOLAR_ECLIPSE_HALF_DUR - eclTick;
            }
            float perc = ((float) eclTick) / CelestialHandler.SOLAR_ECLIPSE_HALF_DUR;
            return sunBr * (0.15F + (0.85F * perc));
        }
        return getDefaultSunBrightness(parTicks);
    }

    private float getDefaultClientSunBrightness(float parTicks) {
        float f = worldObj.getCelestialAngle(parTicks);
        float f1 = 1.0F - (MathHelper.cos(f * ((float) Math.PI * 2F)) * 2.0F + 0.2F);
        f1 = MathHelper.clamp_float(f1, 0.0F, 1.0F);
        f1 = 1.0F - f1;
        f1 = (float) ((double) f1 * (1.0D - (double) (worldObj.getRainStrength(parTicks) * 5.0F) / 16.0D));
        f1 = (float) ((double) f1 * (1.0D - (double) (worldObj.getThunderStrength(parTicks) * 5.0F) / 16.0D));
        return f1 * 0.8F + 0.2F;
    }

    private float getDefaultSunBrightness(float parTicks) {
        float f = worldObj.getCelestialAngle(parTicks);
        float f1 = 1.0F - (MathHelper.cos(f * ((float) Math.PI * 2F)) * 2.0F + 0.5F);
        f1 = MathHelper.clamp_float(f1, 0.0F, 1.0F);
        f1 = 1.0F - f1;
        f1 = (float) ((double) f1 * (1.0D - (double) (worldObj.getRainStrength(parTicks) * 5.0F) / 16.0D));
        f1 = (float) ((double) f1 * (1.0D - (double) (worldObj.getThunderStrength(parTicks) * 5.0F) / 16.0D));
        return f1;
    }

}
