package hellfire.astralSorcery.common.data.research;

import hellfire.astralSorcery.common.net.packet.PktSyncKnowledge;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 09.02.2016 11:44
 */
public class PlayerProgress {

    private List<String> knownConstellations = new ArrayList<String>();
    private int tierReached = 0;

    public void load(NBTTagCompound compound) {
        knownConstellations.clear();
        tierReached = 0;

        if(compound.hasKey("constellations")) {
            NBTTagList list = compound.getTagList("constellations", 8);
            for (int i = 0; i < list.tagCount(); i++) {
                knownConstellations.add(list.getStringTagAt(i));
            }
        }

        if(compound.hasKey("tierReached")) {
            tierReached = compound.getInteger("tierReached");
        }

    }

    public void store(NBTTagCompound cmp) {

        NBTTagList list = new NBTTagList();
        for(String s : knownConstellations) {
            list.appendTag(new NBTTagString(s));
        }
        cmp.setTag("constellations", list);
        cmp.setInteger("tierReached", tierReached);

    }

    public int getTierReached() {
        return tierReached;
    }

    protected void setTierReached(int tier) {
        this.tierReached = tier;
    }

    public List<String> getKnownConstellations() {
        return knownConstellations;
    }

    public boolean hasConstellationDiscovered(String constellation) {
        return knownConstellations.contains(constellation);
    }

    protected void discoverConstellation(String name) {
        if(!knownConstellations.contains(name)) knownConstellations.add(name);
    }

    protected void receive(PktSyncKnowledge message) {
        this.knownConstellations = message.knownConstellations;
        this.tierReached = message.progressTier;
    }
}
