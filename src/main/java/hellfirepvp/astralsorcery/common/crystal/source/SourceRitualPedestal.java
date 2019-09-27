/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal.source;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.crystal.calc.PropertySource;
import hellfirepvp.astralsorcery.common.crystal.source.instance.Ritual;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.tile.network.StarlightReceiverRitualPedestal;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SourceRitualPedestal
 * Created by HellFirePvP
 * Date: 02.02.2019 / 16:22
 */
public class SourceRitualPedestal extends PropertySource<StarlightReceiverRitualPedestal, Ritual> {

    public SourceRitualPedestal() {
        super(AstralSorcery.key("ritual"));
    }

    @Override
    public Ritual createInstance(StarlightReceiverRitualPedestal pedestal) {
        return new Ritual(this, pedestal.getChannelingType(), pedestal.getChannelingTrait());
    }
}
