/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree.perk;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.types.PerkDataTypesAS;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tree.*;
import hellfirepvp.astralsorcery.common.perk.tree.point.MajorPerkTreePoint;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MajorPerk
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class MajorPerk extends AttributeModifierPerk<AbstractPerk.Data> {

    public static final MapCodec<MajorPerk> CODEC = RecordCodecBuilder.mapCodec(inst -> perkModifierFields(inst).apply(inst, MajorPerk::new));
    public static final PerkType<MajorPerk> TYPE =
            PerkType.of(MajorPerk.CODEC, PerkDataTypesAS.DEFAULT_DATA, MajorPerk::new);

    private MajorPerk(ResourceLocation key, float x, float y) {
        this(key, defaultNameKey(key), x, y, PerkCategory.MAJOR, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
    }

    protected MajorPerk(ResourceLocation key, String nameKey, float x, float y, PerkCategory category, Collection<PerkRequirement> requirements, Collection<PerkAttributeConverter> converters, Collection<PerkAttributeModifier> modifiers) {
        super(key, nameKey, x, y, category, requirements, converters, modifiers);
    }

    @Override
    protected PerkTreePoint<?> initPerkTreePoint() {
        return new MajorPerkTreePoint<>(this.getOffset(), this);
    }

    @Override
    public PerkType<?> getType() {
        return TYPE;
    }
}
