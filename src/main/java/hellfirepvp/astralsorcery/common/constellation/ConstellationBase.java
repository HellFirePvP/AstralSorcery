/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.base.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationBase
 * Created by HellFirePvP
 * Date: 01.06.2019 / 15:59
 */
public abstract class ConstellationBase extends ForgeRegistryEntry<IConstellation> implements IConstellation {

    private List<StarLocation> starLocations = new ArrayList<>(); //31x31 locations are valid. 0-indexed.
    private List<StarConnection> connections = new ArrayList<>(); //The connections between 2 tuples/stars in the constellation.
    private List<ItemHandle> signatureItems = new LinkedList<>();

    private final String name, simpleName;
    private final Color color;

    public ConstellationBase(String name) {
        this(name, IConstellation.major);
    }

    public ConstellationBase(String name, Color color) {
        this.simpleName = name.toLowerCase();
        ModContainer mod = MiscUtils.getCurrentlyActiveMod();
        if(mod != null) {
            this.setRegistryName(new ResourceLocation(mod.getModId(), name));
            this.name = mod.getModId() + ".constellation." + name;
        } else {
            this.setRegistryName(new ResourceLocation(AstralSorcery.MODID, name));
            this.name = "unknown.constellation." + name;
        }
        this.color = color;
    }

    public StarLocation addStar(int x, int y) {
        x %= (STAR_GRID_SIZE - 1); //31x31
        y %= (STAR_GRID_SIZE - 1);
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

    public ConstellationBase addSignatureItem(ItemHandle handle) {
        this.signatureItems.add(handle);
        return this;
    }

    @Override
    public List<ItemHandle> getConstellationSignatureItems() {
        return Collections.unmodifiableList(this.signatureItems);
    }

    public boolean canDiscover(EntityPlayer player, PlayerProgress progress) {
        return true;
        //return !Mods.GAMESTAGES.isPresent() ||
        //        (player != null && canDiscoverGameStages(player, progress));
    }

    //Guess we can only config one at a time...
    /*
    @Optional.Method(modid = "gamestages")
    final boolean canDiscoverGameStages(EntityPlayer player, PlayerProgress progress) {
        return !Mods.CRAFTTWEAKER.isPresent() || canDiscoverGameStagesCraftTweaker(player, progress);
    }

    @Optional.Method(modid = "crafttweaker")
    private boolean canDiscoverGameStagesCraftTweaker(EntityPlayer player, PlayerProgress progress) {
        if (player == null) {
            return false;
        }
        IStageData data = GameStageHelper.getPlayerData(player);
        if (data == null) {
            return false;
        }
        return GameStageTweaks.canDiscover(data.getStages(), name);
    }
    */

    @Override
    public Color getConstellationColor() {
        return color;
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
    public String getSimpleName() {
        return simpleName;
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
        return Objects.equals(getRegistryName(), that.getRegistryName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRegistryName());
    }

    public static class Major extends Weak implements IMajorConstellation {

        public Major(String name) {
            super(name);
        }

        public Major(String name, Color color) {
            super(name, color);
        }

        //@Override
        //public boolean canDiscover(EntityPlayer player, PlayerProgress progress) {
        //    return !Mods.GAMESTAGES.isPresent() ||
        //            (player != null && canDiscoverGameStages(player, progress));
        //}
    }

    public static class Weak extends ConstellationBase implements IWeakConstellation {

        public Weak(String name) {
            super(name);
        }

        public Weak(String name, Color color) {
            super(name, color);
        }

        @Nullable
        @Override
        public ConstellationEffect getRitualEffect(ILocatable origin) {
            return ConstellationEffectRegistry.getEffect(this, origin);
        }

        @Override
        public boolean canDiscover(EntityPlayer player, PlayerProgress progress) {
            return super.canDiscover(player, progress) &&
                    progress.getTierReached().isThisLaterOrEqual(ProgressionTier.ATTUNEMENT) &&
                    progress.wasOnceAttuned();
        }
    }

    public static abstract class WeakSpecial extends Weak implements IConstellationSpecialShowup {

        public WeakSpecial(String name) {
            super(name);
        }

        public WeakSpecial(String name, Color color) {
            super(name, color);
        }
    }

    public static class Minor extends ConstellationBase implements IMinorConstellation {

        private final List<MoonPhase> phases;

        public Minor(String name, MoonPhase... applicablePhases) {
            super(name);
            phases = new ArrayList<>(applicablePhases.length);
            for (MoonPhase ph : applicablePhases) {
                if(ph == null) {
                    throw new IllegalArgumentException("null MoonPhase passed to Minor constellation registration for " + name);
                }
                phases.add(ph);
            }
        }

        public Minor(String name, Color color, MoonPhase... applicablePhases) {
            super(name, color);
            phases = new ArrayList<>(applicablePhases.length);
            for (MoonPhase ph : applicablePhases) {
                if(ph == null) {
                    throw new IllegalArgumentException("null MoonPhase passed to Minor constellation registration for " + name);
                }
                phases.add(ph);
            }
        }

        @Override
        public List<MoonPhase> getShowupMoonPhases(long rSeed) {
            List<MoonPhase> shifted = new ArrayList<>(phases.size());
            for (MoonPhase mp : this.phases) {
                int index = mp.ordinal() + (((int) (rSeed % MoonPhase.values().length)) + MoonPhase.values().length);
                while (index >= MoonPhase.values().length) {
                    index -= MoonPhase.values().length;
                }
                index = MathHelper.clamp(index, 0, MoonPhase.values().length - 1);
                MoonPhase offset = MoonPhase.values()[index];
                if(!shifted.contains(offset)) {
                    shifted.add(offset);
                }
            }
            return shifted;
        }

        @Override
        public boolean canDiscover(EntityPlayer player, PlayerProgress progress) {
            return super.canDiscover(player, progress) &&
                    progress.wasOnceAttuned() &&
                    progress.getTierReached().isThisLaterOrEqual(ProgressionTier.TRAIT_CRAFT);
        }
    }

}

