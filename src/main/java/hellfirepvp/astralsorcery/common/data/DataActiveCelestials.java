package hellfirepvp.astralsorcery.common.data;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.CelestialHandler;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DataActiveCelestials
 * Created by HellFirePvP
 * Date: 09.05.2016 / 21:46
 */
public class DataActiveCelestials extends AbstractData {

    private boolean gotUpdated = false;

    private List<Constellation> activeConstellations = new LinkedList<>();

    @Override
    public boolean needsUpdate() {
        return gotUpdated;
    }

    public List<Constellation> getActiveConstellations() {
        return activeConstellations;
    }

    public void updateIterations(Collection<CelestialHandler.TierIteration> iterations) {
        List<Constellation> list = new LinkedList<Constellation>();
        for (CelestialHandler.TierIteration ti : iterations) {
            if (ti.isShowing()) {
                Constellation c = ti.getCurrentConstellation();
                if (c != null) {
                    list.add(c);
                }
            }
        }
        updateConstellations(list);
    }

    private void updateConstellations(List<Constellation> constellations) {
        for (Constellation c : constellations) {
            if (!activeConstellations.contains(c)) {
                activeConstellations.add(c);
            }
        }
        Iterator<Constellation> iterator = activeConstellations.iterator();
        while (iterator.hasNext()) {
            Constellation c = iterator.next();
            if (!constellations.contains(c)) {
                iterator.remove();
            }
        }

        gotUpdated = true;
        markDirty();
    }

    @Override
    public void writeAllDataToPacket(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Constellation c : activeConstellations) {
            list.appendTag(new NBTTagString(c.getName()));
        }
        compound.setTag("constellations", list);
    }

    @Override
    public void writeToPacket(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Constellation c : activeConstellations) {
            list.appendTag(new NBTTagString(c.getName()));
        }
        compound.setTag("constellations", list);
    }

    @Override
    public void readRawFromPacket(NBTTagCompound compound) {
        if (!compound.hasKey("constellations")) {
            this.activeConstellations = new LinkedList<>();
            return;
        }
        this.activeConstellations.clear();
        NBTTagList list = compound.getTagList("constellations", new NBTTagString().getId());
        for (int i = 0; i < list.tagCount(); i++) {
            String str = list.getStringTagAt(i);
            Constellation c = ConstellationRegistry.getConstellationByName(str);
            if (c == null) {
                AstralSorcery.log.info("Received unknown constellation from server: " + str);
            } else {
                this.activeConstellations.add(c);
            }
        }
    }

    @Override
    public void handleIncomingData(AbstractData serverData) {
        if (!(serverData instanceof DataActiveCelestials)) return;

        this.activeConstellations = ((DataActiveCelestials) serverData).activeConstellations;
    }

    public static class Provider extends ProviderAutoAllocate<DataActiveCelestials> {

        public Provider(String key) {
            super(key);
        }

        @Override
        public DataActiveCelestials provideNewInstance() {
            return new DataActiveCelestials();
        }

    }

}
