package hellfire.astralSorcery.common.constellation;

import hellfire.astralSorcery.api.AstralSorceryAPI;
import hellfire.astralSorcery.api.constellation.IConstellation;
import hellfire.astralSorcery.api.constellation.IConstellationTier;
import hellfire.astralSorcery.api.constellation.StarConnection;
import hellfire.astralSorcery.api.constellation.StarLocation;
import hellfire.astralSorcery.common.util.Vector3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 06.02.2016 01:42
 */
public class Constellation implements IConstellation {

    private boolean finished = false;
    private List<StarLocation> starLocations = new ArrayList<StarLocation>(); //32x32 locations are valid. 0-indexed.
    private List<StarConnection> connections = new ArrayList<StarConnection>(); //The connections between 2 tuples/stars in the constellation.
    private List<StarLocation> unmodifiableStars;
    private List<StarConnection> unmodifiableConnections;

    @Override
    public StarLocation addStar(int x, int y) {
        if(finished) return null;
        x &= 31; //32x32
        y &= 31;
        StarLocation star = new StarLocation(x, y);
        if(!starLocations.contains(star)) {
            starLocations.add(star);
            return star;
        }
        return null;
    }

    @Override
    public StarConnection addConnection(StarLocation star1, StarLocation star2) {
        if(finished) return null;
        if(star1.equals(star2)) return null;
        StarConnection sc = new StarConnection(star1, star2);
        if(!connections.contains(sc)) {
            connections.add(sc);
            return sc;
        }
        return null;
    }

    @Override
    public void register(int tier) {
        finished = true;
        AstralSorceryAPI.registerConstellation(tier, this);
    }

    @Override
    public List<StarLocation> getStars() {
        if(!finished) return null;
        if(unmodifiableStars != null) return unmodifiableStars;
        return (unmodifiableStars = Collections.unmodifiableList(starLocations));
    }

    @Override
    public List<StarConnection> getConnections() {
        if(!finished) return null;
        if(unmodifiableConnections != null) return unmodifiableConnections;
        return (unmodifiableConnections = Collections.unmodifiableList(connections));
    }

}
