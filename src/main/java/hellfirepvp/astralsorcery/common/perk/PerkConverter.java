/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collection;
import java.util.Collections;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkConverter
 * Created by HellFirePvP
 * Date: 08.08.2019 / 17:28
 */
public abstract class PerkConverter {

    private static int counter = 0;

    private final int id;

    public PerkConverter() {
        this.id = counter;
        counter++;
    }

    public static final PerkConverter IDENTITY = new PerkConverter() {
        @Nonnull
        @Override
        public PerkAttributeModifier convertModifier(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier perkAttributeModifier, @Nullable ModifierSource owningSource) {
            return perkAttributeModifier;
        }
    };

    public int getId() {
        return id;
    }

    /**
     * Use {@link PerkAttributeModifier#convertModifier(PerkAttributeType, ModifierType, float)} to convert the given modifier
     */
    @Nonnull
    public abstract PerkAttributeModifier convertModifier(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable ModifierSource owningSource);

    /**
     * Use {@link PerkAttributeModifier#gainAsExtraModifier(PerkConverter, PerkAttributeType, ModifierType, float)} to create new modifiers
     * based off of the given modifier! The resulting modifiers cannot be modified with perk converters.
     */
    @Nonnull
    public Collection<PerkAttributeModifier> gainExtraModifiers(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable ModifierSource owningSource) {
        return Lists.newArrayList();
    }

    public void onApply(PlayerEntity player, LogicalSide dist) {}

    public void onRemove(PlayerEntity player, LogicalSide dist) {}

    public PerkConverter andThen(PerkConverter next) {
        PerkConverter thisConverter = this;
        return new PerkConverter() {
            @Nonnull
            @Override
            public PerkAttributeModifier convertModifier(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable ModifierSource owningSource) {
                return thisConverter.convertModifier(player, progress, next.convertModifier(player, progress, modifier, owningSource), owningSource);
            }
        };
    }

    public Radius asRangedConverter(Point.Float offset, float radius) {
        PerkConverter thisConverter = this;
        return new Radius(offset, radius) {
            @Nonnull
            @Override
            public PerkAttributeModifier convertModifierInRange(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier modifier, AbstractPerk owningPerk) {
                return thisConverter.convertModifier(player, progress, modifier, owningPerk);
            }

            @Nonnull
            @Override
            public Collection<PerkAttributeModifier> gainExtraModifiersInRange(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier modifier, AbstractPerk owningPerk) {
                return thisConverter.gainExtraModifiers(player, progress, modifier, owningPerk);
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerkConverter that = (PerkConverter) o;
        return id == that.id;
    }

    public static abstract class Radius extends PerkConverter {

        private final float radius;
        private final Point.Float offset;

        public Radius(Point.Float point, float radius) {
            this.offset = point;
            this.radius = radius;
        }

        public float getRadius() {
            return radius;
        }

        public Point.Float getOffset() {
            return offset;
        }

        public Radius withNewRadius(float radius) {
            Radius thisRadius = this;
            return new Radius(thisRadius.getOffset(), radius) {
                @Nonnull
                @Override
                public PerkAttributeModifier convertModifierInRange(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier modifier, AbstractPerk owningPerk) {
                    return thisRadius.convertModifierInRange(player, progress, modifier, owningPerk);
                }

                @Nonnull
                @Override
                public Collection<PerkAttributeModifier> gainExtraModifiersInRange(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier modifier, AbstractPerk owningPerk) {
                    return thisRadius.gainExtraModifiersInRange(player, progress, modifier, owningPerk);
                }
            };
        }

        protected boolean canAffectPerk(PlayerProgress progress, AbstractPerk otherPerk) {
            return getOffset().distance(otherPerk.getOffset()) <= getRadius();
        }

        @Nonnull
        @Override
        public PerkAttributeModifier convertModifier(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable ModifierSource owningSource) {
            if (!(owningSource instanceof AbstractPerk)) {
                return modifier; //Converting in range doesn't make sense if we're not a perk.
            }
            AbstractPerk owningPerk = (AbstractPerk) owningSource;
            if (!canAffectPerk(progress, owningPerk)) {
                return modifier;
            }
            return convertModifierInRange(player, progress, modifier, owningPerk);
        }

        @Nonnull
        @Override
        public Collection<PerkAttributeModifier> gainExtraModifiers(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier modifier, @Nullable ModifierSource owningSource) {
            if (!(owningSource instanceof AbstractPerk)) {
                return Collections.emptyList(); //Gaining extra in range doesn't make sense if we're not a perk.
            }
            AbstractPerk owningPerk = (AbstractPerk) owningSource;
            if (!canAffectPerk(progress, owningPerk)) {
                return Collections.emptyList();
            }
            return gainExtraModifiersInRange(player, progress, modifier, owningPerk);
        }

        @Nonnull
        public abstract PerkAttributeModifier convertModifierInRange(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier modifier, AbstractPerk owningPerk);

        @Nonnull
        public abstract Collection<PerkAttributeModifier> gainExtraModifiersInRange(PlayerEntity player, PlayerProgress progress, PerkAttributeModifier modifier, AbstractPerk owningPerk);

    }
}
