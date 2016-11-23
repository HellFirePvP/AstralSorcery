package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IConstellation
 * Created by HellFirePvP
 * Date: 16.11.2016 / 23:04
 */
public interface IConstellation {

    static final Color major = new Color(40, 67, 204);
    static final Color minor = new Color(93, 25, 127);

    /**
     * Should only be called before registering the Constellation.
     */
    public StarLocation addStar(int x, int y);

    /**
     * Should only be called before registering the Constellation.
     */
    public StarConnection addConnection(StarLocation star1, StarLocation star2);

    public List<StarLocation> getStars();

    public List<StarConnection> getStarConnections();

    public String getUnlocalizedName();

    default public String getUnlocalizedInfo() {
        return getUnlocalizedName() + ".info";
    }

    public static String getDefaultSaveKey() {
        return "constellationName";
    }

    default public Color getRenderColor() {
        if(this instanceof IMinorConstellation) {
            return minor;
        }
        return major;
    }

    default public boolean canDiscover(PlayerProgress progress) {
        return true;
    }

    default public void writeToNBT(NBTTagCompound compound) {
        writeToNBT(compound, getDefaultSaveKey());
    }

    default public void writeToNBT(NBTTagCompound compound, String key) {
        compound.setString(key, getUnlocalizedName());
    }

    @Nullable
    public static IConstellation readFromNBT(NBTTagCompound compound) {
        return readFromNBT(compound, getDefaultSaveKey());
    }

    @Nullable
    public static IConstellation readFromNBT(NBTTagCompound compound, String key) {
        return ConstellationRegistry.getConstellationByName(compound.getString(key));
    }

}
