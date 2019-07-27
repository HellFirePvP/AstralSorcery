/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.crystal;

import hellfirepvp.astralsorcery.common.data.research.GatedKnowledge;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalProperties
 * Created by HellFirePvP
 * Date: 10.07.2019 / 21:22
 */
public class CrystalProperties {

    private static final Random rand = new Random();

    public static final int MAX_SIZE_ROCK = 400;
    public static final int MAX_SIZE_CELESTIAL = 900;
    private static final CrystalProperties MAXED_ROCK_PROPERTIES = new CrystalProperties(MAX_SIZE_ROCK, 100, 100, 0, -1);
    private static final CrystalProperties MAXED_CELESTIAL_PROPERTIES = new CrystalProperties(MAX_SIZE_CELESTIAL, 100, 100, 0, -1);

    protected int size; //(theoretically) 0 to X
    protected int purity; //0 to 100 where 100 being completely pure.
    protected int collectiveCapability; //0 to 100 where 100 being best collection rate.
    protected int fractured = 0; //0 to 100 where 100 means the crystal should shatter due to its integrity being too damaged
    protected int sizeOverride = -1; //Set to -1 = no override

    public CrystalProperties(int size, int purity, int collectiveCapability, int fractured, int sizeOverride) {
        this.size = size;
        this.purity = purity;
        this.collectiveCapability = collectiveCapability;
        this.fractured = fractured;
        this.sizeOverride = sizeOverride;
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

    public int getFracturation() {
        return fractured;
    }

    public int getSizeOverride() {
        return sizeOverride;
    }

    public static CrystalProperties readFromNBT(CompoundNBT compound) {
        CrystalProperties prop = new CrystalProperties(0, 0, 0, 0, -1);
        prop.size = compound.getInt("size");
        prop.purity = compound.getInt("purity");
        prop.collectiveCapability = compound.getInt("collect");
        prop.fractured = NBTHelper.getInteger(compound, "fract", 0);
        prop.sizeOverride = NBTHelper.getInteger(compound, "sizeOverride", -1);
        return prop;
    }

    public CompoundNBT writeToNBT(CompoundNBT compound) {
        compound.putInt("size", size);
        compound.putInt("purity", purity);
        compound.putInt("collect", collectiveCapability);
        compound.putInt("fract", fractured);
        compound.putInt("sizeOverride", sizeOverride);
        return compound;
    }

    public static CrystalProperties createStructural() {
        int size = Math.min(CrystalProperties.MAX_SIZE_ROCK, (CrystalProperties.MAX_SIZE_ROCK / 2) + rand.nextInt(CrystalProperties.MAX_SIZE_ROCK / 2));
        int purity = 60 + rand.nextInt(41);
        int collect = 45 + rand.nextInt(56);
        return new CrystalProperties(size, purity, collect, 0, -1);
    }

    public static CrystalProperties createRandomRock() {
        int size = Math.max(1, (rand.nextInt(CrystalProperties.MAX_SIZE_ROCK) + rand.nextInt(CrystalProperties.MAX_SIZE_ROCK)) / 2);
        int purity = (rand.nextInt(101) + rand.nextInt(101)) / 2;
        int collect = 5 + rand.nextInt(26);
        return new CrystalProperties(size, purity, collect, 0, -1);
    }

    public static CrystalProperties createRandomCelestial() {
        int size = Math.max(1, (rand.nextInt(CrystalProperties.MAX_SIZE_CELESTIAL) + rand.nextInt(CrystalProperties.MAX_SIZE_CELESTIAL)) / 2);
        int purity = 40 + rand.nextInt(61);
        int collect = 50 + rand.nextInt(26);
        return new CrystalProperties(size, purity, collect, 0, -1);
    }

    public static CrystalProperties getMaxRockProperties() {
        return MAXED_ROCK_PROPERTIES;
    }

    public static CrystalProperties getMaxCelestialProperties() {
        return MAXED_CELESTIAL_PROPERTIES;
    }

    public static int getMaxSize(ItemStack stack) {
        CrystalProperties prop = getCrystalProperties(stack);
        if(prop != null && prop.sizeOverride != -1) {
            return prop.sizeOverride;
        }

        if(stack.isEmpty()) {
            return MAX_SIZE_ROCK;
        }

        //TODO crystal items
        //if(stack.getItem() instanceof CrystalPropertyItem) {
        //    return ((CrystalPropertyItem) stack.getItem()).getMaxSize(stack);
        //}
        //if(stack.getItem() instanceof ItemBlock) {
        //    Block b = ((ItemBlock) stack.getItem()).getBlock();
        //    if(b instanceof CrystalPropertyItem) {
        //        return ((CrystalPropertyItem) b).getMaxSize(stack);
        //    }
        //}
        return MAX_SIZE_ROCK;
    }

    @OnlyIn(Dist.CLIENT)
    public static Optional<Boolean> addPropertyTooltip(CrystalProperties prop, List<ITextComponent> tooltip, int maxSize) {
        return addPropertyTooltip(prop, tooltip, Screen.hasShiftDown(), maxSize);
    }

    @OnlyIn(Dist.CLIENT)
    public static Optional<Boolean> addPropertyTooltip(CrystalProperties prop, List<ITextComponent> tooltip, boolean extended, int maxSize) {
        return addPropertyTooltip(prop, tooltip, extended, ResearchHelper.getClientProgress().getTierReached(), maxSize);
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
    @OnlyIn(Dist.CLIENT)
    public static Optional<Boolean> addPropertyTooltip(CrystalProperties prop, List<ITextComponent> tooltip, boolean extended, ProgressionTier tier, int maxSize) {
        if (prop != null) {
            if (extended) {
                boolean missing = false;
                if(GatedKnowledge.CRYSTAL_SIZE.canSee(tier)) {
                    TextFormatting color = (prop.getSize() > maxSize ? TextFormatting.AQUA : prop.getSize() == maxSize ? TextFormatting.GOLD : TextFormatting.BLUE);
                    tooltip.add(new StringTextComponent(TextFormatting.GRAY + I18n.format("crystal.size") + ": " + color + prop.getSize()));
                } else {
                    missing = true;
                }
                if(GatedKnowledge.CRYSTAL_PURITY.canSee(tier)) {
                    TextFormatting color = (prop.getPurity() > 100 ? TextFormatting.AQUA : prop.getPurity() == 100 ? TextFormatting.GOLD : TextFormatting.BLUE);
                    tooltip.add(new StringTextComponent(TextFormatting.GRAY + I18n.format("crystal.purity") + ": " + color + prop.getPurity() + "%"));
                } else {
                    missing = true;
                }
                if(GatedKnowledge.CRYSTAL_COLLECT.canSee(tier)) {
                    TextFormatting color = (prop.getCollectiveCapability() > 100 ? TextFormatting.AQUA : prop.getCollectiveCapability() == 100 ? TextFormatting.GOLD : TextFormatting.BLUE);
                    tooltip.add(new StringTextComponent(TextFormatting.GRAY + I18n.format("crystal.collectivity") + ": " + color + prop.getCollectiveCapability() + "%"));
                } else {
                    missing = true;
                }
                if(GatedKnowledge.CRYSTAL_FRACTURE.canSee(tier) && prop.getFracturation() > 0) {
                    tooltip.add(new StringTextComponent(TextFormatting.GRAY + I18n.format("crystal.fracture") + ": " + TextFormatting.RED + prop.getFracturation() + "%"));
                }
                if (missing) {
                    tooltip.add(new StringTextComponent(TextFormatting.GRAY + I18n.format("progress.missing.knowledge")));
                }
                return Optional.of(missing);
            } else {
                tooltip.add(new StringTextComponent(TextFormatting.DARK_GRAY + TextFormatting.ITALIC.toString() + I18n.format("misc.moreInformation")));
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    @Nullable
    public CrystalProperties grindCopy(Random rand) {
        CrystalProperties copy = new CrystalProperties(size, purity, collectiveCapability, fractured, sizeOverride);
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
        copy.collectiveCapability = Math.min((collectiveCapability > 100 ? collectiveCapability : 100), collectiveCapability + collectToAdd);
        if(copy.size <= 0)
            return null;
        return copy;
    }

    public static void applyCrystalProperties(ItemStack stack, CrystalProperties properties) {
        CompoundNBT cmp = NBTHelper.getPersistentData(stack);
        CompoundNBT crystalProp = new CompoundNBT();
        crystalProp.putInt("size", properties.getSize());
        crystalProp.putInt("purity", properties.getPurity());
        crystalProp.putInt("collectiveCapability", properties.getCollectiveCapability());
        crystalProp.putInt("fract", properties.getFracturation());
        crystalProp.putInt("sizeOverride", properties.getSizeOverride());
        cmp.put("crystalProperties", crystalProp);
    }

    @Nullable
    public static CrystalProperties getCrystalProperties(ItemStack stack) {
        CompoundNBT cmp = NBTHelper.getPersistentData(stack);
        if (!cmp.contains("crystalProperties")) return null;
        CompoundNBT prop = cmp.getCompound("crystalProperties");
        int size = prop.getInt("size");
        int purity = prop.getInt("purity");
        int colCap = prop.getInt("collectiveCapability");
        int fract = prop.getInt("fract");
        int sizeOvr = NBTHelper.getInteger(prop, "sizeOverride", -1);
        return new CrystalProperties(size, purity, colCap, fract, sizeOvr);
    }

    @Override
    public String toString() {
        return "CrystalProperties={Size=" + size + ", Purity=" + purity + ",Cutting=" + collectiveCapability + ",Fractured=" + fractured + ",SizeOverride=" + sizeOverride + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CrystalProperties that = (CrystalProperties) o;
        return size == that.size &&
                purity == that.purity &&
                collectiveCapability == that.collectiveCapability &&
                fractured == that.fractured;
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, purity, collectiveCapability, fractured);
    }
    
}
