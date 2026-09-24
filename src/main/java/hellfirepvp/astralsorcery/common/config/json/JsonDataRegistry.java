/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.config.json;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: JsonDataRegistry
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class JsonDataRegistry<T> {

    protected static final RandomSource rand = RandomSource.create();
    private final List<T> loadedValues = new ArrayList<>();

    void setLoadedValues(List<T> values) {
        this.loadedValues.clear();
        this.loadedValues.addAll(values);
    }

    public List<T> getLoadedValues() {
        return Collections.unmodifiableList(this.loadedValues);
    }

    public Optional<T> getRandomEntry() {
        return MiscUtil.getRandomEntry(this.getLoadedValues(), rand);
    }

    public Optional<T> getRandomEntry(Function<T, Integer> getWeight) {
        return MiscUtil.getWeightedRandomEntry(this.getLoadedValues(), rand, getWeight);
    }

    public abstract Codec<T> elementCodec();

    public abstract List<T> getDefaultValues();

}
