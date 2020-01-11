/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal;

import hellfirepvp.astralsorcery.common.crystal.calc.PropertySource;
import hellfirepvp.astralsorcery.common.crystal.calc.PropertyUsage;

import java.util.Collection;
import java.util.HashSet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CalculationContext
 * Created by HellFirePvP
 * Date: 29.01.2019 / 22:06
 */
public class CalculationContext {

    private Collection<PropertyUsage> usages = new HashSet<>();
    private PropertySource.SourceInstance<?, ?> source = null;

    private CalculationContext() {}

    public boolean uses(PropertyUsage usage) {
        return usages.contains(usage);
    }

    public boolean hasSource() {
        return source != null;
    }

    public boolean isSource(PropertySource<?, ?> source) {
        return hasSource() && this.source.getProperty().equals(source);
    }

    public <T, I extends PropertySource.SourceInstance<T, I>> I getSource(PropertySource<T, I> source) {
        if (isSource(source)) {
            return (I) this.source;
        }
        return null;
    }

    static class Builder {

        private CalculationContext ctx = new CalculationContext();

        static Builder newBuilder() {
            return new Builder();
        }

        Builder addUsage(PropertyUsage usage) {
            ctx.usages.add(usage);
            return this;
        }

        Builder fromSource(PropertySource.SourceInstance<?, ?> source) {
            ctx.source = source;
            return this;
        }

        CalculationContext build() {
            return this.ctx;
        }

    }

}
