/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal.source.instance;

import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crystal.calc.PropertySource;
import hellfirepvp.astralsorcery.common.tile.network.StarlightReceiverRitualPedestal;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Ritual
 * Created by HellFirePvP
 * Date: 02.02.2019 / 16:22
 */
public class Ritual extends PropertySource.SourceInstance<StarlightReceiverRitualPedestal, Ritual> {

    private final IWeakConstellation channelingConstellation;
    private final IMinorConstellation traitConstellation;

    public Ritual(PropertySource<StarlightReceiverRitualPedestal, Ritual> property,
                  @Nullable IWeakConstellation channelingConstellation,
                  @Nullable IMinorConstellation traitConstellation) {
        super(property);
        this.channelingConstellation = channelingConstellation;
        this.traitConstellation = traitConstellation;
    }

    @Nullable
    public IWeakConstellation getChannelingConstellation() {
        return channelingConstellation;
    }

    @Nullable
    public IMinorConstellation getTraitConstellation() {
        return traitConstellation;
    }
}
