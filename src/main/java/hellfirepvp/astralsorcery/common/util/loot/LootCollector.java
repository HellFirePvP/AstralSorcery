/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.loot;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LootCollector
 * Created by HellFirePvP
 * Date: 08.05.2020 / 17:12
 */
public class LootCollector implements Consumer<ItemStack> {

    private final Consumer<ItemStack> chaining;
    private List<ItemStack> collectedOutput = new ArrayList<>();

    public LootCollector(Consumer<ItemStack> chaining) {
        this.chaining = chaining;
    }

    @Override
    public void accept(ItemStack stack) {
        this.collectedOutput.add(stack);
    }

    public List<ItemStack> getCollectedOutput() {
        return Collections.unmodifiableList(this.collectedOutput);
    }

    public void setCollectedOutput(List<ItemStack> collectedOutput) {
        this.collectedOutput = collectedOutput;
    }

    public void flush() {
        this.getCollectedOutput().forEach(this.chaining);
    }
}
