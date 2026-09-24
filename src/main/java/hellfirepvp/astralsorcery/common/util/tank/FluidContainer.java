/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.tank;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FluidContainer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FluidContainer {

    public static final Codec<FluidContainer> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            FluidStack.CODEC.optionalFieldOf("content", FluidStack.EMPTY).forGetter(FluidContainer::getContent)
    ).apply(inst, FluidContainer::new));

    private FluidStack content;

    public FluidContainer(FluidStack content) {
        this.content = content;
    }

    public FluidStack getContent() {
        return this.content.copy();
    }

    public FluidStack getModifiableContent() {
        return this.content;
    }

    public void setContent(FluidStack content) {
        this.content = content.copy();
    }

    public void clear() {
        this.content = FluidStack.EMPTY;
    }
}
