package hellfirepvp.astralsorcery.common.crafting;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemHandle
 * Created by HellFirePvP
 * Date: 26.12.2016 / 15:13
 */
public class ItemHandle {

    private ItemStack applicableItem = null;
    private String oreDictName = null;

    public ItemHandle(String oreDictName) {
        this.oreDictName = oreDictName;
    }

    public ItemHandle(ItemStack matchStack) {
        this.applicableItem = ItemUtils.copyStackWithSize(matchStack, matchStack.stackSize);
    }

    public List<ItemStack> getApplicableItems() {
        if(oreDictName != null) {
            List<ItemStack> stacks = OreDictionary.getOres(oreDictName);

            List<ItemStack> out = new LinkedList<>();
            for (ItemStack oreDictIn : stacks) {
                if(oreDictIn.getItemDamage() == OreDictionary.WILDCARD_VALUE && !oreDictIn.isItemStackDamageable()) {
                    oreDictIn.getItem().getSubItems(oreDictIn.getItem(), CreativeTabs.BUILDING_BLOCKS, out);
                } else {
                    out.add(oreDictIn);
                }
            }
            return out;
        } else {
            return Lists.newArrayList(applicableItem);
        }
    }

    public Object getObjectForRecipe() {
        if(oreDictName != null) {
            return oreDictName;
        }
        return applicableItem;
    }

    @Nullable
    public String getOreDictName() {
        return oreDictName;
    }

    public boolean matchCrafting(ItemStack stack) {
        if(stack == null) return applicableItem == null;

        if(oreDictName != null) {
            for (int id : OreDictionary.getOreIDs(stack)) {
                String name = OreDictionary.getOreName(id);
                if(name != null && name.equals(oreDictName)) {
                    return true;
                }
            }
            return false;
        } else {
            return ItemUtils.stackEqualsNonNBT(applicableItem, stack);
        }
    }

}
