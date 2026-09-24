/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.reader;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeMap;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import net.minecraft.Util;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ReaderAddedSecondsPercentage
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ReaderAddedSecondsPercentage extends PerkAttributeTypeReader {

    public ReaderAddedSecondsPercentage(Supplier<? extends PerkAttributeType> type) {
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

    @Override
    public String getValueDisplayFormat(PerkAttributeModifier modifier, @Nullable Player player, @Nullable PlayerProgress progress) {
        if (modifier.getMode() == ModifierType.ADDITION) {
            if (this.getRawValue(modifier, player, progress) == 20) {
                return Util.makeDescriptionId("perk.modifier", AstralSorcery.key("value_format.second"));
            } else {
                return Util.makeDescriptionId("perk.modifier", AstralSorcery.key("value_format.second.plural"));
            }
        }
        return super.getValueDisplayFormat(modifier, player, progress);
    }

    @Override
    public String getDisplayValue(PerkAttributeModifier modifier, @Nullable Player player, @Nullable PlayerProgress progress) {
        if (modifier.getMode() == ModifierType.ADDITION) {
            return modifier.getMode().stringifyValue(this.getRawValue(modifier, player, progress) / 20);
        }
        return super.getDisplayValue(modifier, player, progress);
    }
}
