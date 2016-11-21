package hellfirepvp.astralsorcery.common.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DataWorldSkyHandlers
 * Created by HellFirePvP
 * Date: 21.11.2016 / 22:03
 */
public class DataWorldSkyHandlers extends AbstractData {

    private List<Integer> activeWorldSkyHandlers = new LinkedList<>();

    public boolean hasWorldHandler(World world) {
        return world != null && hasWorldHandler(world.provider.getDimension());
    }

    public boolean hasWorldHandler(int dim) {
        return activeWorldSkyHandlers.contains(dim);
    }
    public static boolean hasWorldHandler(int dim, Side side) {
        DataWorldSkyHandlers handle = SyncDataHolder.getData(side, SyncDataHolder.DATA_SKY_HANDLERS);
        return handle.hasWorldHandler(dim);
    }

    public static boolean hasWorldHandler(World world, Side side) {
        DataWorldSkyHandlers handle = SyncDataHolder.getData(side, SyncDataHolder.DATA_SKY_HANDLERS);
        return handle.hasWorldHandler(world);
    }

    @Override
    public void writeAllDataToPacket(NBTTagCompound compound) {
        NBTTagList dims = new NBTTagList();
        for (Integer i : activeWorldSkyHandlers) {
            dims.appendTag(new NBTTagInt(i));
        }
        compound.setTag("dims", dims);
    }

    @Override
    public void writeToPacket(NBTTagCompound compound) {
        writeAllDataToPacket(compound);
    }

    @Override
    public void readRawFromPacket(NBTTagCompound compound) {
        this.activeWorldSkyHandlers.clear();

        NBTTagList dims = compound.getTagList("dims", 3);
        for (int i = 0; i < dims.tagCount(); i++) {
            this.activeWorldSkyHandlers.add(dims.getIntAt(i));
        }
    }

    @Override
    public void handleIncomingData(AbstractData serverData) {
        if(!(serverData instanceof DataWorldSkyHandlers)) return;

        this.activeWorldSkyHandlers = ((DataWorldSkyHandlers) serverData).activeWorldSkyHandlers;
    }

    public void update(List<Integer> newDimIds) {
        this.activeWorldSkyHandlers = new LinkedList<>(newDimIds);
        markDirty();
    }

    public static class Provider extends ProviderAutoAllocate<DataWorldSkyHandlers> {

        public Provider(String key) {
            super(key);
        }

        @Override
        public DataWorldSkyHandlers provideNewInstance() {
            return new DataWorldSkyHandlers();
        }

    }

}
