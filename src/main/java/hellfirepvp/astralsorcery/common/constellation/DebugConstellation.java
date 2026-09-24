/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DebugConstellation
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DebugConstellation extends BaseConstellation {

    private DebugConstellation(ColorWrapper constellationColor) {
        super(constellationColor);
    }

    public static DebugConstellation create() {
        var builder = BaseConstellation.<DebugConstellation>builder(ColorWrapper.of(0xFF, 0x0, 0x0))
                .tier(BaseConstellation.Tier.MAJOR)
                .sorted();

        StarLocation l1 = builder.addStar(0, 0);
        StarLocation l2 = builder.addStar(0, 31);
        StarLocation l3 = builder.addStar(31, 31);
        StarLocation l4 = builder.addStar(31, 0);
        builder.addStar(8, 8);
        builder.addStar(8, 23);
        builder.addStar(23, 23);
        builder.addStar(23, 8);

        builder.addConnection(l1, l2);
        builder.addConnection(l2, l3);
        builder.addConnection(l3, l4);
        builder.addConnection(l4, l1);

        return builder.build(DebugConstellation::new).get();
    }
}
