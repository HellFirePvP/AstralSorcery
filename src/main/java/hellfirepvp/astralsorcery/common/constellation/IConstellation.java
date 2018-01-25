/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
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
    static final Color weak  = new Color(67, 44, 176);
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

    public List<ItemHandle> getConstellationSignatureItems();

    public IConstellation addSignatureItem(ItemHandle item);

    public Color getConstellationColor();

    default public Color getTierRenderColor() {
        if(this instanceof IMinorConstellation) {
            return minor;
        }
        if(this instanceof IMajorConstellation) {
            return major;
        }
        return weak;
    }

    //@Nullable
    //default public ISpellEffect getSpellEffect() {
    //    return SpellEffectRegistry.getSpellEffect(this);
    //}

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
