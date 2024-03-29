/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import com.google.common.collect.Sets;
import hellfirepvp.astralsorcery.common.item.base.TypeEnchantableItem;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ToolType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalPickaxe
 * Created by HellFirePvP
 * Date: 17.08.2019 / 18:03
 */
public class ItemCrystalPickaxe extends ItemCrystalTierItem implements TypeEnchantableItem {

    public ItemCrystalPickaxe() {
        super(ToolType.PICKAXE, new Properties(), Sets.newHashSet(Material.ROCK, Material.IRON, Material.ANVIL));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> stacks) {
        if (this.isInGroup(group)) {
            ItemStack stack = new ItemStack(this);
            CrystalPropertiesAS.CREATIVE_CRYSTAL_TOOL_ATTRIBUTES.store(stack);
            stacks.add(stack);
        }
    }

    @Override
    public boolean canEnchantItem(ItemStack stack, EnchantmentType type) {
        return type == EnchantmentType.BREAKABLE || type == EnchantmentType.DIGGER;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        EnchantmentType type = enchantment.type;
        return type == EnchantmentType.DIGGER || type == EnchantmentType.BREAKABLE;
    }

    @Override
    double getAttackDamage() {
        return 5;
    }

    @Override
    double getAttackSpeed() {
        return -1;
    }
}
