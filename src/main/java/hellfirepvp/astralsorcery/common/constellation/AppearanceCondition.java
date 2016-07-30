package hellfirepvp.astralsorcery.common.constellation;

import scala.actors.threadpool.Arrays;

import java.util.EnumSet;
import java.util.List;

/**
* This class is part of the Astral Sorcery Mod
* The complete source code for this mod can be found on github.
* Class: AppearanceCondition
* Created by HellFirePvP
* Date: 30.07.2016 / 17:27
*/
public interface AppearanceCondition {

    public static AppearanceCondition conditionSolarEclipse = (currentPhase, specialEvents) -> specialEvents.contains(CelestialHandler.CelestialEvent.SOLAR_ECLIPSE);
    public static AppearanceCondition conditionLunarEclipse = (currentPhase, specialEvents) -> specialEvents.contains(CelestialHandler.CelestialEvent.LUNAR_ECLIPSE);

    public boolean shouldAppearThisNight(CelestialHandler.MoonPhase currentPhase, EnumSet<CelestialHandler.CelestialEvent> specialEvents);

    public static AppearanceCondition buildChainedCondition(AppearanceCondition... conditions) {
        return new SimpleConditionChain(Arrays.asList(conditions));
    }

    static class SimpleConditionChain implements AppearanceCondition {

        private List<AppearanceCondition> otherConditions;

        public SimpleConditionChain(List<AppearanceCondition> otherConditions) {
            this.otherConditions = otherConditions;
        }

        @Override
        public boolean shouldAppearThisNight(CelestialHandler.MoonPhase currentPhase, EnumSet<CelestialHandler.CelestialEvent> specialEvents) {
            for (AppearanceCondition cond : otherConditions) {
                if(cond == null) continue;

                if(!cond.shouldAppearThisNight(currentPhase, specialEvents))
                    return false;
            }
            return true;
        }
    }

    public static class ConditionBetweenPhases implements AppearanceCondition {

        private final CelestialHandler.MoonPhase beginningPhaseIncl, endingPhaseIncl;

        public ConditionBetweenPhases(CelestialHandler.MoonPhase beginningPhaseIncl, CelestialHandler.MoonPhase endingPhaseIncl) {
            this.beginningPhaseIncl = beginningPhaseIncl;
            this.endingPhaseIncl = endingPhaseIncl;
        }

        @Override
        public boolean shouldAppearThisNight(CelestialHandler.MoonPhase currentPhase, EnumSet<CelestialHandler.CelestialEvent> specialEvents) {
            int eOrdinal = endingPhaseIncl.ordinal();
            int bOrdinal = beginningPhaseIncl.ordinal();
            int cOrdinal = currentPhase.ordinal();
            if (bOrdinal > eOrdinal) {
                int mov = CelestialHandler.MoonPhase.values().length - bOrdinal;
                bOrdinal += mov;
                eOrdinal += mov;
                cOrdinal += mov;
            }
            return cOrdinal >= bOrdinal && cOrdinal <= eOrdinal;
        }

    }

}
