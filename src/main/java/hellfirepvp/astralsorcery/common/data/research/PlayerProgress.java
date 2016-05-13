package hellfirepvp.astralsorcery.common.data.research;

import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncKnowledge;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

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

    private List<String> knownConstellations = new ArrayList<String>();
    private int tierReached = -1; //-1 == not involved in AstralSorcery, 0 -> maxTier rest.

    public void load(NBTTagCompound compound) {
        knownConstellations.clear();
        tierReached = -1;

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

    public boolean isInvolved() {
        return tierReached >= 0;
    }

    protected void setTierReached(int tier) {
        if(tier > this.tierReached) {
            this.tierReached = tier;
        }
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
