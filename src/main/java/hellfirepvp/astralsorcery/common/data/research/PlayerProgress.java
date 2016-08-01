package hellfirepvp.astralsorcery.common.data.research;

import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncKnowledge;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
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
    private ProgressionTier tierReached = ProgressionTier.EXPLORATION;

    public void load(NBTTagCompound compound) {
        knownConstellations.clear();
        tierReached = ProgressionTier.EXPLORATION;

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

    }

    public void store(NBTTagCompound cmp) {

        NBTTagList list = new NBTTagList();
        for (String s : knownConstellations) {
            list.appendTag(new NBTTagString(s));
        }
        cmp.setTag("constellations", list);
        cmp.setInteger("tierReached", tierReached.ordinal());

    }

    public ProgressionTier getTierReached() {
        return tierReached;
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
        this.tierReached = ProgressionTier.values()[MathHelper.clamp_int(message.progressTier, 0, ProgressionTier.values().length - 1)];
    }

}
