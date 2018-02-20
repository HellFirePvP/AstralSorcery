/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.mappings;

import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
@SideOnly(Side.CLIENT)
public class ClientJournalMapping {

    private static Map<ResearchProgression, JournalCluster> map = new HashMap<>();

    public static void init() {
        BindableResource cloudDsc = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "cloud5");
        map.put(ResearchProgression.DISCOVERY, new JournalCluster(cloudDsc, cloudDsc, -2, -2, 0, 0));

        BindableResource cloudCraft = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "cloud2");
        map.put(ResearchProgression.BASIC_CRAFT, new JournalCluster(cloudCraft, cloudCraft, 0, 1, 3, 3));

        BindableResource cloudAtt = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "cloud4");
        map.put(ResearchProgression.ATTUNEMENT, new JournalCluster(cloudAtt, cloudAtt, 2, -2, 4, 0));

        BindableResource cloudCst = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "cloud1");
        map.put(ResearchProgression.CONSTELLATION, new JournalCluster(cloudCst, cloudCst, 4, 0, 7, 2));

        BindableResource cloudRd = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "cloud3");
        map.put(ResearchProgression.RADIANCE, new JournalCluster(cloudRd, cloudRd, 5, -3, 8, -1));

        //BindableResource cloudTr = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "cloud3");
        //map.put(ResearchProgression.TRAIT_CRAFT, new JournalCluster(cloudTr, cloudTr, 0, 0, 2, 2));
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
