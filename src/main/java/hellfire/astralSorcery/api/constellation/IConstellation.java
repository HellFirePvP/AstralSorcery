package hellfire.astralSorcery.api.constellation;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 06.02.2016 01:56
 */
public interface IConstellation {

    public StarLocation addStar(int x, int y);

    public StarConnection addConnection(StarLocation star1, StarLocation star2);

    public void register(int tier);

}
