/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.fragment;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.base.MoonPhase;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: KnowledgeFragment
 * Created by HellFirePvP
 * Date: 22.09.2018 / 17:57
 */
public abstract class KnowledgeFragment extends ForgeRegistryEntry<KnowledgeFragment> {

    private static final Predicate<PlayerProgress> TRUE = (p) -> true;

    private String unlocPrefix;
    private Predicate<PlayerProgress> canSeeTest = TRUE;
    private Predicate<PlayerProgress> canDiscoverTest = TRUE;

    public KnowledgeFragment(ResourceLocation name, String unlocalizedPrefix) {
        this.setRegistryName(name);
        this.unlocPrefix = unlocalizedPrefix;
    }

    public static KnowledgeFragment onConstellations(String name, IConstellation... constellations) {
        return onConstellations(new ResourceLocation(AstralSorcery.MODID, name), constellations);
    }

    public static KnowledgeFragment onConstellations(ResourceLocation name, IConstellation... constellations) {
        List<IConstellation> cst = Arrays.asList(constellations);
        IConstellation c = Iterables.getFirst(cst, null);
        return new KnowledgeFragment(name, c == null ? "" : c.getUnlocalizedName()) {
            @Override
            @OnlyIn(Dist.CLIENT)
            public boolean isVisible(Screen journalGui) {
                return false;
                //TODO journal constelltation details
                //return journalGui instanceof GuiJournalConstellationDetails &&
                //        MiscUtils.contains(cst, n -> n.equals(((GuiJournalConstellationDetails) journalGui).getConstellation()));
            }
        }
        // Any involved constellation discovered
        .setCanSeeTest(prog -> {
            for (IConstellation con : cst) {
                if (prog.hasConstellationDiscovered(con)) {
                    return true;
                }
            }
            return false;
        });
    }

    public static KnowledgeFragment onResearchNodes(String name, ResearchNode... nodes) {
        return onResearchNodes(new ResourceLocation(AstralSorcery.MODID, name), nodes);
    }

    private static KnowledgeFragment onResearchNodes(ResourceLocation name, ResearchNode... nodes) {
        List<ResearchNode> nds = Arrays.asList(nodes);
        ResearchNode nd = Iterables.getFirst(nds, null);
        return new KnowledgeFragment(name, nd == null ? "" : nd.getUnLocalizedName()) {
            @Override
            @OnlyIn(Dist.CLIENT)
            public boolean isVisible(Screen journalGui) {
                return false;
                //TODO journal
                //return journalGui instanceof GuiJournalPages &&
                //        MiscUtils.contains(nds, n -> n.equals(((GuiJournalPages) journalGui).getResearchNode()));
            }
        }
        // Any preconditions visible
        .setCanSeeTest(prog -> {
            for (ResearchNode n : nds) {
                if (!n.canSee(prog)) {
                    continue;
                }
                for (ResearchProgression rProg : ResearchProgression.findProgression(n)) {
                    if (prog.getResearchProgression().contains(rProg)) {
                        return true;
                    }
                }
            }
            return false;
        })
        // All preconditions visible
        .setCanDiscoverTest(prog -> {
            for (ResearchNode n : nds) {
                if (!n.canSee(prog)) {
                    return false;
                }
                for (ResearchProgression rProg : ResearchProgression.findProgression(n)) {
                    if (!prog.getResearchProgression().contains(rProg)) {
                        return false;
                    }
                }
            }
            return true;
        });
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

    // If this knowledge fragment is gated behind the random constellation minigame
    @OnlyIn(Dist.CLIENT)
    private boolean isConstellationGated(long seed) {
        return new Random(seed).nextInt(4) == 0;
    }

    //TODO client constellation gen
    //@Nullable
    //@OnlyIn(Dist.CLIENT)
    //public IConstellation getDiscoverConstellation(long seed) {
    //    if (!isConstellationGated(seed)) {
    //        return null;
    //    }
    //    ClientConstellationGenerator.ClientConstellation cst = ClientConstellationGenerator.generateRandom(seed);
    //    cst.setFragment(this);
    //    return cst;
    //}

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public List<MoonPhase> getShowupPhases(long seed) {
        if (!isConstellationGated(seed)) {
            return Lists.newArrayList();
        }
        Random r = new Random(seed);
        int amt = 2 + r.nextInt(2);
        List<MoonPhase> phases = new ArrayList<>(amt);
        for (int i = 0; i < amt; i++) {
            MoonPhase phase;
            do {
                phase = MoonPhase.values()[r.nextInt(MoonPhase.values().length)];
            } while (phases.contains(phase));
            phases.add(phase);
        }
        return phases;
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

    //TODO journal
    @OnlyIn(Dist.CLIENT)
    public abstract boolean isVisible(Screen journalGui);
    //public abstract boolean isVisible(GuiScreenJournal journalGui);

    public String getUnlocalizedName() {
        return String.format("%s.name", getLocalizationBaseString());
    }

    public String getUnlocalizedBookmark() {
        return String.format("%s.bookmark", getLocalizationBaseString());
    }

    public String getUnlocalizedPage() {
        return String.format("%s.description", getLocalizationBaseString());
    }

    private String getLocalizationBaseString() {
        ResourceLocation name = this.getRegistryName();
        return String.format("knowledge.%s.%s", name.getNamespace(), name.getPath());
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isFullyPresent() {
        return I18n.hasKey(getUnlocalizedName()) &&
                I18n.hasKey(getUnlocalizedBookmark()) &&
                I18n.hasKey(getUnlocalizedPage()) &&
                (this.unlocPrefix.isEmpty() || I18n.hasKey(this.unlocPrefix));
    }

    @OnlyIn(Dist.CLIENT)
    public String getLocalizedName() {
        return I18n.format(getUnlocalizedName());
    }

    @OnlyIn(Dist.CLIENT)
    public String getLocalizedBookmark() {
        return I18n.format(getUnlocalizedBookmark());
    }

    @OnlyIn(Dist.CLIENT)
    public String getLocalizedPage() {
        return I18n.format(getUnlocalizedPage());
    }

    @OnlyIn(Dist.CLIENT)
    public String getLocalizedIndexName() {
        return this.unlocPrefix.isEmpty() ? this.getLocalizedName() : String.format("%s: %s", I18n.format(this.unlocPrefix), this.getLocalizedName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KnowledgeFragment that = (KnowledgeFragment) o;
        return Objects.equals(this.getRegistryName(), that.getRegistryName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getRegistryName().toString());
    }
}
