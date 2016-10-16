package hellfirepvp.astralsorcery.common.data;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.CelestialHandler;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.Tier;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DataActiveCelestials
 * Created by HellFirePvP
 * Date: 09.05.2016 / 21:46
 */
public class DataActiveCelestials extends AbstractData {

    private Map<Tier, Constellation> constellationTierGrouped = new HashMap<>();

    public Collection<Constellation> getActiveConstellations() {
        return constellationTierGrouped.values();
    }

    @Nullable
    public Constellation getActiveConstellaionForTier(Tier t) {
        return constellationTierGrouped.get(t);
    }

    public void updateIterations(Collection<CelestialHandler.TierIteration> iterations) {
        List<Constellation> list = new LinkedList<>();
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

    //TODO check if i fcked up here.
    private void updateConstellations(List<Constellation> constellations) {
        for (Constellation c : constellations) {
            Tier t = ConstellationRegistry.getTier(c.getAssociatedTier());
            constellationTierGrouped.put(t, c);
            /*if (!constellationTierGrouped.values().contains(c)) {
                constellationTierGrouped.put(t, c);
            }*/
        }
        Iterator<Constellation> iterator = constellationTierGrouped.values().iterator();
        while (iterator.hasNext()) {
            Constellation c = iterator.next();
            if (!constellations.contains(c)) {
                iterator.remove();
            }
        }

        markDirty();
    }

    @Override
    public void writeAllDataToPacket(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Constellation c : getActiveConstellations()) {
            list.appendTag(new NBTTagString(c.getName()));
        }
        compound.setTag("constellations", list);
    }

    @Override
    public void writeToPacket(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Constellation c : getActiveConstellations()) {
            list.appendTag(new NBTTagString(c.getName()));
        }
        compound.setTag("constellations", list);
    }

    @Override
    public void readRawFromPacket(NBTTagCompound compound) {
        if (!compound.hasKey("constellations")) {
            this.constellationTierGrouped = new HashMap<>();
            return;
        }
        this.constellationTierGrouped.clear();
        NBTTagList list = compound.getTagList("constellations", new NBTTagString().getId());
        for (int i = 0; i < list.tagCount(); i++) {
            String str = list.getStringTagAt(i);
            Constellation c = ConstellationRegistry.getConstellationByName(str);
            if (c == null) {
                AstralSorcery.log.info("Received unknown constellation from server: " + str);
            } else {
                Tier t = ConstellationRegistry.getTier(c.getAssociatedTier());
                this.constellationTierGrouped.put(t, c);
            }
        }
    }

    @Override
    public void handleIncomingData(AbstractData serverData) {
        if (!(serverData instanceof DataActiveCelestials)) return;

        this.constellationTierGrouped = ((DataActiveCelestials) serverData).constellationTierGrouped;
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
