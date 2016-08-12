package hellfirepvp.astralsorcery.common.data.research;

import net.minecraft.entity.player.EntityPlayer;
import scala.actors.threadpool.Arrays;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResearchProgression
 * Created by HellFirePvP
 * Date: 10.08.2016 / 13:38
 */
public enum ResearchProgression {

    TEST_PROGRESS(0, ProgressionTier.DISCOVERY);

    private final int progressId;
    private List<ResearchProgression> preConditions = new LinkedList<>();
    private List<ResearchNode> researchNodes = new LinkedList<>();
    private final ProgressionTier requiredProgress;

    private static final Map<Integer, ResearchProgression> BY_ID = new HashMap<>();

    private ResearchProgression(int id, ProgressionTier requiredProgress, ResearchProgression... preConditions) {
        this(id, requiredProgress, Arrays.asList(preConditions));
    }

    private ResearchProgression(int id, ProgressionTier requiredProgress, List<ResearchProgression> preConditions) {
        this.preConditions.addAll(preConditions);
        this.requiredProgress = requiredProgress;
        this.progressId = id;
    }

    void addResearchToGroup(ResearchNode res) {
        this.researchNodes.add(res);
    }

    public List<ResearchNode> getResearchNodes() {
        return researchNodes;
    }

    public boolean tryStepTo(EntityPlayer player) {
        return canStepTo(player) && ResearchManager.forceUnsafeResearchStep(player, this);
    }

    public boolean canStepTo(EntityPlayer player) {
        PlayerProgress progress = ResearchManager.getProgress(player);
        if(progress == null) return false;
        List<ResearchProgression> playerResearchProgression = progress.getResearchProgression();
        ProgressionTier playerTier = progress.getTierReached();
        return playerTier.isThisLaterOrEqual(requiredProgress) && playerResearchProgression.containsAll(preConditions);
    }

    public ProgressionTier getRequiredProgress() {
        return requiredProgress;
    }

    public List<ResearchProgression> getPreConditions() {
        return Collections.unmodifiableList(preConditions);
    }

    public int getProgressId() {
        return progressId;
    }

    public static ResearchProgression getById(int id) {
        return BY_ID.get(id);
    }

    static {
        for (ResearchProgression progress : values()) {
            BY_ID.put(progress.progressId, progress);
        }
    }

}
