/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.util.crystal.CrystalProperties;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ToolCrystalProperties
 * Created by HellFirePvP
 * Date: 17.08.2019 / 16:59
 */
public class ToolCrystalProperties extends CrystalProperties {

    public ToolCrystalProperties(int size, int purity, int collectiveCapability, int fractured, int sizeOverride) {
        super(size, purity, collectiveCapability, fractured, sizeOverride);
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

    public static ToolCrystalProperties readFromNBT(CompoundNBT compound) {
        ToolCrystalProperties prop = new ToolCrystalProperties(0, 0, 0, 0, -1);
        prop.size = compound.getInt("size");
        prop.purity = compound.getInt("purity");
        prop.collectiveCapability = compound.getInt("collect");
        prop.fractured = compound.getInt("fract");
        prop.sizeOverride = compound.contains("sizeOverride") ? compound.getInt("sizeOverride") : -1;
        return prop;
    }
}
