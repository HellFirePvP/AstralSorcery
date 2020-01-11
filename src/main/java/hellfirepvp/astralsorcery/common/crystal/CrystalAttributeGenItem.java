/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalAttributeGenItem
 * Created by HellFirePvP
 * Date: 20.08.2019 / 20:14
 */
public interface CrystalAttributeGenItem extends CrystalAttributeItem {

    int getGeneratedPropertyTiers();

    int getMaxPropertyTiers();
}
