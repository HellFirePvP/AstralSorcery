/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ToolCrystalProperties
 * Created by HellFirePvP
 * Date: 18.09.2016 / 12:46
 */
public class ToolCrystalProperties extends CrystalProperties {

    public ToolCrystalProperties(int size, int purity, int collectiveCapability) {
        super(size, purity, collectiveCapability);
    }

    public static ToolCrystalProperties merge(CrystalProperties... properties) {
        return merge(Arrays.asList(properties));
    }

    public static ToolCrystalProperties merge(List<CrystalProperties> properties) {
        int totalSize = 0;
        int totalPurity = 0;
        int totalCollectivity = 0;
        for (CrystalProperties c : properties) {
            totalSize += c.getSize();
            totalPurity += c.getPurity();
            totalCollectivity += c.getCollectiveCapability();
        }
        return new ToolCrystalProperties(totalSize, totalPurity / properties.size(), totalCollectivity / properties.size());
    }

    public static ToolCrystalProperties readFromNBT(NBTTagCompound compound) {
        ToolCrystalProperties prop = new ToolCrystalProperties(0, 0, 0);
        prop.size = compound.getInteger("size");
        prop.purity = compound.getInteger("purity");
        prop.collectiveCapability = compound.getInteger("collect");
        return prop;
    }

    public void damageCutting() {
        this.collectiveCapability = Math.max(0, this.collectiveCapability - 1);
    }

    //Return null if the tool should break during grind.
    @Nullable
    public ToolCrystalProperties grindCopy(Random rand) {
        CrystalProperties out = super.grindCopy(rand);
        if(out == null) return null;
        return new ToolCrystalProperties(out.size, out.purity, out.collectiveCapability);
    }

    public float getEfficiencyMultiplier() {
        float mult = ((float) collectiveCapability) / 100F;
        return Math.max(0.05F, mult * mult);
    }

}
