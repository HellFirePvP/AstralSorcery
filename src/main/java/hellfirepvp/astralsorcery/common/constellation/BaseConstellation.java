/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BaseConstellation
 * Created by HellFirePvP
 * Date: 18.10.2020 / 20:09
 */
public abstract class BaseConstellation extends ForgeRegistryEntry<IConstellation> implements IConstellation {

    private final List<StarLocation> starLocations = new ArrayList<>(); //31x31 locations are valid. 0-indexed.
    private final List<StarConnection> connections = new ArrayList<>(); //The connections between 2 tuples/stars in the constellation.
    private AbstractRenderableTexture background;
    private RenderType renderType;

    @Override
    public StarLocation addStar(int x, int y) {
        x %= STAR_GRID_INDEX; //31x31
        y %= STAR_GRID_INDEX;
        StarLocation star = new StarLocation(x, y);
        if (!starLocations.contains(star)) {
            starLocations.add(star);
            return star;
        }
        return null;
    }

    @Override
    public IConstellation setTexture(AbstractRenderableTexture tex) {
        background = tex;
        return this;
    }

    @Override
    public IConstellation setRenderType(RenderType type) {
        renderType = type;
        return this;
    }

    @Override
    public StarConnection addConnection(StarLocation star1, StarLocation star2) {
        if (star1.equals(star2)) return null;
        StarConnection sc = new StarConnection(star1, star2);
        if (!connections.contains(sc)) {
            connections.add(sc);
            return sc;
        }
        return null;
    }

    @Override
    public AbstractRenderableTexture getTexture() {
        return background;
    }

    @Override
    public RenderType getRenderType() {
        return renderType;
    }

    @Override
    public List<StarLocation> getStars() {
        return Collections.unmodifiableList(this.starLocations);
    }

    @Override
    public List<StarConnection> getStarConnections() {
        return Collections.unmodifiableList(this.connections);
    }
}
