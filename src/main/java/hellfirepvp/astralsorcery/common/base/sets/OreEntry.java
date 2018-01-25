/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.sets;

import hellfirepvp.astralsorcery.common.data.config.ConfigDataAdapter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OreEntry
 * Created by HellFirePvP
 * Date: 05.11.2017 / 10:28
 */
public class OreEntry implements ConfigDataAdapter.DataSet {

    public final String oreName;
    public final int weight;

    public OreEntry(String oreName, int weight) {
        this.oreName = oreName;
        this.weight = weight;
    }

    @Nonnull
    @Override
    public String serialize() {
        return oreName + ";" + weight;
    }

    @Nullable
    public static OreEntry deserialize(String str) {
        String[] spl = str.split(";");
        if(spl.length != 2) {
            return null;
        }
        String oreDict = spl[0];
        int weight;
        try {
            weight = Integer.parseInt(spl[1]);
        } catch (Exception exc) {
            return null;
        }
        return new OreEntry(oreDict, weight);
    }

}
