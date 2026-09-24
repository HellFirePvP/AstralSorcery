/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery;

import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.UnaryOperator;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EnumExtensions
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EnumExtensions {

    public static final EnumProxy<Rarity> RARITY_RELIC = new EnumProxy<>(
            Rarity.class, -1, AstralSorcery.MODID + ":relic",
            (UnaryOperator<Style>) style -> style.withColor(ColorsAS.RARITY_RELIC.getColor()));
    public static final EnumProxy<Rarity> RARITY_ARTIFACT = new EnumProxy<>(
            Rarity.class, -1, AstralSorcery.MODID + ":artifact",
            (UnaryOperator<Style>) style -> style.withColor(ColorsAS.RARITY_ARTIFACT.getColor()));

}
