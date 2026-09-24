/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container.slot;

import hellfirepvp.astralsorcery.common.component.ConstellationPaperComponent;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ConstellationPaperSlot
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ConstellationPaperSlot extends Slot {

    public ConstellationPaperSlot(Supplier<? extends BaseConstellation> cstSupplier, int x, int y) {
        super(new EndlessContainer(cstSupplier), 0, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.is(ItemsAS.CONSTELLATION_PAPER);
    }

    private static class EndlessContainer implements Container {

        private final Supplier<ItemStack> constellationPaperSupplier;

        public EndlessContainer(Supplier<? extends BaseConstellation> cstSupplier) {
            this.constellationPaperSupplier = () -> {
                BaseConstellation cst = cstSupplier.get();
                if (cst == null) return ItemStack.EMPTY;
                ItemStack paper = ItemsAS.CONSTELLATION_PAPER.toStack();
                paper.set(DataComponentsAS.CONSTELLATION_PAPER, new ConstellationPaperComponent(cst));
                return paper;
            };
        }

        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public ItemStack getItem(int slot) {
            return this.constellationPaperSupplier.get();
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            if (amount <= 0) {
                return ItemStack.EMPTY;
            }
            return this.constellationPaperSupplier.get();
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return this.constellationPaperSupplier.get();
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
        }

        @Override
        public void setChanged() {
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public void clearContent() {

        }
    }
}
