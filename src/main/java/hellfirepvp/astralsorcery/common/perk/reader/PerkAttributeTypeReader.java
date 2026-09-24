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
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkAttributeTypeReader
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class PerkAttributeTypeReader {

    private static final DecimalFormat percentageFormat = new DecimalFormat("0.00");

    private final Supplier<? extends PerkAttributeType> type;
    private boolean negate = false;

    public PerkAttributeTypeReader(Supplier<? extends PerkAttributeType> type) {
        this.type = type;
    }

    public final PerkAttributeType getType() {
        return this.type.get();
    }

    public <T extends PerkAttributeTypeReader> T negate() {
        this.negate = true;
        return MiscUtil.cast(this);
    }

    /**
     * Return the default value the perks or other things scale off of.
     *
     * @param statMap The player's current stat map
     * @param player The player
     * @param side The current side
     * @return The default value as it would be without any modifiers.
     */
    public abstract double getDefaultValue(PerkAttributeMap statMap, Player player, LogicalSide side);

    /**
     * Return the modifier (multiplier or addition) for the given mode.
     *
     * @param statMap The player's current stat map
     * @param player The player
     * @param side The current side
     * @param mode The mode to get the modifier for
     * @return The currently applying modifier value for the given mode.
     */
    public abstract double getModifierValueForMode(PerkAttributeMap statMap, Player player, LogicalSide side, ModifierType mode);

    public static String formatDecimal(double decimal) {
        return percentageFormat.format(decimal);
    }

    public String getDisplayFormat(PerkAttributeModifier modifier) {
        return Util.makeDescriptionId("perk.modifier", AstralSorcery.key("format"));
    }
    
    public String getValueDisplayFormat(PerkAttributeModifier modifier, @Nullable Player player, @Nullable PlayerProgress progress) {
        return Util.makeDescriptionId("perk.modifier", AstralSorcery.key("value_format"));
    }

    public String getDisplayValue(PerkAttributeModifier modifier, @Nullable Player player, @Nullable PlayerProgress progress) {
        return modifier.getMode().stringifyValue(this.getRawValue(modifier, player, progress));
    }

    public String getDisplayModifierOperation(PerkAttributeModifier modifier, @Nullable Player player, @Nullable PlayerProgress progress) {
        return modifier.getMode().getModifierOperationNameId(this.getRawValue(modifier, player, progress));
    }
    
    public Component formatDisplayValue(PerkAttributeModifier modifier, @Nullable Player player, @Nullable PlayerProgress progress, String valueStr) {
        return Component.translatable(this.getValueDisplayFormat(modifier, player, progress), valueStr);
    }

    public Component formatDisplayModifierOperation(PerkAttributeModifier modifier, String operationStr) {
        return Component.translatable(operationStr);
    }

    public MutableComponent getDisplay(PerkAttributeModifier modifier, @Nullable Player player, @Nullable PlayerProgress progress) {
        String format = this.getDisplayFormat(modifier);
        Component value = this.formatDisplayValue(modifier, player, progress, this.getDisplayValue(modifier, player, progress));
        Component operation = this.formatDisplayModifierOperation(modifier, this.getDisplayModifierOperation(modifier, player, progress));
        MutableComponent attributeType = this.getType().getName();

        return Component.translatable(format, value, operation, attributeType)
                .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC);
    }

    protected float getRawValue(PerkAttributeModifier modifier, @Nullable Player player, @Nullable PlayerProgress progress) {
        float val = modifier.getValue(player, progress);
        return this.negate ? -val : val;
    }

    public static record Type(Supplier<? extends PerkAttributeType> perkAttributeType, PerkAttributeTypeReader reader) {
    }
}
