/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.fragment;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.gui.GuiJournalConstellationDetails;
import hellfirepvp.astralsorcery.client.gui.journal.GuiJournalPages;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KnowledgeFragment
 * Created by HellFirePvP
 * Date: 22.09.2018 / 17:57
 */
public abstract class KnowledgeFragment {

    private static final Predicate<PlayerProgress> TRUE = (p) -> true;

    private final ResourceLocation name;
    private Predicate<PlayerProgress> canSeeTest = TRUE;
    private Predicate<PlayerProgress> canDiscoverTest = TRUE;

    public KnowledgeFragment(ResourceLocation name) {
        this.name = name;
    }

    public KnowledgeFragment(String name) {
        this(new ResourceLocation(AstralSorcery.MODID, name));
    }

    public static KnowledgeFragment onConstellations(String name, IConstellation... constellations) {
        return onConstellations(new ResourceLocation(AstralSorcery.MODID, name), constellations);
    }

    public static KnowledgeFragment onConstellations(ResourceLocation name, IConstellation... constellations) {
        return new KnowledgeFragment(name) {
            @Override
            @SideOnly(Side.CLIENT)
            public boolean isVisible(GuiScreenJournal journalGui) {
                return journalGui instanceof GuiJournalConstellationDetails &&
                        MiscUtils.contains(Arrays.asList(constellations), n -> n.equals(((GuiJournalConstellationDetails) journalGui).getConstellation()));
            }
        };
    }

    public static KnowledgeFragment onResearchNodes(String name, ResearchNode... nodes) {
        return onResearchNodes(new ResourceLocation(AstralSorcery.MODID, name), nodes);
    }

    private static KnowledgeFragment onResearchNodes(ResourceLocation name, ResearchNode... nodes) {
        return new KnowledgeFragment(name) {
            @Override
            @SideOnly(Side.CLIENT)
            public boolean isVisible(GuiScreenJournal journalGui) {
                return journalGui instanceof GuiJournalPages &&
                        MiscUtils.contains(Arrays.asList(nodes), n -> n.equals(((GuiJournalPages) journalGui).getResearchNode()));
            }
        };
    }

    public KnowledgeFragment setCanSeeTest(Predicate<PlayerProgress> canSeeTest) {
        this.canSeeTest = canSeeTest;
        return this;
    }

    public KnowledgeFragment setCanDiscoverTest(Predicate<PlayerProgress> canDiscoverTest) {
        this.canDiscoverTest = canDiscoverTest;
        return this;
    }

    public static Predicate<PlayerProgress> hasTier(ProgressionTier tier) {
        return (p) -> p.getTierReached().isThisLaterOrEqual(tier);
    }

    public static Predicate<PlayerProgress> discoveredConstellation(IConstellation cst) {
        return (p) -> p.hasConstellationDiscovered(cst);
    }

    //If the content of the knowledge fragment can be seen at the current progress
    //Might be earlier than #canDiscover
    public boolean canSee(PlayerProgress progress) {
        return canSeeTest.test(progress);
    }

    //If the knowledge fragment can be discovered at this stage
    //Might be later than #canSee
    //Always includes #canSee
    public boolean canDiscover(PlayerProgress progress) {
        return canSeeTest.test(progress) && canDiscoverTest.test(progress);
    }

    @SideOnly(Side.CLIENT)
    public abstract boolean isVisible(GuiScreenJournal journalGui);

    public String getUnlocalizedName() {
        return getLocalizationBaseString() + ".name";
    }

    public String getUnlocalizedBookmark() {
        return getLocalizationBaseString() + ".bookmark";
    }

    public String getUnlocalizedPage() {
        return getLocalizationBaseString() + ".description";
    }

    private String getLocalizationBaseString() {
        return "knowledge." + name.getResourceDomain() + "." + name.getResourcePath();
    }

    @SideOnly(Side.CLIENT)
    public boolean isFullyPresent() {
        return I18n.hasKey(getUnlocalizedName()) &&
                I18n.hasKey(getUnlocalizedBookmark()) &&
                I18n.hasKey(getUnlocalizedPage());
    }

    @SideOnly(Side.CLIENT)
    public String getLocalizedName() {
        return I18n.format(getUnlocalizedName());
    }

    @SideOnly(Side.CLIENT)
    public String getLocalizedBookmark() {
        return I18n.format(getUnlocalizedBookmark());
    }

    @SideOnly(Side.CLIENT)
    public String getLocalizedPage() {
        return I18n.format(getUnlocalizedPage());
    }

    public ResourceLocation getRegistryName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KnowledgeFragment that = (KnowledgeFragment) o;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name.toString());
    }
}
