/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.util.word.RandomWordGenerator;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.Ingredient;

import java.awt.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationGenerator
 * Created by HellFirePvP
 * Date: 18.10.2020 / 20:05
 */
public class ConstellationGenerator {

    public static GeneratedConstellation generateRandom(long seed) {
        Random sRandom = new Random(seed);
        int stars = 5 + (sRandom.nextFloat() > 0.6F ? 1 : 0);
        return generateRandom(seed, stars);
    }

    public static GeneratedConstellation generateRandom(long seed, int stars) {
        Random sRandom = new Random(seed);
        String name = RandomWordGenerator.getGenerator().generateWord(seed, sRandom.nextFloat() > 0.6F ? 7 : 6);
        GeneratedConstellation cst = new GeneratedConstellation(name);
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
            Point opt = new Point(rand.nextInt(IConstellation.STAR_GRID_INDEX - 6), rand.nextInt(IConstellation.STAR_GRID_INDEX - 6));
            opt.translate(3, 3);

            for (StarLocation other : occupied) {
                if (opt.distance(other.asPoint()) < minDst) {
                    continue lblSearch;
                }
            }

            return opt;
        }
    }

    public static class GeneratedConstellation extends BaseConstellation {

        private final String localizedName;

        private GeneratedConstellation(String localizedName) {
            this.localizedName = localizedName;
        }

        @Override
        public int getSortingId() {
            return 0;
        }

        @Override
        public String getSimpleName() {
            return localizedName;
        }

        @Override
        public String getTranslationKey() {
            return this.getSimpleName();
        }

        @Override
        public List<Ingredient> getConstellationSignatureItems() {
            return Collections.emptyList();
        }

        @Override
        public IConstellation addSignatureItem(Supplier<Ingredient> ingredient) {
            return this;
        }

        @Override
        public Color getConstellationColor() {
            return ColorsAS.CONSTELLATION_TYPE_WEAK;
        }

        @Override
        public boolean canDiscover(PlayerEntity player, PlayerProgress progress) {
            return true;
        }

        @Override
        public int compareTo(IConstellation o) {
            return 0;
        }
    }

}
