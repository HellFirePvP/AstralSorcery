/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
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

    public ToolCrystalProperties(int size, int purity, int collectiveCapability, int fracturation, int sizeOverride) {
        super(size, purity, collectiveCapability, fracturation, sizeOverride);
    }

    public static ToolCrystalProperties merge(CrystalProperties... properties) {
        return merge(Arrays.asList(properties));
    }

    public static ToolCrystalProperties merge(List<CrystalProperties> properties) {
        int totalSize = 0;
        int totalPurity = 0;
        int totalCollectivity = 0;
        int frac = 0;
        int ovr = 0;
        for (CrystalProperties c : properties) {
            totalSize += c.getSize();
            totalPurity += c.getPurity();
            totalCollectivity += c.getCollectiveCapability();
            frac += c.getFracturation();
            if(c.getSizeOverride() >= 0) {
                ovr += (c.getSizeOverride() - MAX_SIZE_CELESTIAL);
            }
        }
        if(ovr != 0) {
            ovr /= properties.size();
            ovr += MAX_SIZE_CELESTIAL * properties.size();
        } else {
            ovr = -1;
        }
        return new ToolCrystalProperties(
                totalSize,
                totalPurity / properties.size(),
                totalCollectivity / properties.size(),
                frac / properties.size(),
                ovr);
    }

    public static ToolCrystalProperties readFromNBT(NBTTagCompound compound) {
        ToolCrystalProperties prop = new ToolCrystalProperties(0, 0, 0, 0, -1);
        prop.size = compound.getInteger("size");
        prop.purity = compound.getInteger("purity");
        prop.collectiveCapability = compound.getInteger("collect");
        prop.fractured = compound.getInteger("fract");
        prop.sizeOverride = NBTHelper.getInteger(compound, "sizeOverride", -1);
        return prop;
    }

    public ToolCrystalProperties copyDamagedCutting() {
        return new ToolCrystalProperties(
                this.size,
                this.purity,
                Math.max(0, this.collectiveCapability - 1),
                this.fractured,
                this.sizeOverride);
    }

    //Return null if the tool should break during grind.
    @Nullable
    public ToolCrystalProperties grindCopy(Random rand) {
        CrystalProperties out = super.grindCopy(rand);
        if(out == null) return null;
        return new ToolCrystalProperties(out.size, out.purity, out.collectiveCapability, out.fractured, out.sizeOverride);
    }

    public float getEfficiencyMultiplier() {
        float mult = ((float) collectiveCapability) / 100F;
        return Math.max(0.05F, mult * mult);
    }

}
