/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.convert;

import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.util.data.FloatPoint;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RangedPerkAttributeConverter
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RangedPerkAttributeConverter extends PerkAttributeConverter {

    private final PerkAttributeConverter converter;
    private final FloatPoint offset;
    private final float radius;

    protected RangedPerkAttributeConverter(PerkAttributeConverter modifierConverter, FloatPoint offset, float radius) {
        super(modifierConverter.getIdentifier());
        this.converter = modifierConverter;
        this.offset = offset;
        this.radius = radius;
    }

    public static RangedPerkAttributeConverter asRanged(PerkAttributeConverter converter, FloatPoint offset, float radius) {
        return new RangedPerkAttributeConverter(converter, offset, radius);
    }

    public FloatPoint getOffset() {
        return this.offset;
    }

    public float getRadius() {
        return this.radius;
    }

    @Nonnull
    @Override
    public PerkAttributeModifier convertModifier(Player player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable ModifierSource owningSource) {
        if (!(owningSource instanceof AbstractPerk<?> otherPerk)) return modifier;
        if (!this.affectsPerk(otherPerk)) return modifier;
        return this.converter.convertModifier(player, progress, modifier, owningSource);
    }

    @Nonnull
    @Override
    public Collection<PerkAttributeModifier> gainExtraModifiers(Player player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable ModifierSource owningSource) {
        if (!(owningSource instanceof AbstractPerk<?> otherPerk)) return Collections.emptyList();
        if (!this.affectsPerk(otherPerk)) return Collections.emptyList();
        return this.converter.gainExtraModifiers(player, progress, modifier, owningSource);
    }

    protected boolean affectsPerk(AbstractPerk<?> otherPerk) {
        return otherPerk.getOffset().distance(this.getOffset()) <= this.getRadius();
    }
}
