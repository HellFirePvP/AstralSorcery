package hellfirepvp.astralsorcery.common.data;

import hellfirepvp.astralsorcery.common.starlight.network.TransmissionChain;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

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


    public void updateNewConnectionsThreaded(List<TransmissionChain.LightConnection> newlyAddedConnections) {

    }

    public void removeOldConnectionsThreaded(List<TransmissionChain.LightConnection> invalidConnections) {

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
        if(!(serverData instanceof DataLightConnections)) return;

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
