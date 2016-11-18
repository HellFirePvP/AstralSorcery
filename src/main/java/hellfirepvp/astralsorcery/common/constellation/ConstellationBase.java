package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectRegistry;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationBase
 * Created by HellFirePvP
 * Date: 16.11.2016 / 23:07
 */
public abstract class ConstellationBase implements IConstellation {

    private List<StarLocation> starLocations = new ArrayList<>(); //32x32 locations are valid. 0-indexed.
    private List<StarConnection> connections = new ArrayList<>(); //The connections between 2 tuples/stars in the constellation.

    private final String name;

    public ConstellationBase(String name) {
        ModContainer mod = Loader.instance().activeModContainer();
        if(mod != null) {
            this.name = mod.getModId() + ".constellation." + name;
        } else {
            this.name = "unknown.constellation." + name;
        }
    }

    public StarLocation addStar(int x, int y) {
        x &= 31; //32x32
        y &= 31;
        StarLocation star = new StarLocation(x, y);
        if (!starLocations.contains(star)) {
            starLocations.add(star);
            return star;
        }
        return null;
    }

    public StarConnection addConnection(StarLocation star1, StarLocation star2) {
        if (star1.equals(star2)) return null;
        StarConnection sc = new StarConnection(star1, star2);
        if (!connections.contains(sc)) {
            connections.add(sc);
            return sc;
        }
        return null;
    }

    @Override
    public List<StarLocation> getStars() {
        return Collections.unmodifiableList(starLocations);
    }

    @Override
    public List<StarConnection> getStarConnections() {
        return Collections.unmodifiableList(connections);
    }

    @Override
    public String getUnlocalizedName() {
        return name;
    }

    @Override
    public String toString() {
        return "Constellation={name:" + getUnlocalizedName() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstellationBase that = (ConstellationBase) o;
        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public static class Major extends ConstellationBase implements IMajorConstellation {

        public Major(String name) {
            super(name);
        }

        @Override
        public ConstellationEffect getRitualEffect() {
            return ConstellationEffectRegistry.getEffect(this);
        }

    }

    public static class Minor extends ConstellationBase implements IMinorConstellation {

        private final List<MoonPhase> phases;

        public Minor(String name, MoonPhase... applicablePhases) {
            super(name);
            phases = new ArrayList<>(applicablePhases.length);
            for (MoonPhase ph : applicablePhases) {
                if(ph == null) {
                    throw new IllegalArgumentException("[AstralSorcery] null MoonPhase passed to Minor constellation registration for " + name);
                }
                phases.add(ph);
            }
        }

        @Override
        public List<MoonPhase> getShowupMoonPhases() {
            return Collections.unmodifiableList(phases);
        }

    }

}
