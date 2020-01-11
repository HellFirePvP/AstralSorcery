/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryKnowledgeFragments
 * Created by HellFirePvP
 * Date: 17.07.2019 / 17:29
 */
public class RegistryKnowledgeFragments {

    private RegistryKnowledgeFragments() {}

    public static void init() {

    }

    private static <T extends KnowledgeFragment> T register(T fragment) {
        AstralSorcery.getProxy().getRegistryPrimer().register(fragment);
        return fragment;
    }

}
