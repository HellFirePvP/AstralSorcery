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
 * Class: Ritual
 * Created by HellFirePvP
 * Date: 02.02.2019 / 16:22
 */
public class Ritual extends TraitedSourceInstance {

    public Ritual(PropertySource<?, ?> source,
                  @Nullable IWeakConstellation attunedConstellation,
                  @Nullable IMinorConstellation traitConstellation) {
        super(source, attunedConstellation, traitConstellation);
    }
}
