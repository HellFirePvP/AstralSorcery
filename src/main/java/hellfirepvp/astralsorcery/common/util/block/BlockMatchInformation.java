/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockMatchInformation
 * Created by HellFirePvP
 * Date: 13.10.2019 / 10:07
 */
public class BlockMatchInformation implements Predicate<BlockState> {

    private final BlockState matchState;
    private final ItemStack display;
    private final boolean matchExact;

    public BlockMatchInformation(BlockState matchState, boolean matchExact) {
        this.matchState = matchState;
        this.display = new ItemStack(matchState.getBlock());
        this.matchExact = matchExact;

        if (this.display.isEmpty()) {
            throw new IllegalArgumentException("No display ItemStack passed, and " + matchState.getBlock().getRegistryName() + " has no associated ItemBlock!");
        }
    }

    public BlockMatchInformation(BlockState matchState, ItemStack display, boolean matchExact) {
        this.matchState = matchState;
        this.display = display;
        this.matchExact = matchExact;
    }

    public BlockState getMatchState() {
        return matchState;
    }

    public boolean doesMatchExact() {
        return matchExact;
    }

    @Nonnull
    public ItemStack getDisplayStack() {
        return this.display.copy();
    }

    @Override
    public boolean test(BlockState state) {
        return this.matchExact ? BlockUtils.matchStateExact(state, this.matchState) : state.getBlock().equals(this.matchState.getBlock());
    }
}
