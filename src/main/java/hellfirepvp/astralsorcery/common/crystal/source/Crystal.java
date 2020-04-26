/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal.source;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crystal.calc.PropertySource;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Crystal
 * Created by HellFirePvP
 * Date: 02.02.2019 / 16:03
 */
public class Crystal extends AttunedSourceInstance {

    public Crystal(PropertySource<?, ?> source,
                   @Nullable IWeakConstellation attunedConstellation) {
        super(source, attunedConstellation);
    }
}
