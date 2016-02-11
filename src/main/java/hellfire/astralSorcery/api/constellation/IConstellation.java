package hellfire.astralSorcery.api.constellation;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 06.02.2016 01:56
 */
public interface IConstellation {

    public static final String CONSTELLATION_TRANSLATOR = "constellation.%s.name";

    public StarLocation addStar(int x, int y);

    public StarConnection addConnection(StarLocation star1, StarLocation star2);

    /**
     * Finishes and registers the constellation to the specified tier.
     *
     * @param name the unique and never-changing name of the constellation.
     *             will be prepended by the currently active mod or "unknown" if no active mod is specified...
     * @param tier the tier to register it to.
     */
    public void register(String name, int tier);

    public String getName();

    public int getAssociatedTier();

    public List<StarLocation> getStars();

    public List<StarConnection> getConnections();

}
