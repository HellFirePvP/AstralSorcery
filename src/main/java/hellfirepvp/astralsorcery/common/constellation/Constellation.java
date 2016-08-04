package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Constellation
 * Created by HellFirePvP
 * Date: 06.02.2016 01:42
 */
public class Constellation {

    private boolean finished = false;
    private String name = null;
    private int tier = -1;
    private List<StarLocation> starLocations = new ArrayList<>(); //32x32 locations are valid. 0-indexed.
    private List<StarConnection> connections = new ArrayList<>(); //The connections between 2 tuples/stars in the constellation.

    private List<StarLocation> unmodifiableStars;
    private List<StarConnection> unmodifiableConnections;

    public StarLocation addStar(int x, int y) {
        if (finished) return null;
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
        if (finished) return null;
        if (star1.equals(star2)) return null;
        StarConnection sc = new StarConnection(star1, star2);
        if (!connections.contains(sc)) {
            connections.add(sc);
            return sc;
        }
        return null;
    }

    public void register(String name, int tier) {
        registerModConstellation(Loader.instance().activeModContainer(), name, tier);
    }

    public void registerModConstellation(ModContainer mod, String name, int tier) {
        ConstellationRegistry.registerConstellation(tier, this);
        this.tier = tier;
        if (mod == null) {
            this.name = "unknown." + name;
        } else {
            this.name = mod.getModId() + ".constellation." + name;
        }
        finished = true;
    }

    public Tier queryTier() {
        if (!finished) return null;

        return ConstellationRegistry.getTier(getAssociatedTier());
    }

    public String getName() {
        return name;
    }

    public int getAssociatedTier() {
        return tier;
    }

    public List<StarLocation> getStars() {
        if (!finished) return null;
        if (unmodifiableStars != null) return unmodifiableStars;
        return (unmodifiableStars = Collections.unmodifiableList(starLocations));
    }

    public List<StarConnection> getConnections() {
        if (!finished) return null;
        if (unmodifiableConnections != null) return unmodifiableConnections;
        return (unmodifiableConnections = Collections.unmodifiableList(connections));
    }

    public void writeToNBT(NBTTagCompound compound) {
        compound.setString("constellationName", getName());
    }

    public static Constellation readFromNBT(NBTTagCompound compound) {
        return ConstellationRegistry.getConstellationByName(compound.getString("constellationName"));
    }

}
