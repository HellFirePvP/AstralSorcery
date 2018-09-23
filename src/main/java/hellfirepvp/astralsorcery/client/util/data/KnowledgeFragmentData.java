/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.data;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragmentManager;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    public Collection<KnowledgeFragment> getFragmentsFor(GuiScreenJournal journal) {
        return this.flattenedFragments.stream().filter(f -> f.isVisible(journal)).collect(Collectors.toList());
    }

    public boolean addFragment(KnowledgeFragment fragment) {
        return this.addFragmentCache(fragment) && save();
    }

    private boolean addFragmentCache(KnowledgeFragment frag) {
        return !this.flattenedFragments.contains(frag) && this.flattenedFragments.add(frag);
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
        Collection<KnowledgeFragment> flattened = this.flattenedFragments;
        NBTTagList listFragments = new NBTTagList();
        for (KnowledgeFragment frag : flattened) {
            listFragments.appendTag(new NBTTagString(frag.getRegistryName().toString()));
        }
        cmp.setTag("fragments", listFragments);
    }

}
