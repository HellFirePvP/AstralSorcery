package hellfirepvp.astralsorcery.common.item.crystal;

import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
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
        int totalSize = 0;
        int totalPurity = 0;
        int totalCollectivity = 0;
        for (CrystalProperties c : properties) {
            totalSize += c.getSize();
            totalPurity += c.getPurity();
            totalCollectivity += c.getCollectiveCapability();
        }
        return new ToolCrystalProperties(totalSize, totalPurity / properties.length, totalCollectivity / properties.length);
    }

    public static ToolCrystalProperties readFromNBT(NBTTagCompound compound) {
        ToolCrystalProperties prop = new ToolCrystalProperties(0, 0, 0);
        prop.size = compound.getInteger("size");
        prop.purity = compound.getInteger("purity");
        prop.collectiveCapability = compound.getInteger("collect");
        return prop;
    }

    public int getMaxToolDamage() {
        return (int) Math.pow(size, 1.4);
    }

    //Return null if the tool should break during grind.
    @Nullable
    public ToolCrystalProperties grindCopy(Random rand) {
        ToolCrystalProperties copy = new ToolCrystalProperties(size, purity, collectiveCapability);
        float percGrinded = 0F;
        while (rand.nextInt(2) == 0 && percGrinded <= 0.1F)
            percGrinded += 0.01F;

        int sizeToRemove = Math.max(5 + rand.nextInt(5), (int) (size * percGrinded));
        int collectToAdd = Math.max(0, Math.round((100 - collectiveCapability) * percGrinded));
        copy.size = percGrinded > 0F ? size - sizeToRemove : size;
        copy.collectiveCapability = percGrinded > 0F ? Math.min(100, collectiveCapability + collectToAdd) : collectiveCapability;
        if(copy.size <= 0)
            return null;
        return copy;
    }

    public float getEfficiencyMultiplier() {
        return Math.max(0.1F, ((float) collectiveCapability) / 100F);
    }

}
