/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.reader.custom;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.reader.ReaderAddedPercentage;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import net.minecraft.Util;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ReaderAttackLifeLeech
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ReaderAttackLifeLeech extends ReaderAddedPercentage {

    public ReaderAttackLifeLeech(Supplier<? extends PerkAttributeType> type) {
        super(type, true);
    }

    @Override
    public String getDisplayFormat(PerkAttributeModifier modifier) {
        return Util.makeDescriptionId("perk.modifier", AstralSorcery.key("format.life_leech"));
    }
}
