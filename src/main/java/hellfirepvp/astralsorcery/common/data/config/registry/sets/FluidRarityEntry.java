/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.registry.sets;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigDataSet;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidRarityEntry
 * Created by HellFirePvP
 * Date: 20.04.2019 / 10:57
 */
public class FluidRarityEntry implements ConfigDataSet {

    private final String fluidNameTmp;
    public final Fluid fluid;
    public final int guaranteedAmount, additionalRandomAmount, rarity;

    public FluidRarityEntry(String fluidNameTmp, int rarity, int guaranteedAmount, int additionalRandomAmount) {
        this.fluidNameTmp = fluidNameTmp;
        this.fluid = null;
        this.rarity = rarity;
        this.guaranteedAmount = guaranteedAmount;
        this.additionalRandomAmount = additionalRandomAmount;
    }

    public FluidRarityEntry(Fluid fluid, int rarity, int guaranteedAmount, int additionalRandomAmount) {
        this.fluidNameTmp = null;
        this.fluid = fluid;
        this.rarity = rarity;
        this.guaranteedAmount = guaranteedAmount;
        this.additionalRandomAmount = additionalRandomAmount;
    }

    @Nonnull
    @Override
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        if(fluid == null) {
            if(fluidNameTmp != null) {
                sb.append(fluidNameTmp);
            } else {
                sb.append("water");
            }
        } else {
            sb.append(fluid.getName());
        }
        sb.append(";").append(guaranteedAmount).append(";").append(additionalRandomAmount).append(";").append(rarity);
        return sb.toString();
    }

    @Nullable
    public static FluidRarityEntry deserialize(String str) {
        String[] split = str.split(";");
        if(split.length != 4) {
            return null;
        }
        String fluidName = split[0];
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
