/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkCategory
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkCategory {

    public static final PerkCategory DEFAULT = new PerkCategory("default", ColorWrapper.WHITE);
    public static final PerkCategory ROOT = new PerkCategory("root", ColorWrapper.WHITE);
    public static final PerkCategory MAJOR = new PerkCategory("major", ChatFormatting.GOLD);
    public static final PerkCategory EPIPHANY = new PerkCategory("epiphany", ChatFormatting.GOLD);

    public static final Codec<PerkCategory> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ComponentSerialization.CODEC.fieldOf("name").forGetter(cat -> cat.name),
            ColorWrapper.CODEC.fieldOf("color").forGetter(PerkCategory::getColor)
    ).apply(inst, PerkCategory::new));

    private final Component name;
    private final ColorWrapper color;

    public PerkCategory(String name, ColorWrapper color) {
        this(AstralSorcery.key(name), color);
    }

    public PerkCategory(String name, ChatFormatting color) {
        this(AstralSorcery.key(name), ColorWrapper.opaque(color.getColor()));
    }

    public PerkCategory(ResourceLocation name, ColorWrapper color) {
        this(Component.translatable(Util.makeDescriptionId("perk.category", name)), color);
    }

    protected PerkCategory(Component name, ColorWrapper color) {
        this.name = name;
        this.color = color;
    }

    public MutableComponent getDisplayName() {
        return this.name.copy().withColor(this.getColor().getColor());
    }

    public ColorWrapper getColor() {
        return this.color;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PerkCategory that = (PerkCategory) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
