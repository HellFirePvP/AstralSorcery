/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal.property;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crystal.CalculationContext;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.crystal.source.instance.Crystal;
import hellfirepvp.astralsorcery.common.crystal.source.instance.Ritual;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import net.minecraft.util.ResourceLocation;

import static hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS.Sources.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PropertyConstellation
 * Created by HellFirePvP
 * Date: 02.02.2019 / 17:22
 */
public class PropertyConstellation extends CrystalProperty {

    private IWeakConstellation cst;

    public PropertyConstellation(IWeakConstellation cst) {
        super(new ResourceLocation(AstralSorcery.MODID, "constellation." + cst.getSimpleName()));
        this.cst = cst;
        this.tierRequirement = ProgressionTier.ATTUNEMENT;
    }

    public IWeakConstellation getConstellation() {
        return cst;
    }

    @Override
    public int getMaxTier() {
        return 2;
    }

    @Override
    public boolean canSee(PlayerProgress progress) {
        return super.canSee(progress) && progress.hasConstellationDiscovered(this.cst);
    }

    @Override
    public double modify(double value, int thisTier, CalculationContext context) {
        if (context.isSource(SOURCE_COLLECTOR_CRYSTAL)) {
            Crystal crystal = context.getSource(SOURCE_COLLECTOR_CRYSTAL);
            if (crystal != null && cst.equals(crystal.getTunedConstellation())) {
                value *= 1.0 + (0.15 * thisTier);
            }
        }
        if (context.isSource(SOURCE_RITUAL_PEDESTAL)) {
            Ritual ritual = context.getSource(SOURCE_RITUAL_PEDESTAL);
            if (ritual != null && cst.equals(ritual.getChannelingConstellation())) {
                value *= 1.0 + (0.2 * thisTier);
            }
        }
        return value;
    }
}
