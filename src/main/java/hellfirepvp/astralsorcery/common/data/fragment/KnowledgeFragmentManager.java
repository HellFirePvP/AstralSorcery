/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.fragment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KnowledgeFragmentManager
 * Created by HellFirePvP
 * Date: 22.09.2018 / 17:57
 */
public class KnowledgeFragmentManager {

    private static KnowledgeFragmentManager INSTANCE = new KnowledgeFragmentManager();
    private Map<ResourceLocation, KnowledgeFragment> fragments = Maps.newHashMap();

    private KnowledgeFragmentManager() {}

    public static KnowledgeFragmentManager getInstance() {
        return INSTANCE;
    }

    public KnowledgeFragment register(KnowledgeFragment frag) {
        fragments.put(frag.getRegistryName(), frag);
        return frag;
    }

    @Nullable
    public KnowledgeFragment getFragment(ResourceLocation name) {
        return fragments.get(name);
    }

    public List<KnowledgeFragment> getAllFragments() {
        return Lists.newArrayList(fragments.values());
    }

}
