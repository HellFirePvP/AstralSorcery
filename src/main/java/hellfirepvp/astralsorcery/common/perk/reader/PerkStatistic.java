/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.reader;

import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkStatistic
 * Created by HellFirePvP
 * Date: 09.08.2019 / 07:48
 */
public class PerkStatistic {

    private final PerkAttributeType type;
    private final String unlocPerkTypeName;
    private final String perkValue;
    private final String suffix;
    private final String postProcessInfo;

    public PerkStatistic(PerkAttributeType type, String perkValue, String suffix, String postProcessInfo) {
        this.type = type;
        this.unlocPerkTypeName = type.getUnlocalizedName();
        this.perkValue = perkValue;
        this.suffix = suffix;
        this.postProcessInfo = postProcessInfo;
    }

    public PerkAttributeType getType() {
        return type;
    }

    public String getUnlocPerkTypeName() {
        return unlocPerkTypeName;
    }

    public String getPerkValue() {
        return perkValue;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getPostProcessInfo() {
        return postProcessInfo;
    }
}
