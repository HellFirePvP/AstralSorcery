package hellfire.astralSorcery.common.constellation;

import hellfire.astralSorcery.api.AstralSorceryAPI;
import hellfire.astralSorcery.api.constellation.IConstellation;
import hellfire.astralSorcery.api.constellation.StarConnection;
import hellfire.astralSorcery.api.constellation.StarLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 06.02.2016 01:42
 */
public class Constellation implements IConstellation {

    private List<StarLocation> starLocations = new ArrayList<StarLocation>(); //32x32 locations are valid. 0-indexed.
    private List<StarConnection> connections = new ArrayList<StarConnection>(); //The connections between 2 tuples/stars in the constellation.

    @Override
    public StarLocation addStar(int x, int y) {
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
        AstralSorceryAPI.registerConstellation(tier, this);
    }

    public List<StarLocation> getStarLocations() {
        return starLocations;
    }

    public List<StarConnection> getConnections() {
        return connections;
    }

}
