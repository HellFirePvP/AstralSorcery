/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.ingredient;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.IngredientsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: HasStoredLumenIngredient
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class HasStoredLumenIngredient implements ICustomIngredient {

    public static final HasStoredLumenIngredient INSTANCE = new HasStoredLumenIngredient();
    public static final MapCodec<HasStoredLumenIngredient> CODEC = MapCodec.unit(INSTANCE);

    private static ItemStack displayStack = null;

    private HasStoredLumenIngredient() {}

    @Override
    public boolean test(ItemStack stack) {
        if (stack.equals(displayStack)) return true; //Hard-test against specifically created display objects
        return !stack.getOrDefault(DataComponentsAS.STORED_LUMEN, StoredLumenComponent.EMPTY).isEmpty();
    }

    @Override
    public Stream<ItemStack> getItems() {
        if (displayStack != null) return Stream.of(displayStack);

        displayStack = ItemsAS.STARDUST.toStack();
        displayStack.set(DataComponents.ITEM_NAME, Component.translatable("ingredient.astralsorcery.has_stored_lumen.description"));

        return Stream.of(displayStack);
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        return IngredientsAS.STORED_LUMEN.get();
    }

    public static void clearDisplayCache() {
        displayStack = null;
    }
}
