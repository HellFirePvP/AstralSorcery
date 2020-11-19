/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.gem;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GemType
 * Created by HellFirePvP
 * Date: 09.08.2019 / 07:33
 */
public enum GemType {

    SKY(0.15F, 1.0F),
    DAY(0.6F, 0.4F),
    NIGHT(0F, 2.0F);

    public final float countModifier;
    public final float amplifierModifier;

    GemType(float countModifier, float amplifierModifier) {
        this.countModifier = countModifier;
        this.amplifierModifier = amplifierModifier;
    }

}
