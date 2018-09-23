/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.fragment;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Nonnull
    @SideOnly(Side.CLIENT)
    public Collection<KnowledgeFragment> getAllFragmentsFor(GuiScreenJournal journal) {
        return this.fragments.values().stream().filter(k -> k.isVisible(journal)).collect(Collectors.toList());
    }

}
