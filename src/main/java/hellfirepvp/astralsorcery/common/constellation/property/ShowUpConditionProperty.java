/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.property;

import com.mojang.datafixers.util.Either;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ShowUpConditionProperty
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ShowUpConditionProperty extends ConstellationProperty<ShowUpConditionProperty> {

    public static final Key<ShowUpConditionProperty> KEY = new Key<>();
    private final Either<RegularDistributionShowUpCondition, DynamicShowUpCondition> condition;

    private ShowUpConditionProperty(BaseConstellation constellation, DynamicShowUpCondition condition) {
        super(KEY, constellation);
        this.condition = Either.right(condition);
    }

    private ShowUpConditionProperty(BaseConstellation constellation, RegularDistributionShowUpCondition condition) {
        super(KEY, constellation);
        this.condition = Either.left(condition);
    }

    public static Function<BaseConstellation, ShowUpConditionProperty> create(DynamicShowUpCondition condition) {
        return cst -> new ShowUpConditionProperty(cst, condition);
    }

    public static Function<BaseConstellation, ShowUpConditionProperty> create(RegularDistributionShowUpCondition condition) {
        return cst -> new ShowUpConditionProperty(cst, condition);
    }

    public static Function<BaseConstellation, ShowUpConditionProperty> forSeededMoonPhases(MoonPhase... phases) {
        return forSeededMoonPhases(0.25F, phases);
    }

    public static Function<BaseConstellation, ShowUpConditionProperty> forSeededMoonPhases(float distanceDropOff, MoonPhase... phases) {
        return cst -> new ShowUpConditionProperty(cst, RegularDistributionShowUpCondition.forShiftedMoonPhases(distanceDropOff, phases));
    }

    public static Function<BaseConstellation, ShowUpConditionProperty> forFixedMoonPhases(MoonPhase... phases) {
        return forSeededMoonPhases(0.25F, phases);
    }

    public static Function<BaseConstellation, ShowUpConditionProperty> forFixedMoonPhases(float distanceDropOff, MoonPhase... phases) {
        return cst -> new ShowUpConditionProperty(cst, RegularDistributionShowUpCondition.forFixedMoonPhases(distanceDropOff, phases));
    }

    public boolean canIndexPhaseDistribution() {
        return this.condition.left().isPresent();
    }

    public boolean doesShowUp(Level world, long day, long seed) {
        return this.condition.map(
                left -> left.doesShowUp(MoonPhase.fromDay(day), seed),
                right -> right.doesShowUp(world, day, seed)
        );
    }

    public float getDistribution(Level world, long day, long seed, boolean active) {
        return Mth.clamp(this.condition.map(
                left -> left.getDistribution(MoonPhase.fromDay(day), seed, active),
                right -> right.getDistribution(world, day, seed, active)
        ), 0F, 1F);
    }

    public static interface RegularDistributionShowUpCondition {

        public static RegularDistributionShowUpCondition forFixedMoonPhases(float distanceDropOff, MoonPhase... phases) {
            return new RegularDistributionShowUpCondition() {
                @Override
                public boolean doesShowUp(MoonPhase currentPhase, long seed) {
                    return Arrays.asList(phases).contains(currentPhase);
                }

                @Override
                public float getDistribution(MoonPhase currentPhase, long seed, boolean active) {
                    int distance = getDistance(currentPhase, List.of(phases));
                    return 1F - distance * distanceDropOff;
                }
            };
        }

        public static RegularDistributionShowUpCondition forShiftedMoonPhases(float distanceDropOff, MoonPhase... phases) {
            return new RegularDistributionShowUpCondition() {

                @Override
                public boolean doesShowUp(MoonPhase currentPhase, long seed) {
                    return this.shiftPhases(phases, seed).contains(currentPhase);
                }

                @Override
                public float getDistribution(MoonPhase currentPhase, long seed, boolean active) {
                    int distance = getDistance(currentPhase, this.shiftPhases(phases, seed));
                    return 1F - distance * distanceDropOff;
                }

                private List<MoonPhase> shiftPhases(MoonPhase[] phases, long seed) {
                    List<MoonPhase> shifted = NonNullList.createWithCapacity(phases.length);
                    for (int i = 0; i < phases.length; i++) {
                        MoonPhase original = phases[i];
                        int index = original.ordinal() + (((int) (seed % MoonPhase.values().length)) + MoonPhase.values().length);
                        index %= MoonPhase.values().length;
                        shifted.set(i, MoonPhase.values()[index]);
                    }
                    return shifted;
                }
            };
        }

        boolean doesShowUp(MoonPhase currentPhase, long seed);

        float getDistribution(MoonPhase currentPhase, long seed, boolean active);

    }

    public static int getDistance(MoonPhase current, List<MoonPhase> activePhases) {
        return activePhases.stream()
                .map(MoonPhase::ordinal)
                .min(Comparator.comparingInt(ord -> getDistance(current.ordinal(), ord)))
                .map(ord -> getDistance(current.ordinal(), ord))
                .orElse(MoonPhase.values().length);
    }

    private static int getDistance(int current, int other) {
        int distance = Math.abs(other - current) % MoonPhase.values().length;
        return Math.min(distance, MoonPhase.values().length - distance);
    }

    public static interface DynamicShowUpCondition {

        //should return true or false for the entire day + seed combination in that level
        boolean doesShowUp(Level world, long day, long seed);

        //'active' should mean it returns true for that entire day + seed combination in that level
        float getDistribution(Level world, long day, long seed, boolean active);

    }
}
