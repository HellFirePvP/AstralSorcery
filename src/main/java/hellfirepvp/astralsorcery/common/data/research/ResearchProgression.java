/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraftforge.common.IExtensibleEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResearchProgression
 * Created by HellFirePvP
 * Date: 10.08.2016 / 13:38
 */
public enum ResearchProgression implements IExtensibleEnum {

    DISCOVERY(ProgressionTier.DISCOVERY),
    BASIC_CRAFT(ProgressionTier.BASIC_CRAFT, DISCOVERY),
    ATTUNEMENT(ProgressionTier.ATTUNEMENT, BASIC_CRAFT),
    CONSTELLATION(ProgressionTier.CONSTELLATION_CRAFT, ATTUNEMENT),
    RADIANCE(ProgressionTier.TRAIT_CRAFT, CONSTELLATION),
    BRILLIANCE(ProgressionTier.BRILLIANCE, RADIANCE)
    ;

    private List<ResearchProgression> preConditions = new LinkedList<>();
    private List<ResearchNode> researchNodes = new LinkedList<>();
    private final ProgressionTier requiredProgress;
    private final String unlocName;

    private static final Map<String, ResearchProgression> BY_NAME = new HashMap<>();

    private ResearchProgression(ProgressionTier requiredProgress, ResearchProgression... preConditions) {
        this(requiredProgress, Arrays.asList(preConditions));
    }

    private ResearchProgression(ProgressionTier requiredProgress, List<ResearchProgression> preConditions) {
        this.preConditions.addAll(preConditions);
        this.requiredProgress = requiredProgress;
        this.unlocName = AstralSorcery.MODID + ".journal.cluster." + name().toLowerCase() + ".name";
    }

    void addResearchToGroup(ResearchNode res) {
        for (ResearchNode node : researchNodes) {
            if (node.renderPosX == res.renderPosX &&
                    node.renderPosZ == res.renderPosZ) {
                throw new IllegalArgumentException("Tried to register 2 Research Nodes at the same position at x=" + res.renderPosX + ", z=" + res.renderPosZ + "! " +
                        "Present: " + node.getUnLocalizedName() + " - Tried to set: " + res.getUnLocalizedName());
            }
        }
        this.researchNodes.add(res);
    }

    public static ResearchProgression create(String name, ProgressionTier tier, List<ResearchProgression> preConditions) {
        throw new IllegalStateException("Enum not extended");
    }

    public List<ResearchNode> getResearchNodes() {
        return researchNodes;
    }

    public Registry getRegistry() {
        return new Registry(this);
    }

    /*public boolean tryStepTo(PlayerEntity player, boolean force) {
        return (force || canStepTo(player)) && ResearchManager.forceUnsafeResearchStep(player, this);
    }

    public boolean canStepTo(PlayerEntity player) {
        PlayerProgress progress = ResearchManager.getProgress(player);
        if (progress == null) return false;
        List<ResearchProgression> playerResearchProgression = progress.getResearchProgression();
        ProgressionTier playerTier = progress.getTierReached();
        return playerTier.isThisLaterOrEqual(requiredProgress) && playerResearchProgression.containsAll(preConditions);
    }*/

    public ProgressionTier getRequiredProgress() {
        return requiredProgress;
    }

    public List<ResearchProgression> getPreConditions() {
        return Collections.unmodifiableList(preConditions);
    }

    public String getUnlocalizedName() {
        return unlocName;
    }

    public static ResearchProgression getByEnumName(String name) {
        return BY_NAME.get(name);
    }

    @Nullable
    public static ResearchNode findNode(String name) {
        for (ResearchProgression prog : values()) {
            for (ResearchNode node : prog.getResearchNodes()) {
                if (node.getSimpleName().equals(name)) {
                    return node;
                }
            }
        }
        return null;
    }

    @Nonnull
    public static Collection<ResearchProgression> findProgression(ResearchNode n) {
        Collection<ResearchProgression> progressions = Lists.newArrayList();
        for (ResearchProgression prog : values()) {
            if (prog.getResearchNodes().contains(n)) {
                progressions.add(prog);
            }
        }
        return progressions;
    }

    static {
        for (ResearchProgression progress : values()) {
            BY_NAME.put(progress.name(), progress);
        }
    }

    public static class Registry {

        private final ResearchProgression prog;

        public Registry(ResearchProgression prog) {
            this.prog = prog;
        }

        public void register(ResearchNode node) {
            prog.addResearchToGroup(node);
        }

    }

}
