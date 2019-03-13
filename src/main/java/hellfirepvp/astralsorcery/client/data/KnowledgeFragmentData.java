/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.data;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragmentManager;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KnowledgeFragmentData
 * Created by HellFirePvP
 * Date: 22.09.2018 / 17:03
 */
@SideOnly(Side.CLIENT)
public class KnowledgeFragmentData extends CachedPersistentData {

    private List<KnowledgeFragment> flattenedFragments = Lists.newArrayList();
    private List<KnowledgeFragment> cacheCreativeFragments = Lists.newArrayList();
    private List<KnowledgeFragment> mergedCache = null;

    KnowledgeFragmentData() {
        super(PersistentDataManager.PersistentKey.KNOWLEDGE_FRAGMENTS);
    }

    @Override
    protected boolean mergeFrom(CachedPersistentData that) {
        boolean changed = false;
        if (that instanceof KnowledgeFragmentData) {
            for (KnowledgeFragment other : ((KnowledgeFragmentData) that).flattenedFragments) {
                changed |= this.addFragmentCache(other);
            }
        }
        return changed;
    }

    @Override
    public void clearCreativeCaches() {
        super.clearCreativeCaches();

        cacheCreativeFragments.clear();
        mergedCache = null;
    }

    public List<KnowledgeFragment> getAllFragments() {
        if (mergedCache == null) {
            mergedCache = new ArrayList<>(flattenedFragments.size() + cacheCreativeFragments.size());
            mergedCache.addAll(flattenedFragments);
            mergedCache.addAll(cacheCreativeFragments);
        }
        return mergedCache;
    }

    public List<KnowledgeFragment> getDiscoverableFragments() {
        PlayerProgress prog = ResearchManager.getProgress(Minecraft.getMinecraft().player, Side.CLIENT);
        List<KnowledgeFragment> frag = KnowledgeFragmentManager.getInstance().getAllFragments();
        frag.removeAll(flattenedFragments);
        frag.removeIf(f -> !f.canDiscover(prog));
        return frag;
    }

    public Collection<KnowledgeFragment> getFragmentsFor(GuiScreenJournal journal) {
        PlayerProgress prog = ResearchManager.getProgress(Minecraft.getMinecraft().player, Side.CLIENT);
        return getAllFragments().stream().filter(f -> f.isVisible(journal) && f.canSee(prog) && f.isFullyPresent()).collect(Collectors.toList());
    }

    public boolean addFragment(KnowledgeFragment fragment) {
        return this.addFragmentCache(fragment) && save();
    }

    private boolean addFragmentCache(KnowledgeFragment frag) {
        List<KnowledgeFragment> target = creative ? cacheCreativeFragments : flattenedFragments;
        if (getAllFragments().contains(frag)) {
            return false;
        }
        mergedCache = null;
        return target.add(frag);
    }

    @Override
    public void readFromNBT(NBTTagCompound cmp) {
        this.flattenedFragments.clear();

        KnowledgeFragmentManager mgr = KnowledgeFragmentManager.getInstance();
        for (NBTBase tag : cmp.getTagList("fragments", Constants.NBT.TAG_STRING)) {
            if (tag instanceof NBTTagString) { //Should always be the case tho
                String str = ((NBTTagString) tag).getString();
                KnowledgeFragment frag = mgr.getFragment(new ResourceLocation(str));
                if (frag != null) {
                    this.addFragmentCache(frag);
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound cmp) {
        Collection<KnowledgeFragment> flattened = this.flattenedFragments; //Only save non-creative ones
        NBTTagList listFragments = new NBTTagList();
        for (KnowledgeFragment frag : flattened) {
            listFragments.appendTag(new NBTTagString(frag.getRegistryName().toString()));
        }
        cmp.setTag("fragments", listFragments);
    }

}
