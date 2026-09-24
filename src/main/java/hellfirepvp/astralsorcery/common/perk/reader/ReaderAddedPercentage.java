/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.reader;

import hellfirepvp.astralsorcery.common.perk.PerkAttributeMap;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ReaderAddedPercentage
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ReaderAddedPercentage extends PerkAttributeTypeReader {

    protected final boolean addPercentageSymbol;

    protected ReaderAddedPercentage(Supplier<? extends PerkAttributeType> type, boolean addPercentageSymbol) {
        super(type);
        this.addPercentageSymbol = addPercentageSymbol;
    }

    public static ReaderAddedPercentage withoutPercent(Supplier<? extends PerkAttributeType> type) {
        return new ReaderAddedPercentage(type, false);
    }

    public static ReaderAddedPercentage withPercent(Supplier<? extends PerkAttributeType> type) {
        return new ReaderAddedPercentage(type, true);
    }

    @Override
    public double getDefaultValue(PerkAttributeMap statMap, Player player, LogicalSide side) {
        return this.getType().isMultiplicative() ? 1 : 0;
    }

    @Override
    public double getModifierValueForMode(PerkAttributeMap statMap, Player player, LogicalSide side, ModifierType mode) {
        return statMap.getModifier(player, ResearchManager.getProgress(player, side), this.getType(), mode);
    }

    @Override
    public String getDisplayValue(PerkAttributeModifier modifier, @Nullable Player player, @Nullable PlayerProgress progress) {
        if (modifier.getMode() == ModifierType.ADDITION) {
            String valueStr = modifier.getMode().stringifyValue(this.getRawValue(modifier, player, progress) * 100);
            if (addPercentageSymbol) {
                valueStr += "%";
            }
            return valueStr;
        }
        return super.getDisplayValue(modifier, player, progress);
    }
}
