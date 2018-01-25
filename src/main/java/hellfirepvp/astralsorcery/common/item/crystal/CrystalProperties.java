/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.data.research.EnumGatedKnowledge;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
* This class is part of the Astral Sorcery Mod
* The complete source code for this mod can be found on github.
* Class: CrystalProperties
* Created by HellFirePvP
* Date: 01.08.2016 / 22:21
*/
public class CrystalProperties {

    private static final Random rand = new Random();

    public static final int MAX_SIZE_ROCK = 400;
    public static final int MAX_SIZE_CELESTIAL = 900;
    private static final CrystalProperties MAXED_ROCK_PROPERTIES = new CrystalProperties(MAX_SIZE_ROCK, 100, 100);
    private static final CrystalProperties MAXED_CELESTIAL_PROPERTIES = new CrystalProperties(MAX_SIZE_CELESTIAL, 100, 100);

    protected int size; //(theoretically) 0 to X
    protected int purity; //0 to 100 where 100 being completely pure.
    protected int collectiveCapability; //0 to 100 where 100 being best collection rate.

    public CrystalProperties(int size, int purity, int collectiveCapability) {
        this.size = size;
        this.purity = purity;
        this.collectiveCapability = collectiveCapability;
    }

    public int getSize() {
        return size;
    }

    public int getPurity() {
        return purity;
    }

    public int getCollectiveCapability() {
        return collectiveCapability;
    }

    public static CrystalProperties readFromNBT(NBTTagCompound compound) {
        CrystalProperties prop = new CrystalProperties(0, 0, 0);
        prop.size = compound.getInteger("size");
        prop.purity = compound.getInteger("purity");
        prop.collectiveCapability = compound.getInteger("collect");
        return prop;
    }

    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger("size", size);
        compound.setInteger("purity", purity);
        compound.setInteger("collect", collectiveCapability);
    }

    public static CrystalProperties createStructural() {
        int size = Math.min(CrystalProperties.MAX_SIZE_ROCK, (CrystalProperties.MAX_SIZE_ROCK / 2) + rand.nextInt(CrystalProperties.MAX_SIZE_ROCK / 2));
        int purity = 60 + rand.nextInt(41);
        int collect = 45 + rand.nextInt(56);
        return new CrystalProperties(size, purity, collect);
    }

    public static CrystalProperties createRandomRock() {
        int size = Math.max(1, (rand.nextInt(CrystalProperties.MAX_SIZE_ROCK) + rand.nextInt(CrystalProperties.MAX_SIZE_ROCK)) / 2);
        int purity = (rand.nextInt(101) + rand.nextInt(101)) / 2;
        int collect = 5 + rand.nextInt(26);
        return new CrystalProperties(size, purity, collect);
    }

    public static CrystalProperties createRandomCelestial() {
        int size = Math.max(1, (rand.nextInt(CrystalProperties.MAX_SIZE_CELESTIAL) + rand.nextInt(CrystalProperties.MAX_SIZE_CELESTIAL)) / 2);
        int purity = 40 + rand.nextInt(61);
        int collect = 50 + rand.nextInt(26);
        return new CrystalProperties(size, purity, collect);
    }

    public static CrystalProperties getMaxRockProperties() {
        return MAXED_ROCK_PROPERTIES;
    }

    public static CrystalProperties getMaxCelestialProperties() {
        return MAXED_CELESTIAL_PROPERTIES;
    }

    @SideOnly(Side.CLIENT)
    public static Optional<Boolean> addPropertyTooltip(CrystalProperties prop, List<String> tooltip, int maxSize) {
        return addPropertyTooltip(prop, tooltip, Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54), maxSize);
    }

    @SideOnly(Side.CLIENT)
    public static Optional<Boolean> addPropertyTooltip(CrystalProperties prop, List<String> tooltip, boolean extended, int maxSize) {
        return addPropertyTooltip(prop, tooltip, extended, ResearchManager.clientProgress.getTierReached(), maxSize);
    }

    /**
     * Adds the property tooltip to the given item, depending on the properties.
     *
     * @return Optional boolean.
     *
     * Missing value = no significant information was added
     * False = The player misses some knowledge.
     * True = Everything has been displayed.
     */
    @SideOnly(Side.CLIENT)
    public static Optional<Boolean> addPropertyTooltip(CrystalProperties prop, List<String> tooltip, boolean extended, ProgressionTier tier, int maxSize) {
        if (prop != null) {
            if (extended) {
                boolean missing = false;
                if(EnumGatedKnowledge.CRYSTAL_SIZE.canSee(tier)) {
                    tooltip.add(TextFormatting.GRAY + I18n.format("crystal.size") + ": " + (prop.getSize() == maxSize ? TextFormatting.GOLD : TextFormatting.BLUE) + prop.getSize());
                } else {
                    missing = true;
                }
                if(EnumGatedKnowledge.CRYSTAL_PURITY.canSee(tier)) {
                    tooltip.add(TextFormatting.GRAY + I18n.format("crystal.purity") + ": " + (prop.getPurity() == 100 ? TextFormatting.GOLD : TextFormatting.BLUE) + prop.getPurity() + "%");
                } else {
                    missing = true;
                }
                if(EnumGatedKnowledge.CRYSTAL_COLLECT.canSee(tier)) {
                    tooltip.add(TextFormatting.GRAY + I18n.format("crystal.collectivity") + ": " + (prop.getCollectiveCapability() == 100 ? TextFormatting.GOLD : TextFormatting.BLUE) + prop.getCollectiveCapability() + "%");
                } else {
                    missing = true;
                }
                if(missing) {
                    tooltip.add(TextFormatting.GRAY + I18n.format("progress.missing.knowledge"));
                }
                return Optional.of(missing);
            } else {
                tooltip.add(TextFormatting.DARK_GRAY + TextFormatting.ITALIC.toString() + I18n.format("misc.moreInformation"));
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    @Nullable
    public CrystalProperties grindCopy(Random rand) {
        CrystalProperties copy = new CrystalProperties(size, purity, collectiveCapability);
        int grind = 7 + rand.nextInt(5);
        double purity = ((double) this.purity) / 100D;
        if(purity <= 0.4) purity = 0.4;
        for (int j = 0; j < 3; j++) {
            if (purity <= rand.nextFloat()) {
                grind += grind;
            }
        }
        int collectToAdd = 3 + rand.nextInt(4);
        copy.size = size - grind;
        copy.collectiveCapability = Math.min(100, collectiveCapability + collectToAdd);
        if(copy.size <= 0)
            return null;
        return copy;
    }

    public static void applyCrystalProperties(ItemStack stack, CrystalProperties properties) {
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        NBTTagCompound crystalProp = new NBTTagCompound();
        crystalProp.setInteger("size", properties.getSize());
        crystalProp.setInteger("purity", properties.getPurity());
        crystalProp.setInteger("collectiveCapability", properties.getCollectiveCapability());
        cmp.setTag("crystalProperties", crystalProp);
    }

    public static CrystalProperties getCrystalProperties(ItemStack stack) {
        NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
        if (!cmp.hasKey("crystalProperties")) return null;
        NBTTagCompound prop = cmp.getCompoundTag("crystalProperties");
        Integer size = prop.getInteger("size");
        Integer purity = prop.getInteger("purity");
        Integer colCap = prop.getInteger("collectiveCapability");
        return new CrystalProperties(size, purity, colCap);
    }

    @Override
    public String toString() {
        return "CrystalProperties={Size=" + size + ", Purity=" + purity + ",Cutting=" + collectiveCapability + "}";
    }
}
