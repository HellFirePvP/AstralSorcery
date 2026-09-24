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
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ReaderFlatAttribute
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ReaderFlatAttribute extends PerkAttributeTypeReader {

    private final double defaultValue;
    private boolean formatAsDecimal = false;

    public ReaderFlatAttribute(Supplier<? extends PerkAttributeType> type, double defaultValue) {
        super(type);
        this.defaultValue = defaultValue;
    }

    public static Function<Supplier<? extends PerkAttributeType>, ReaderFlatAttribute> withDefault(double defaultValue) {
        return type -> new ReaderFlatAttribute(type, defaultValue);
    }

    public <T extends ReaderFlatAttribute> T formatAsDecimal() {
        this.formatAsDecimal = true;
        return MiscUtil.cast(this);
    }

    @Override
    public double getDefaultValue(PerkAttributeMap statMap, Player player, LogicalSide side) {
        return this.defaultValue;
    }

    @Override
    public double getModifierValueForMode(PerkAttributeMap statMap, Player player, LogicalSide side, ModifierType mode) {
        return statMap.getModifier(player, ResearchManager.getProgress(player, side), this.getType(), mode);
    }

    protected String formatForDisplay(double value) {
        String valueStr;
        if (this.formatAsDecimal) {
            valueStr = formatDecimal(value);
        } else {
            valueStr = String.valueOf(Mth.floor(value));
        }

        return (value >= 0 ? "+" : "") + valueStr;
    }
}
