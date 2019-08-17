/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import com.google.common.collect.Sets;
import hellfirepvp.astralsorcery.common.util.crystal.CrystalProperties;
import net.minecraft.block.material.Material;
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
public class ItemCrystalPickaxe extends ItemCrystalTierItem {

    public ItemCrystalPickaxe() {
        super(3, ToolType.PICKAXE, new Properties(), Sets.newHashSet(Material.ROCK, Material.IRON, Material.ANVIL));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> stacks) {
        if (this.isInGroup(group)) {
            CrystalProperties crystal = CrystalProperties.getMaxCelestialProperties();
            ItemStack stack = new ItemStack(this);
            setToolProperties(stack, ToolCrystalProperties.merge(crystal, crystal, crystal));
            stacks.add(stack);
        }
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
