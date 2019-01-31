/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.reader;

import hellfirepvp.astralsorcery.common.constellation.perk.PlayerAttributeMap;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.DecimalFormat;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeReader
 * Created by HellFirePvP
 * Date: 05.01.2019 / 13:41
 */
public abstract class AttributeReader {

    private static final DecimalFormat percentageFormat = new DecimalFormat("0.00");

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
    @SideOnly(Side.CLIENT)
    public abstract PerkStatistic getStatistics(PlayerAttributeMap statMap, EntityPlayer player);

    /**
     * Return the default value the perks or other things scale off of.
     *
     * @param statMap The player's current stat map
     * @param player The player
     * @param side The current side
     * @return The default value as it would be without any modifiers.
     */
    public abstract double getDefaultValue(PlayerAttributeMap statMap, EntityPlayer player, Side side);

    /**
     * Return the modifier (multiplier or addition) for the given mode.
     *
     * @param statMap The player's current stat map
     * @param player The player
     * @param side The current side
     * @param mode The mode to get the modifier for
     * @return The currently applying modifier value for the given mode.
     */
    public abstract double getModifierValueForMode(PlayerAttributeMap statMap, EntityPlayer player, Side side,
                                                   PerkAttributeModifier.Mode mode);

    public static String formatDecimal(double decimal) {
        return percentageFormat.format(decimal);
    }

}
