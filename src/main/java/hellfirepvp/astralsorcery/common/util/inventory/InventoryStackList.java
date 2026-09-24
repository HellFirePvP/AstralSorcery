/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.inventory;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.data.MapStream;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: InventoryStackList
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class InventoryStackList implements Iterable<ItemStack> {

    public static final Codec<InventoryStackList> CODEC = Codec.unboundedMap(CodecUtil.stringInteger(), ItemStack.CODEC)
            .xmap(InventoryStackList::new, InventoryStackList::getCleanSlotMap);

    private final Map<Integer, ItemStack> slotMap = new HashMap<>();

    private InventoryStackList(Map<Integer, ItemStack> slotMap) {
        this.slotMap.putAll(slotMap);
    }

    public static InventoryStackList create() {
        return new InventoryStackList(new HashMap<>());
    }

    public ItemStack getStackInSlot(int slot) {
        return this.slotMap.getOrDefault(slot, ItemStack.EMPTY);
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            this.slotMap.remove(slot);
        } else {
            this.slotMap.put(slot, stack);
        }
    }

    @Override
    @Nonnull
    public Iterator<ItemStack> iterator() {
        return this.slotMap.values().iterator();
    }

    private Map<Integer, ItemStack> getCleanSlotMap() {
        return MapStream.of(this.slotMap)
                .filterValue(stack -> !stack.isEmpty())
                .toMap();
    }
}
