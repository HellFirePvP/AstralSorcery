/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal.progression;

import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JournalProgressionClusterMapping
 * Created by HellFirePvP
 * Date: 03.08.2019 / 17:25
 */
public class JournalProgressionClusterMapping {

    private static Map<ResearchProgression, JournalCluster> clusterMapping = new HashMap<>();

    static {
        AbstractRenderableTexture tex = TexturesAS.TEX_GUI_CLUSTER_DISCOVERY;
        register(ResearchProgression.DISCOVERY, new JournalCluster(tex, tex, -2, -2, 0, 0));

        tex = TexturesAS.TEX_GUI_CLUSTER_BASICCRAFT;
        register(ResearchProgression.BASIC_CRAFT, new JournalCluster(tex, tex, 0, 1, 3, 3));

        tex = TexturesAS.TEX_GUI_CLUSTER_ATTUNEMENT;
        register(ResearchProgression.ATTUNEMENT, new JournalCluster(tex, tex, 2, -2, 4, 0));

        tex = TexturesAS.TEX_GUI_CLUSTER_CONSTELLATION;
        register(ResearchProgression.CONSTELLATION, new JournalCluster(tex, tex, 4, 0, 7, 2));

        tex = TexturesAS.TEX_GUI_CLUSTER_RADIANCE;
        register(ResearchProgression.RADIANCE, new JournalCluster(tex, tex, 5, -3, 8, -1));

        tex = TexturesAS.TEX_GUI_CLUSTER_BRILLIANCE;
        register(ResearchProgression.BRILLIANCE, new JournalCluster(tex, tex, 8, -1, 10, 1));
    }

    public static void register(ResearchProgression prog, JournalCluster cluster) {
        clusterMapping.put(prog, cluster);
    }

    @Nonnull
    public static JournalCluster getClusterMapping(ResearchProgression progression) {
        JournalCluster cluster = clusterMapping.get(progression);
        if (cluster == null) {
            throw new IllegalArgumentException("ResearchProgression " + progression.name() + " has no registered cluster!");
        }
        return cluster;
    }

}
