/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.ingredient;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.lib.IngredientsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: IsEnchantedIngredient
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class IsEnchantedIngredient implements ICustomIngredient {

    public static final IsEnchantedIngredient INSTANCE = new IsEnchantedIngredient();
    public static final MapCodec<IsEnchantedIngredient> CODEC = MapCodec.unit(INSTANCE);

    private static List<ItemStack> inputDisplayCache = null;

    private IsEnchantedIngredient() {}

    @Override
    public boolean test(ItemStack stack) {
        if (getDisplayCache().contains(stack)) return true; //Hard-test against specifically created display objects
        return EnchantmentHelper.hasAnyEnchantments(stack);
    }

    @Override
    public Stream<ItemStack> getItems() {
        return getDisplayCache().stream();
    }

    private static List<ItemStack> getDisplayCache() {
        if (inputDisplayCache != null) return inputDisplayCache;

        Component display = Component.translatable("ingredient.astralsorcery.is_enchanted.description");
        inputDisplayCache = BuiltInRegistries.ITEM.stream()
                .map(Item::getDefaultInstance)
                .filter(ItemStack::isEnchantable)
                .peek(stack -> stack.set(DataComponents.ITEM_NAME, display))
                .collect(MiscUtil.collectShuffledList(ArrayList::new));
        return inputDisplayCache;
    }

    public static void clearDisplayCache() {
        inputDisplayCache = null;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return IngredientsAS.IS_ENCHANTED.get();
    }
}
