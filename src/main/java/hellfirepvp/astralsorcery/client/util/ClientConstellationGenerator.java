/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.util.word.RandomWordGenerator;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientConstellationGenerator
 * Created by HellFirePvP
 * Date: 29.09.2018 / 11:21
 */
@SideOnly(Side.CLIENT)
public class ClientConstellationGenerator {

    public static ClientConstellation generateRandom(long seed) {
        Random sRandom = new Random(seed);
        int stars = 5 + (sRandom.nextFloat() > 0.6F ? 1 : 0);
        return generateRandom(seed, stars);
    }

    public static ClientConstellation generateRandom(long seed, int stars) {
        Random sRandom = new Random(seed);
        String name = RandomWordGenerator.getGenerator().generateWord(seed, sRandom.nextFloat() > 0.6F ? 7 : 6);
        ClientConstellation cst = new ClientConstellation(name);
        List<StarLocation> tmpStars = Lists.newArrayList();
        List<StarConnection> tmpConnections = Lists.newArrayList();
        for (int i = 0; i < stars; i++) {
            Point newPoint = pickStarPoint(
                    sRandom,
                    tmpStars,
                    6);
            tmpStars.add(new StarLocation(newPoint.x, newPoint.y));
        }
        Iterator<StarLocation> it = tmpStars.iterator();
        while (it.hasNext()) {
            StarLocation sl = it.next();
            StarLocation other = findConnection(sRandom, sl, tmpStars, tmpConnections);
            if (other == null) {
                it.remove();
                continue;
            }
            tmpConnections.add(new StarConnection(sl, other));
        }
        tmpStars.forEach(s -> cst.addStar(s.x, s.y));
        tmpConnections.forEach(c -> {
            if (cst.getStars().contains(c.to) &&
                    cst.getStars().contains(c.from)) {
                cst.addConnection(c.from, c.to);
            }
        });
        return cst;
    }

    private static StarLocation findConnection(Random rand, StarLocation sl, List<StarLocation> stars, List<StarConnection> existingConnections) {
        List<StarLocation> others = Lists.newArrayList(stars);
        others.remove(sl);
        if (others.isEmpty()) return null;
        Collections.shuffle(others, rand);
        lblStars:
        for (StarLocation other : others) {
            StarConnection conn = new StarConnection(sl, other);
            for (StarConnection otherConnection : existingConnections) {
                if (intersect(conn, otherConnection)) {
                    continue lblStars;
                }
            }
            return other;
        }
        return null;
    }

    private static boolean intersect(StarConnection sc1, StarConnection sc2) {
        return isIntersecting(sc1, sc2.from.asPoint()) || isIntersecting(sc1, sc2.to.asPoint());
    }

    private static boolean isTouching(StarConnection part, Point p) {
        StarConnection originPart = new StarConnection(
                new StarLocation(0, 0),
                new StarLocation(part.to.x - part.from.x, part.to.y - part.from.y));
        Point originOffset = new Point(p.x - part.from.x, p.y - part.from.y);
        int cr = cross(originPart.to.asPoint(), originOffset);
        return Math.abs(cr) < 10;
    }

    private static boolean isIntersecting(StarConnection part, Point p) {
        StarConnection originPart = new StarConnection(
                new StarLocation(0, 0),
                new StarLocation(part.to.x - part.from.x, part.to.y - part.from.y));
        Point originOffset = new Point(p.x - part.from.x, p.y - part.from.y);
        return cross(originPart.to.asPoint(), originOffset) < 0;
    }

    private static int cross(Point p1, Point p2) {
        return p1.x * p2.y - p2.x * p1.y;
    }

    private static Point pickStarPoint(Random rand, List<StarLocation> occupied, float minDst) {
        lblSearch:
        while (true) {
            Point opt = new Point(rand.nextInt(IConstellation.STAR_GRID_SIZE - 6), rand.nextInt(IConstellation.STAR_GRID_SIZE - 6));
            opt.translate(3, 3);

            for (StarLocation other : occupied) {
                if (opt.distance(other.asPoint()) < minDst) {
                    continue lblSearch;
                }
            }

            return opt;
        }
    }

    public static class ClientConstellation implements IConstellation {

        private List<StarLocation> starLocations = Lists.newArrayList();
        private List<StarConnection> connections = Lists.newArrayList();

        private final String localizedName;
        private KnowledgeFragment associatedFragment;

        private ClientConstellation(String localizedName) {
            this.localizedName = localizedName;
        }

        public void setFragment(KnowledgeFragment fragment) {
            this.associatedFragment = fragment;
        }

        public KnowledgeFragment getFragment() {
            return associatedFragment;
        }

        @Override
        public StarLocation addStar(int x, int y) {
            x %= (IConstellation.STAR_GRID_SIZE - 1); //31x31
            y %= (IConstellation.STAR_GRID_SIZE - 1);
            StarLocation star = new StarLocation(x, y);
            if (!starLocations.contains(star)) {
                starLocations.add(star);
                return star;
            }
            return null;
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
        public List<StarLocation> getStars() {
            return Collections.unmodifiableList(starLocations);
        }

        @Override
        public List<StarConnection> getStarConnections() {
            return Collections.unmodifiableList(connections);
        }

        @Override
        public String getSimpleName() {
            return this.localizedName;
        }

        @Override
        public String getUnlocalizedName() {
            return this.localizedName;
        }

        @Override
        public List<ItemHandle> getConstellationSignatureItems() {
            return Lists.newArrayList();
        }

        @Override
        public IConstellation addSignatureItem(ItemHandle item) {
            return this;
        }

        @Override
        public Color getConstellationColor() {
            return IConstellation.weak;
        }
    }

}
