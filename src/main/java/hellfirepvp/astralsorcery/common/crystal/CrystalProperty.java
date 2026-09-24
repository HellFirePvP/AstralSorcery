/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal;

import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CrystalProperty
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CrystalProperty implements Comparable<CrystalProperty> {

    private static int counter = 0;
    private final int sortingId;

    private final Supplier<String> unlocalizedName;
    private final ColorWrapper color;
    private final int maxTier;

    public CrystalProperty(ChatFormatting color, int maxTier) {
        this(ColorWrapper.opaque(color.getColor()), maxTier);
    }

    public CrystalProperty(TextColor color, int maxTier) {
        this(ColorWrapper.opaque(color.getValue()), maxTier);
    }

    public CrystalProperty(ColorWrapper color, int maxTier) {
        this.sortingId = counter++;
        this.unlocalizedName = NameUtil.cacheName("crystal.property", RegistriesAS.REGISTRY_CRYSTAL_PROPERTIES, this);
        this.color = color;
        this.maxTier = maxTier;
    }

    public int getMaxTier() {
        return this.maxTier;
    }

    public ColorWrapper getColor() {
        return this.color;
    }

    public Style getColorStyle() {
        return Style.EMPTY.withColor(TextColor.fromRgb(this.getColor().getColor()));
    }

    public MutableComponent getName(int currentTier) {
        return Component.translatable(this.unlocalizedName.get());
    }

    public String getNameFormat() {
        return "crystal.property.format";
    }

    @Override
    public int compareTo(CrystalProperty o) {
        return this.sortingId - o.sortingId;
    }
}
