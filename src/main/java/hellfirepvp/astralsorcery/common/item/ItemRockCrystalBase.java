package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.ItemNBTHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.input.Keyboard;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemRockCrystalBase
 * Created by HellFirePvP
 * Date: 08.05.2016 / 21:38
 */
public class ItemRockCrystalBase extends Item {

    public ItemRockCrystalBase() {
        setMaxStackSize(1);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        ItemStack stack = new ItemStack(this);
        applyCrystalProperties(stack, new CrystalProperties(CrystalProperties.MAX_SIZE, 100, 100));
        subItems.add(stack);
    }


    //TODO change information shown depending on progression/itemization
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        addTooltip(stack, tooltip, Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
    }

    protected void addTooltip(ItemStack stack, List<String> tooltip, boolean isShiftDown) {
        CrystalProperties prop = getCrystalProperties(stack);
        if (prop != null) {
            if (isShiftDown) {
                tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("crystal.size") + ": " + TextFormatting.BLUE + prop.getSize());
                tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("crystal.purity") + ": " + TextFormatting.BLUE + prop.getPurity() + "%");
                tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("crystal.collectivity") + ": " + TextFormatting.BLUE + prop.getCollectiveCapability() + "%");
            } else {
                tooltip.add(TextFormatting.DARK_GRAY + TextFormatting.ITALIC.toString() + I18n.translateToLocal("misc.moreInformation"));
            }
        }
    }

    public static void applyCrystalProperties(ItemStack stack, CrystalProperties properties) {
        if (!(stack.getItem() instanceof ItemRockCrystalBase)) return;

        NBTTagCompound cmp = ItemNBTHelper.getPersistentData(stack);
        NBTTagCompound crystalProp = new NBTTagCompound();
        crystalProp.setInteger("size", properties.getSize());
        crystalProp.setInteger("purity", properties.getPurity());
        crystalProp.setInteger("collectiveCapability", properties.getCollectiveCapability());
        cmp.setTag("crystalProperties", crystalProp);
    }

    public static CrystalProperties getCrystalProperties(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemRockCrystalBase)) return null;

        NBTTagCompound cmp = ItemNBTHelper.getPersistentData(stack);
        if (!cmp.hasKey("crystalProperties")) return null;
        NBTTagCompound prop = cmp.getCompoundTag("crystalProperties");
        Integer size = prop.getInteger("size");
        Integer purity = prop.getInteger("purity");
        Integer colCap = prop.getInteger("collectiveCapability");
        return new CrystalProperties(size, purity, colCap);
    }

    public static class CrystalProperties {

        public static final int MAX_SIZE = 500;

        private int size; //(theoretically) 0 to 500
        private int purity; //0 to 100 where 100 being completely pure.
        private int collectiveCapability; //0 to 100 where 100 being best collection rate.

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

    }

}
