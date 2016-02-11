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

    public void load(NBTTagCompound compound) {
        knownConstellations.clear();

        if(compound.hasKey("constellations")) {
            NBTTagList list = compound.getTagList("constellations", 8);
            for (int i = 0; i < list.tagCount(); i++) {
                knownConstellations.add(list.getStringTagAt(i));
            }
        }

    }

    public void store(NBTTagCompound cmp) {

        NBTTagList list = new NBTTagList();
        for(String s : knownConstellations) {
            list.appendTag(new NBTTagString(s));
        }
        cmp.setTag("constellations", list);

    }

    public List<String> getKnownConstellations() {
        return knownConstellations;
    }

    public boolean hasConstellationDiscovered(String constellation) {
        return knownConstellations.contains(constellation);
    }

    public void discoverConstellation(String name) {
        if(!knownConstellations.contains(name)) knownConstellations.add(name);
    }

    public void receive(PktSyncKnowledge message) {
        this.knownConstellations = message.knownConstellations;
    }
}
