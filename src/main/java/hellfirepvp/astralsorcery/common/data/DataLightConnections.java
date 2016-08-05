package hellfirepvp.astralsorcery.common.data;

import net.minecraft.nbt.NBTTagCompound;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DataStarlightConnections
 * Created by HellFirePvP
 * Date: 05.08.2016 / 20:14
 */
public class DataLightConnections extends AbstractData {

    private boolean changed = false;

    @Override
    public boolean needsUpdate() {
        return changed;
    }

    @Override
    public void writeAllDataToPacket(NBTTagCompound compound) {

    }

    @Override
    public void writeToPacket(NBTTagCompound compound) {

    }

    @Override
    public void readRawFromPacket(NBTTagCompound compound) {

    }

    @Override
    public void handleIncomingData(AbstractData serverData) {

    }

    public static class Provider extends ProviderAutoAllocate<DataLightConnections> {

        public Provider(String key) {
            super(key);
        }

        @Override
        public DataLightConnections provideNewInstance() {
            return new DataLightConnections();
        }

    }


}
