/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crystal;

import hellfirepvp.astralsorcery.common.lumen.Lumen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CrystalPropertyConstellation
 * Created by HellFirePvP
 * Date: 17.10.2026 / 15:50
 */
public class CrystalPropertyLumen extends CrystalProperty {

    private final Supplier<? extends Lumen> lumen;

    public CrystalPropertyLumen(Supplier<? extends Lumen> lumen) {
        super(ChatFormatting.GRAY, 1);
        this.lumen = lumen;
    }

    public Lumen getLumen() {
        return this.lumen.get();
    }

    @Override
    public MutableComponent getName(int currentTier, int tick) {
        return Component.translatable("crystal.property.astralsorcery.lumen_focus",
                        this.lumen.get().getHoverName().copy().withColor(this.getLumen().getColor(tick).getColor()))
                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(this.getColor().getColor())));
    }
}
