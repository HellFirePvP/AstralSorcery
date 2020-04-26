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
 * Class: AttunedSourceInstance
 * Created by HellFirePvP
 * Date: 25.04.2020 / 20:59
 */
public class AttunedSourceInstance extends PropertySource.SourceInstance {

    private final IWeakConstellation attunedConstellation;

    public AttunedSourceInstance(PropertySource<?, ?> source, @Nullable IWeakConstellation attunedConstellation) {
        super(source);
        this.attunedConstellation = attunedConstellation;
    }

    @Nullable
    public IWeakConstellation getAttunedConstellation() {
        return attunedConstellation;
    }
}
