/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.reader;

import hellfirepvp.astralsorcery.common.perk.PerkAttributeMap;
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ReaderPercentageAttribute
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ReaderPercentageAttribute extends PerkAttributeTypeReader {

    public ReaderPercentageAttribute(Supplier<? extends PerkAttributeType> type) {
        super(type);
    }

    @Override
    public double getDefaultValue(PerkAttributeMap statMap, Player player, LogicalSide side) {
        return this.getType().isMultiplicative() ? 1 : 0;
    }

    @Override
    public double getModifierValueForMode(PerkAttributeMap statMap, Player player, LogicalSide side, ModifierType mode) {
        return statMap.getModifier(player, ResearchManager.getProgress(player, side), this.getType(), mode);
    }

    protected String formatForDisplay(double value) {
        value *= 100F;
        return (value >= 0 ? "+" : "") + formatDecimal(value) + "%";
    }
}
