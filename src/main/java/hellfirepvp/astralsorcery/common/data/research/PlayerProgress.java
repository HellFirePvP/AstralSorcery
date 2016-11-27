package hellfirepvp.astralsorcery.common.data.research;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerks;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncKnowledge;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
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
    private IMajorConstellation attunedConstellation = null;
    private List<ConstellationPerk> appliedPerks = new LinkedList<>();
    private List<ResearchProgression> researchProgression = new LinkedList<>();
    private ProgressionTier tierReached = ProgressionTier.DISCOVERY;

    public void load(NBTTagCompound compound) {
        knownConstellations.clear();
        researchProgression.clear();
        appliedPerks.clear();
        attunedConstellation = null;
        tierReached = ProgressionTier.DISCOVERY;

        if (compound.hasKey("constellations")) {
            NBTTagList list = compound.getTagList("constellations", 8);
            for (int i = 0; i < list.tagCount(); i++) {
                knownConstellations.add(list.getStringTagAt(i));
            }
        }
        if(compound.hasKey("listPerks")) {
            NBTTagList list = compound.getTagList("listPerks", 3);
            for (int i = 0; i < list.tagCount(); i++) {
                ConstellationPerks perkEnum = ConstellationPerks.values()[list.getIntAt(i)];
                if(perkEnum != null) {
                    appliedPerks.add(perkEnum.createPerk());
                }
            }
        }

        if (compound.hasKey("attuned")) {
            String cst = compound.getString("attuned");
            IConstellation c = ConstellationRegistry.getConstellationByName(cst);
            if(c == null || !(c instanceof IMajorConstellation)) {
                AstralSorcery.log.warn("[AstralSorcery] Failed to load attuned Constellation: " + cst + " - constellation doesn't exist or isn't major.");
            } else {
                attunedConstellation = (IMajorConstellation) c;
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
        if(attunedConstellation != null) {
            cmp.setString("attuned", attunedConstellation.getUnlocalizedName());
        }
        list = new NBTTagList();
        for (ConstellationPerk perk : appliedPerks) {
            list.appendTag(new NBTTagInt(perk.getId()));
        }
        cmp.setTag("listPerks", list);
    }

    protected boolean forceGainResearch(ResearchProgression progression) {
        if(!researchProgression.contains(progression)) {
            researchProgression.add(progression);
            return true;
        }
        return false;
    }

    protected void setAttunedConstellation(IMajorConstellation constellation) {
        this.attunedConstellation = constellation;
    }

    public void addPerk(ConstellationPerk singleInstance) {
        this.appliedPerks.add(singleInstance);
    }

    public void clearPerks() {
        this.appliedPerks.clear();
    }

    public List<ResearchProgression> getResearchProgression() {
        return Lists.newLinkedList(researchProgression);
    }

    public ProgressionTier getTierReached() {
        return tierReached;
    }

    public IMajorConstellation getAttunedConstellation() {
        return attunedConstellation;
    }

    public List<ConstellationPerk> getAppliedPerks() {
        return Collections.unmodifiableList(appliedPerks);
    }

    public boolean hasPerkUnlocked(ConstellationPerks perk) {
        return hasPerkUnlocked(perk.getSingleInstance());
    }

    public boolean hasPerkUnlocked(ConstellationPerk perk) {
        if(perk == ConstellationPerks.OFF_DMG_INCREASE.getSingleInstance()) return true; //Remove after wiiv adjusted things.
        if(perk == ConstellationPerks.OFF_DMG_DISTANCE.getSingleInstance()) return true;

        return appliedPerks.contains(perk);
    }

    protected boolean stepTier() {
        if(getTierReached().hasNextTier()) {
            setTierReached(ProgressionTier.values()[getTierReached().ordinal() + 1]);
            return true;
        }
        return false;
    }

    protected void setTierReached(ProgressionTier tier) {
        this.tierReached = tier;
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
        this.attunedConstellation = message.attunedConstellation;
        this.appliedPerks = message.appliedPerks;
    }

}
