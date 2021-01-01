/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.quality;

import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Locale;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GemQuality
 * Created by HellFirePvP
 * Date: 01.01.2021 / 14:12
 */
public enum GemQuality {

    BROKEN  (TextFormatting.GRAY,  0.1F),
    FLAWED  (TextFormatting.GRAY,  0.35F),
    MUNDANE (TextFormatting.WHITE, 0.5F),
    CLEAR   (TextFormatting.AQUA,  0.6F),
    FACETED (TextFormatting.AQUA,  0.7F),
    GLEAMING(TextFormatting.GOLD,  0.8F),
    FLAWLESS(TextFormatting.GOLD,  1.0F);

    private final TextFormatting color;
    private final float degree;

    GemQuality(TextFormatting color, float degree) {
        this.color = color;
        this.degree = degree;
    }

    public float getDegree() {
        return degree;
    }

    public IFormattableTextComponent getDisplayName() {
        return new TranslationTextComponent("item.astralsorcery.gem_quality.%s", this.name().toLowerCase(Locale.ROOT))
                .mergeStyle(this.color);
    }
}
