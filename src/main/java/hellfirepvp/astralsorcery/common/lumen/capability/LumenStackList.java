/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.capability;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenStackList
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenStackList {

    public static final Codec<LumenStackList> CODEC = Codec.list(LumenStack.CODEC)
            .xmap(LumenStackList::new, view -> view.lumenStacks);

    private final List<LumenStack> lumenStacks = new ArrayList<>();

    private LumenStackList(List<LumenStack> lumenStacks) {
        this.lumenStacks.addAll(lumenStacks);
    }

    public static LumenStackList create() {
        return new LumenStackList(new ArrayList<>());
    }

    public List<LumenStack> getLumenStacks() {
        return this.lumenStacks.stream()
                .map(LumenStack::copy)
                .toList();
    }

    public List<LumenStack> getModifiableLumenStacks() {
        return this.lumenStacks;
    }

    public Optional<LumenStack> getLumenStack(Lumen lumen) {
        return this.lumenStacks.stream()
                .filter(stack -> stack.getLumen().equals(lumen))
                .findFirst()
                .map(LumenStack::copy);
    }

    public void setLumenStack(LumenStack stack) {
        this.lumenStacks.removeIf(existing -> existing.getLumen().equals(stack.getLumen()));
        if (stack.getAmount() > 0) {
            this.lumenStacks.add(stack.copy());
        }
    }

    public void clear() {
        this.lumenStacks.clear();
    }

    public boolean isEmpty() {
        return this.getLumenStacks().isEmpty() || this.getLumenStacks().stream().allMatch(LumenStack::isEmpty);
    }
}
