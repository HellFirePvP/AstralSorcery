/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal.source;

import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crystal.calc.PropertySource;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TraitedSourceInstance
 * Created by HellFirePvP
 * Date: 25.04.2020 / 21:00
 */
public class TraitedSourceInstance extends AttunedSourceInstance {

    private final IMinorConstellation traitConstellation;

    public TraitedSourceInstance(PropertySource<?, ?> source,
                                 @Nullable IWeakConstellation attunedConstellation,
                                 @Nullable IMinorConstellation traitConstellation) {
        super(source, attunedConstellation);
        this.traitConstellation = traitConstellation;
    }

    @Nullable
    public IMinorConstellation getTraitConstellation() {
        return traitConstellation;
    }
}
