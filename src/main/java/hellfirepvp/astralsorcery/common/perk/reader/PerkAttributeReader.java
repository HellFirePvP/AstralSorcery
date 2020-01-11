/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.reader;

import hellfirepvp.astralsorcery.common.perk.PerkAttributeMap;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.text.DecimalFormat;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkAttributeReader
 * Created by HellFirePvP
 * Date: 09.08.2019 / 07:47
 */
public abstract class PerkAttributeReader extends ForgeRegistryEntry<PerkAttributeReader> {

    private static final DecimalFormat percentageFormat = new DecimalFormat("0.00");

    private final PerkAttributeType type;

    /**
     * Create a new attribute reader for a specific attribute type.
     * See {@link PerkAttributeType#getReader()} for the default usage
     *
     * @param type the type this reader correlates with.
     */
    protected PerkAttributeReader(PerkAttributeType type) {
        this.type = type;
        this.setRegistryName(this.type.getRegistryName());
    }

    /**
     * Get the type this reader parses
     * @return the type
     */
    public final PerkAttributeType getType() {
        return type;
    }

    /**
     * Returns a string representation of the current attribute.
     *
     * Return string should be the currently active value, properly formatted as
     * percentage or flat value depending on its representation.
     *
     * @param statMap The player's current stat map
     * @param player The player
     * @return A string representation of the attribute's value
     */
    @OnlyIn(Dist.CLIENT)
    public abstract PerkStatistic getStatistics(PerkAttributeMap statMap, PlayerEntity player);

    /**
     * Return the default value the perks or other things scale off of.
     *
     * @param statMap The player's current stat map
     * @param player The player
     * @param side The current side
     * @return The default value as it would be without any modifiers.
     */
    public abstract double getDefaultValue(PerkAttributeMap statMap, PlayerEntity player, LogicalSide side);

    /**
     * Return the modifier (multiplier or addition) for the given mode.
     *
     * @param statMap The player's current stat map
     * @param player The player
     * @param side The current side
     * @param mode The mode to get the modifier for
     * @return The currently applying modifier value for the given mode.
     */
    public abstract double getModifierValueForMode(PerkAttributeMap statMap, PlayerEntity player, LogicalSide side,
                                                   ModifierType mode);

    public static String formatDecimal(double decimal) {
        return percentageFormat.format(decimal);
    }

}
