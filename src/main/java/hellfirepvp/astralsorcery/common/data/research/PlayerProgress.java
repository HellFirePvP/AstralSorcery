package hellfirepvp.astralsorcery.common.data.research;

import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncKnowledge;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerProgress
 * Created by HellFirePvP
 * Date: 07.05.2016 / 13:34
 */
public class PlayerProgress {

    private List<String> knownConstellations = new ArrayList<>();
    private List<ResearchProgression> researchProgression = new LinkedList<>();
    private ProgressionTier tierReached = ProgressionTier.DISCOVERY;

    public void load(NBTTagCompound compound) {
        knownConstellations.clear();
        researchProgression.clear();
        tierReached = ProgressionTier.DISCOVERY;

        if (compound.hasKey("constellations")) {
            NBTTagList list = compound.getTagList("constellations", 8);
            for (int i = 0; i < list.tagCount(); i++) {
                knownConstellations.add(list.getStringTagAt(i));
            }
        }

        if (compound.hasKey("tierReached")) {
            int tierOrdinal = compound.getInteger("tierReached");
            tierReached = ProgressionTier.values()[MathHelper.clamp_int(tierOrdinal, 0, ProgressionTier.values().length - 1)];
        }

        if (compound.hasKey("research")) {
            int[] research = compound.getIntArray("research");
            for (int resOrdinal : research) {
                researchProgression.add(ResearchProgression.getById(resOrdinal));
            }
        }

    }

    public void store(NBTTagCompound cmp) {
        NBTTagList list = new NBTTagList();
        for (String s : knownConstellations) {
            list.appendTag(new NBTTagString(s));
        }
        cmp.setTag("constellations", list);
        cmp.setInteger("tierReached", tierReached.ordinal());
        int[] researchArray = new int[researchProgression.size()];
        for (int i = 0; i < researchProgression.size(); i++) {
            ResearchProgression progression = researchProgression.get(i);
            researchArray[i] = progression.getProgressId();
        }
        cmp.setIntArray("research", researchArray);
    }

    protected void forceGainResearch(ResearchProgression progression) {
        if(!researchProgression.contains(progression))
            researchProgression.add(progression);
    }

    public List<ResearchProgression> getResearchProgression() {
        return Collections.unmodifiableList(researchProgression);
    }

    public ProgressionTier getTierReached() {
        return tierReached;
    }

    public boolean stepTier() {
        if(getTierReached().hasNextTier()) {
            setTierReached(ProgressionTier.values()[getTierReached().ordinal() + 1]);
            return true;
        }
        return false;
    }

    public void setTierReached(ProgressionTier tier) {
        this.tierReached = tier;
    }

    public EnumGatedKnowledge.ViewCapability getViewCapability() {
        return getTierReached().getViewCapability();
    }

    public List<String> getKnownConstellations() {
        return knownConstellations;
    }

    public boolean hasConstellationDiscovered(String constellation) {
        return knownConstellations.contains(constellation);
    }

    protected void discoverConstellation(String name) {
        if (!knownConstellations.contains(name)) knownConstellations.add(name);
    }

    protected void receive(PktSyncKnowledge message) {
        this.knownConstellations = message.knownConstellations;
        this.researchProgression = message.researchProgression;
        this.tierReached = ProgressionTier.values()[MathHelper.clamp_int(message.progressTier, 0, ProgressionTier.values().length - 1)];
    }

}
