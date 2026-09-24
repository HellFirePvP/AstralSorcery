/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container.slot;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CustomItemSlot
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CustomItemSlot extends Slot {

    private final Supplier<ItemStack> getter;
    private final Consumer<ItemStack> setter;
    private final Predicate<ItemStack> setterFilter;
    private final Predicate<ItemStack> getterFilter;

    public CustomItemSlot(int x, int y, Supplier<ItemStack> getter, Consumer<ItemStack> setter) {
        this(x, y, getter, setter, s -> true, s -> true);
    }

    public CustomItemSlot(int x, int y, Supplier<ItemStack> getter, Consumer<ItemStack> setter, Predicate<ItemStack> setterFilter, Predicate<ItemStack> getterFilter) {
        super(new SimpleContainer(0), 0, x, y);
        this.getter = getter;
        this.setter = setter;
        this.setterFilter = setterFilter;
        this.getterFilter = getterFilter;
    }

    @Override
    public ItemStack getItem() {
        return this.getter.get();
    }

    @Override
    public void set(ItemStack stack) {
        this.setter.accept(stack);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return super.mayPlace(stack) && this.setterFilter.test(stack);
    }

    @Override
    public boolean mayPickup(Player player) {
        return super.mayPickup(player) && this.getterFilter.test(this.getItem());
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public ItemStack remove(int amount) {
        ItemStack stack = this.getItem();
        this.set(ItemStack.EMPTY);
        return stack;
    }
}
