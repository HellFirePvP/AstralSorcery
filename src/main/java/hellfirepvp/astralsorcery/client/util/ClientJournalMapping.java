package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientJournalMapping
 * Created by HellFirePvP
 * Date: 13.08.2016 / 11:13
 */
public class ClientJournalMapping {

    private static Map<ResearchProgression, JournalCluster> map = new HashMap<>();

    public static void init() {
        BindableResource cloud1 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiCloud1");
        map.put(ResearchProgression.TEST_PROGRESS, new JournalCluster(cloud1, cloud1, -2, -2, 0, -1));
    }

    public static JournalCluster getClusterMapping(ResearchProgression progression) {
        return map.get(progression);
    }

    public static class JournalCluster {

        public final BindableResource cloudTexture, clusterBackgroundTexture;
        public final Point boundary1, boundary2;
        public final int leftMost, rightMost, upperMost, lowerMost;

        /** Keep in mind: negative coords are left upper side of the GUI
         *
         *  uppermost = most negative Y, lowermost = most positive Y
         *  leftmost =  most negative X, rightmost = most positive X
         *
         *  A wrong definition doesn't affect size calculation, but rendering.
         */
        public JournalCluster(BindableResource cloudTexture, BindableResource clusterBackgroundTexture, int leftMost, int upperMost, int rightMost, int lowerMost) {
            this.cloudTexture = cloudTexture;
            this.clusterBackgroundTexture = clusterBackgroundTexture;
            this.leftMost = leftMost;
            this.upperMost = upperMost;
            this.rightMost = rightMost;
            this.lowerMost = lowerMost;
            this.boundary1 = new Point(leftMost, upperMost);
            this.boundary2 = new Point(rightMost, lowerMost);
        }

    }
}
