/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.registry.sets;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigDataSet;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidRarityEntry
 * Created by HellFirePvP
 * Date: 20.04.2019 / 10:57
 */
public class FluidRarityEntry implements ConfigDataSet {

    private final ResourceLocation fluidName;
    private final int guaranteedAmount, additionalRandomAmount, rarity;

    public FluidRarityEntry(ResourceLocation fluidName, int rarity, int guaranteedAmount, int additionalRandomAmount) {
        this.fluidName = fluidName;
        this.rarity = rarity;
        this.guaranteedAmount = guaranteedAmount;
        this.additionalRandomAmount = additionalRandomAmount;
    }

    public Fluid getFluid() {
        return ForgeRegistries.FLUIDS.getValue(this.fluidName);
    }

    public int getRarity() {
        return rarity;
    }

    public int getRandomAmount(Random rand) {
        return this.guaranteedAmount + (additionalRandomAmount > 0 ? rand.nextInt(additionalRandomAmount) : 0);
    }

    @Nonnull
    @Override
    public String serialize() {
        return fluidName + ";" + guaranteedAmount + ";" + additionalRandomAmount + ";" + rarity;
    }

    @Nullable
    public static FluidRarityEntry deserialize(String str) throws IllegalArgumentException {
        String[] split = str.split(";");
        if (split.length != 4) {
            return null;
        }
        ResourceLocation fluidName = new ResourceLocation(split[0]);
        if (ForgeRegistries.FLUIDS.getValue(fluidName) == null) {
            throw new IllegalArgumentException("Unknown Fluid: " + fluidName);
        }
        String strGAmount = split[1];
        String strRAmount = split[2];
        String strRarity = split[3];
        int guaranteed, randomAmt, rarity;
        try {
            guaranteed = Integer.parseInt(strGAmount);
            randomAmt = Integer.parseInt(strRAmount);
            rarity = Integer.parseInt(strRarity);
        } catch (NumberFormatException exc) {
            return null;
        }
        return new FluidRarityEntry(fluidName, rarity, guaranteed, randomAmt);
    }

}
