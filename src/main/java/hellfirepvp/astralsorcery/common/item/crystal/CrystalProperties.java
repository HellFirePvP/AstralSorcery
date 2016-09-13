package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.data.research.EnumGatedKnowledge;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.nbt.ItemNBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

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

    public static final int MAX_SIZE_ROCK = 500;
    public static final int MAX_SIZE_CELESTIAL = 800;
    private static final CrystalProperties MAXED_PROPERTIES = new CrystalProperties(MAX_SIZE_ROCK, 100, 100);

    private int size; //(theoretically) 0 to 500
    private int purity; //0 to 100 where 100 being completely pure.
    private int collectiveCapability; //0 to 100 where 100 being best collection rate.

    CrystalProperties() {}

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
        CrystalProperties prop = new CrystalProperties();
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
        int size = Math.min(CrystalProperties.MAX_SIZE_ROCK, CrystalProperties.MAX_SIZE_ROCK / 2 + rand.nextInt(CrystalProperties.MAX_SIZE_ROCK));
        int purity = 60 + rand.nextInt(41);
        int collect = 45 + rand.nextInt(56);
        return new CrystalProperties(size, purity, collect);
    }

    public static CrystalProperties createRandomRock() {
        int size = (rand.nextInt(CrystalProperties.MAX_SIZE_ROCK) + rand.nextInt(CrystalProperties.MAX_SIZE_ROCK)) / 2;
        int purity = (rand.nextInt(101) + rand.nextInt(101)) / 2;
        int collect = 5 + rand.nextInt(26);
        return new CrystalProperties(size, purity, collect);
    }

    public static CrystalProperties createRandomCelestial() {
        int size = (rand.nextInt(CrystalProperties.MAX_SIZE_CELESTIAL) + rand.nextInt(CrystalProperties.MAX_SIZE_CELESTIAL)) / 2;
        int purity = 40 + rand.nextInt(61);
        int collect = 50 + rand.nextInt(26);
        return new CrystalProperties(size, purity, collect);
    }

    public static CrystalProperties getMaxProperties() {
        return MAXED_PROPERTIES;
    }

    @SideOnly(Side.CLIENT)
    public static Optional<Boolean> addPropertyTooltip(CrystalProperties prop, List<String> tooltip) {
        return addPropertyTooltip(prop, tooltip, Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
    }

    @SideOnly(Side.CLIENT)
    public static Optional<Boolean> addPropertyTooltip(CrystalProperties prop, List<String> tooltip, boolean extended) {
        return addPropertyTooltip(prop, tooltip, extended, ResearchManager.clientProgress.getViewCapability());
    }

    @SideOnly(Side.CLIENT)
    public static Optional<Boolean> addPropertyTooltip(CrystalProperties prop, List<String> tooltip, boolean extended, EnumGatedKnowledge.ViewCapability cap) {
        if (prop != null) {
            if (extended) {
                boolean missing = false;
                if(EnumGatedKnowledge.CRYSTAL_SIZE.canSee(cap)) {
                    tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("crystal.size") + ": " + TextFormatting.BLUE + prop.getSize());
                } else {
                    missing = true;
                }
                if(EnumGatedKnowledge.CRYSTAL_PURITY.canSee(cap)) {
                    tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("crystal.purity") + ": " + TextFormatting.BLUE + prop.getPurity() + "%");
                } else {
                    missing = true;
                }
                if(EnumGatedKnowledge.CRYSTAL_COLLECT.canSee(cap)) {
                    tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("crystal.collectivity") + ": " + TextFormatting.BLUE + prop.getCollectiveCapability() + "%");
                } else {
                    missing = true;
                }
                if(missing) {
                    tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("progress.missing.knowledge"));
                }
                return Optional.of(missing);
            } else {
                tooltip.add(TextFormatting.DARK_GRAY + TextFormatting.ITALIC.toString() + I18n.translateToLocal("misc.moreInformation"));
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static void applyCrystalProperties(ItemStack stack, CrystalProperties properties) {
        NBTTagCompound cmp = ItemNBTHelper.getPersistentData(stack);
        NBTTagCompound crystalProp = new NBTTagCompound();
        crystalProp.setInteger("size", properties.getSize());
        crystalProp.setInteger("purity", properties.getPurity());
        crystalProp.setInteger("collectiveCapability", properties.getCollectiveCapability());
        cmp.setTag("crystalProperties", crystalProp);
    }

    public static CrystalProperties getCrystalProperties(ItemStack stack) {
        NBTTagCompound cmp = ItemNBTHelper.getPersistentData(stack);
        if (!cmp.hasKey("crystalProperties")) return null;
        NBTTagCompound prop = cmp.getCompoundTag("crystalProperties");
        Integer size = prop.getInteger("size");
        Integer purity = prop.getInteger("purity");
        Integer colCap = prop.getInteger("collectiveCapability");
        return new CrystalProperties(size, purity, colCap);
    }

}
