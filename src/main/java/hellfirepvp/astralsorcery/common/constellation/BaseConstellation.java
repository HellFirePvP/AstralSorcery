/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.property.ConstellationProperty;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchTier;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import hellfirepvp.astralsorcery.common.util.data.CacheSupplier;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BaseConstellation
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class BaseConstellation implements Comparable<BaseConstellation> {

    public static int STAR_GRID_WIDTH_HEIGHT = 32; // 1-indexed
    private static int sortingCounter = 0;

    final List<StarLocation> starLocations = new ArrayList<>(); //31x31 locations are valid. 0-indexed.
    final List<StarConnection> connections = new ArrayList<>(); //The connections between 2 tuples/stars in the constellation.
    final Map<ConstellationProperty.Key<?>, ConstellationProperty<?>> properties = new HashMap<>();

    int sortingId = -1;
    Tier tier = Tier.MAJOR;

    private final ColorWrapper constellationColor;
    private final Supplier<String> unlocalizedName;
    private final CacheSupplier<Holder<BaseConstellation>> holderReference;

    public BaseConstellation(ColorWrapper constellationColor) {
        this.constellationColor = constellationColor;
        this.unlocalizedName = NameUtil.cacheName("constellation", RegistriesAS.REGISTRY_CONSTELLATIONS, this);
        this.holderReference = new CacheSupplier<>(() -> {
            ResourceLocation cstKey = RegistriesAS.REGISTRY_CONSTELLATIONS.getKey(this);
            if (cstKey == null) return null;
            return RegistriesAS.REGISTRY_CONSTELLATIONS.getHolder(cstKey).orElse(null);
        });
    }

    public ColorWrapper getConstellationColor() {
        return this.constellationColor;
    }

    public MutableComponent getName() {
        return Component.translatable(this.unlocalizedName.get());
    }

    public MutableComponent getColoredName() {
        return this.getName().withColor(this.constellationColor.getColor());
    }

    public Tier getTier() {
        return this.tier;
    }

    public boolean canDiscover(Player player, PlayerProgress progress) {
        return this.getTier().canDiscover(player, progress);
    }

    public List<StarLocation> getStars() {
        return Collections.unmodifiableList(this.starLocations);
    }

    public List<StarConnection> getStarConnections() {
        return Collections.unmodifiableList(this.connections);
    }

    public boolean hasProperty(ConstellationProperty.Key<?> propertyKey) {
        return this.properties.containsKey(propertyKey);
    }

    @Nullable
    public <T extends ConstellationProperty<T>> T getProperty(ConstellationProperty.Key<T> propertyKey) {
        return (T) this.properties.get(propertyKey);
    }

    public <T extends ConstellationProperty<T>> Optional<T> getPropertyOpt(ConstellationProperty.Key<T> propertyKey) {
        return Optional.ofNullable(this.getProperty(propertyKey));
    }

    public int getSortingId() {
        return this.sortingId;
    }

    public Optional<Holder<BaseConstellation>> getHolder() {
        return Optional.ofNullable(this.holderReference.get());
    }

    public boolean is(TagKey<BaseConstellation> cstKey) {
        return this.getHolder().map(holder -> holder.is(cstKey)).orElse(false);
    }

    @Override
    public int compareTo(BaseConstellation o) {
        return Integer.compare(this.getSortingId(), o.getSortingId());
    }

    /**
     * 'name' should match the registry path of the constellation
     */
    public static <T extends BaseConstellation> Builder<T> builder(ColorWrapper color) {
        return new Builder<>(color);
    }

    public static class Builder<T extends BaseConstellation> {

        private final ColorWrapper color;
        private Tier tier = Tier.MAJOR;
        private final List<StarLocation> starLocations = new ArrayList<>();
        private final List<StarConnection> connections = new ArrayList<>();
        private final Map<ConstellationProperty.Key<?>, Function<T, ConstellationProperty<?>>> propertyBuilder = new HashMap<>();
        private boolean sorted = false;

        public Builder(ColorWrapper color) {
            this.color = color;
        }

        public StarLocation addStar(int x, int y) {
            if (x < 0 || x >= STAR_GRID_WIDTH_HEIGHT) {
                throw new IllegalArgumentException("X coordinate must be in range [0, " + STAR_GRID_WIDTH_HEIGHT + ")");
            }
            if (y < 0 || y >= STAR_GRID_WIDTH_HEIGHT) {
                throw new IllegalArgumentException("Y coordinate must be in range [0, " + STAR_GRID_WIDTH_HEIGHT + ")");
            }
            StarLocation star = new StarLocation(x, y);
            if (this.starLocations.contains(star)) {
                throw new IllegalArgumentException("Star already exists at " + x + ":" + y);
            }
            this.starLocations.add(star);
            return star;
        }

        public StarConnection addConnection(StarLocation star1, StarLocation star2) {
            if (star1.equals(star2)) return null;
            StarConnection sc = new StarConnection(star1, star2);
            if (this.connections.contains(sc)) {
                throw new IllegalArgumentException("Connection already exists between " + star1 + " and " + star2);
            }
            this.connections.add(sc);
            return sc;
        }

        public Builder<T> sorted() {
            this.sorted = true;
            return this;
        }

        public Builder<T> tier(Tier tier) {
            this.tier = tier;
            return this;
        }

        public <V extends ConstellationProperty<V>> Builder<T> withProperty(ConstellationProperty.Key<V> key, Function<T, V> propertyBuilder) {
            this.propertyBuilder.put(key, MiscUtil.cast(propertyBuilder));
            return this;
        }

        public Supplier<T> build(Function<ColorWrapper, T> ctor) {
            return () -> {
                T constellation = ctor.apply(this.color);
                constellation.tier = this.tier;
                constellation.starLocations.addAll(this.starLocations);
                constellation.connections.addAll(this.connections);
                this.propertyBuilder.forEach((key, builder) -> {
                    constellation.properties.put(key, builder.apply(constellation));
                });
                if (this.sorted) {
                    constellation.sortingId = sortingCounter++;
                }
                return constellation;
            };
        }
    }

    public static class Tier {

        public static final Tier MAJOR = new Tier("major", ColorsAS.CONSTELLATION_TYPE_MAJOR);
        public static final Tier MINOR = new Tier("minor", ColorsAS.CONSTELLATION_TYPE_MINOR);
        public static final Tier HIDDEN = new Tier("hidden", ColorsAS.CONSTELLATION_TYPE_HIDDEN);

        private final String typeName;
        private final ColorWrapper color;

        public Tier(String typeName, ColorWrapper color) {
            this.typeName = typeName;
            this.color = color;
        }

        public ColorWrapper getColor() {
            return color;
        }

        public String getTierTypeName() {
            return this.typeName;
        }

        public MutableComponent getTierName() {
            return Component.translatable(Util.makeDescriptionId("tome.constellation.tier", AstralSorcery.key(this.getTierTypeName())));
        }

        public boolean canDiscover(Player player, PlayerProgress progress) {
            if (this == HIDDEN) return false;

            if (this == MINOR) {
                return progress.getTierReached().isThisLaterOrEqual(ResearchTier.RESONANCE);
            }
            return true;
        }
    }
}
