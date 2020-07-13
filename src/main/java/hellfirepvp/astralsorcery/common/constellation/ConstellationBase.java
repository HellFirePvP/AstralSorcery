/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
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
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationBase
 * Created by HellFirePvP
 * Date: 01.06.2019 / 15:59
 */
public abstract class ConstellationBase extends ForgeRegistryEntry<IConstellation> implements IConstellation {

    private static int counter = 0;

    private List<StarLocation> starLocations = new ArrayList<>(); //31x31 locations are valid. 0-indexed.
    private List<StarConnection> connections = new ArrayList<>(); //The connections between 2 tuples/stars in the constellation.
    private List<Ingredient> signatureItems = new LinkedList<>();

    private final String name, simpleName;
    private final Color color;
    private final int id;

    public ConstellationBase(String name) {
        this(name, ColorsAS.CONSTELLATION_TYPE_MAJOR);
    }

    public ConstellationBase(String name, Color color) {
        this.id = counter++;
        this.simpleName = name;
        ModContainer mod = MiscUtils.getCurrentlyActiveMod();
        if (mod != null) {
            this.setRegistryName(new ResourceLocation(mod.getModId(), name));
            this.name = mod.getModId() + ".constellation." + name;
        } else {
            this.setRegistryName(AstralSorcery.key(name));
            this.name = "unknown.constellation." + name;
        }
        this.color = color;
    }

    public StarLocation addStar(int x, int y) {
        x %= (STAR_GRID_INDEX - 1); //31x31
        y %= (STAR_GRID_INDEX - 1);
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

    public ConstellationBase addSignatureItem(Ingredient handle) {
        this.signatureItems.add(handle);
        return this;
    }

    @Override
    public List<Ingredient> getConstellationSignatureItems() {
        return Collections.unmodifiableList(this.signatureItems);
    }

    public boolean canDiscover(PlayerEntity player, PlayerProgress progress) {
        return true;
        //return !Mods.GAMESTAGES.isPresent() ||
        //        (player != null && canDiscoverGameStages(player, progress));
    }

    //Guess we can only config one at a time...
    /*
    @Optional.Method(modid = "gamestages")
    final boolean canDiscoverGameStages(PlayerEntity player, PlayerProgress progress) {
        return !Mods.CRAFTTWEAKER.isPresent() || canDiscoverGameStagesCraftTweaker(player, progress);
    }

    @Optional.Method(modid = "crafttweaker")
    private boolean canDiscoverGameStagesCraftTweaker(PlayerEntity player, PlayerProgress progress) {
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
    public int getSortingId() {
        return this.id;
    }

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
    public String getTranslationKey() {
        return this.name;
    }

    @Override
    public String getSimpleName() {
        return simpleName;
    }

    @Override
    public String toString() {
        return "Constellation={name:" + this.name + "}";
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

    @Override
    public int compareTo(IConstellation o) {
        return Integer.compare(this.getSortingId(), o.getSortingId());
    }

    public static class Major extends Weak implements IMajorConstellation {

        public Major(String name) {
            super(name);
        }

        public Major(String name, Color color) {
            super(name, color);
        }

        @Override
        public boolean canDiscover(PlayerEntity player, PlayerProgress progress) {
            return true;
        }

        //@Override
        //public boolean canDiscover(PlayerEntity player, PlayerProgress progress) {
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

        @Override
        public boolean canDiscover(PlayerEntity player, PlayerProgress progress) {
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
                if (ph == null) {
                    throw new IllegalArgumentException("null MoonPhase passed to Minor constellation registration for " + name);
                }
                phases.add(ph);
            }
        }

        public Minor(String name, Color color, MoonPhase... applicablePhases) {
            super(name, color);
            phases = new ArrayList<>(applicablePhases.length);
            for (MoonPhase ph : applicablePhases) {
                if (ph == null) {
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
                if (!shifted.contains(offset)) {
                    shifted.add(offset);
                }
            }
            return shifted;
        }

        @Override
        public boolean canDiscover(PlayerEntity player, PlayerProgress progress) {
            return super.canDiscover(player, progress) &&
                    progress.wasOnceAttuned() &&
                    progress.getTierReached().isThisLaterOrEqual(ProgressionTier.TRAIT_CRAFT);
        }
    }
}

