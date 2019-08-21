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
import hellfirepvp.astralsorcery.common.crystal.source.instance.Crystal;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal.IndependentCrystalSource;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SourceCollectorCrystal
 * Created by HellFirePvP
 * Date: 02.02.2019 / 16:03
 */
public class SourceCollectorCrystal extends PropertySource<IndependentCrystalSource, Crystal> {

    public SourceCollectorCrystal() {
        super(new ResourceLocation(AstralSorcery.MODID, "collector_crystal"));
    }

    @Override
    public Crystal createInstance(IndependentCrystalSource source) {
        return new Crystal(this, source.getStarlightType());
    }
}
