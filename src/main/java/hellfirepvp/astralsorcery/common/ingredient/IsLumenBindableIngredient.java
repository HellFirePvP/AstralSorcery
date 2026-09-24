/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.ingredient;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.component.FlagsComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.IngredientsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lumen.binding.LumenBindingType;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: IsLumenBindableIngredient
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class IsLumenBindableIngredient implements ICustomIngredient {

    public static final IsLumenBindableIngredient INSTANCE = new IsLumenBindableIngredient();
    public static final MapCodec<IsLumenBindableIngredient> CODEC = MapCodec.unit(INSTANCE);

    private static boolean buildingCache = false;
    private static List<ItemStack> inputDisplayCache = null;

    private IsLumenBindableIngredient() {}

    @Override
    public boolean test(ItemStack stack) {
        if (!buildingCache && getDisplayCache().contains(stack)) return true; //Hard-test against specifically created display objects
        if (this.isPotionLumenBindable(stack)) return true;
        return Arrays.stream(LumenBindingType.SlotType.values())
                .anyMatch(type -> type.isSlotTypeFor(stack)) ;
    }

    public boolean isPotionLumenBindable(ItemStack stack) {
        if (!stack.is(ItemsAS.STARDEW)) return false;
        if (!stack.has(DataComponents.POTION_CONTENTS)) return false;
        if (stack.getDamageValue() == stack.getMaxDamage()) return false;
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents == null) return false;
        boolean isPrismaticSet = stack.getOrDefault(DataComponentsAS.FLAGS, FlagsComponent.EMPTY).isSet(FlagsComponent.Flag.HAS_PRISMATIC_LUMEN);
        return contents.is(Potions.WATER) || (contents.potion().isEmpty() &&
                ((isPrismaticSet && contents.customEffects().size() < 2) ||
                        (!isPrismaticSet && contents.customEffects().size() < 3)));
    }

    @Override
    public Stream<ItemStack> getItems() {
        return getDisplayCache().stream();
    }

    private static List<ItemStack> getDisplayCache() {
        if (inputDisplayCache != null) return inputDisplayCache;
        buildingCache = true;

        Component display = Component.translatable("ingredient.astralsorcery.is_lumen_bindable.description")
                .withStyle(ChatFormatting.GOLD);
        inputDisplayCache = BuiltInRegistries.ITEM.stream()
                .map(Item::getDefaultInstance)
                .filter(IsLumenBindableIngredient.INSTANCE::test)
                .peek(stack -> stack.set(DataComponents.ITEM_NAME, display))
                .collect(MiscUtil.collectShuffledList(ArrayList::new));
        buildingCache = false;
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
        return IngredientsAS.LUMEN_BINDABLE.get();
    }
}
