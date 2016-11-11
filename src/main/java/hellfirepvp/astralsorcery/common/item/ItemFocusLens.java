package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.item.base.IItemVariants;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemFocusLens
 * Created by HellFirePvP
 * Date: 02.11.2016 / 12:38
 */
public class ItemFocusLens extends Item implements IItemVariants {

    public ItemFocusLens() {
        setMaxStackSize(4);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        Item i = stack.getItem();
        if(i instanceof ItemFocusLens) {
            FocusLensType type = FocusLensType.values()[stack.getItemDamage()];
            return super.getUnlocalizedName(stack) + "." + type.getName();
        }
        return super.getUnlocalizedName(stack);
    }

    @Override
    public String[] getVariants() {
        String[] sub = new String[FocusLensType.values().length];
        FocusLensType[] values = FocusLensType.values();
        for (int i = 0; i < values.length; i++) {
            FocusLensType mt = values[i];
            sub[i] = mt.getName();
        }
        return sub;
    }

    @Override
    public int[] getVariantMetadatas() {
        int[] sub = new int[FocusLensType.values().length];
        FocusLensType[] values = FocusLensType.values();
        for (int i = 0; i < values.length; i++) {
            FocusLensType mt = values[i];
            sub[i] = mt.ordinal();
        }
        return sub;
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (FocusLensType type : FocusLensType.values()) {
            subItems.add(new ItemStack(itemIn, 1, type.ordinal()));
        }
    }

    public static enum FocusLensType implements IStringSerializable {

        TIER2,
        TIER3,
        TIER4,
        TIER5;

        public ItemStack asStack() {
            //return new ItemStack(ItemsAS.entityPlacer, 1, getMeta());
            return null;
        }

        public int getMeta() {
            return ordinal();
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }

    }

}
