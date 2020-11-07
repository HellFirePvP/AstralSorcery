package hellfirepvp.astralsorcery.common.data.sync.server;

import hellfirepvp.astralsorcery.common.data.sync.base.AbstractData;
import hellfirepvp.astralsorcery.common.data.sync.base.AbstractDataProvider;
import hellfirepvp.astralsorcery.common.data.sync.base.ClientDataReader;
import hellfirepvp.astralsorcery.common.data.sync.client.ClientTimeFreezeEntities;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DataTimeFreezeEntities
 * Created by HellFirePvP
 * Date: 03.11.2020 / 20:46
 */
public class DataTimeFreezeEntities extends AbstractData {

    private final Map<DimensionType, Set<Integer>> serverActiveEntityFreeze = new HashMap<>();

    private final Set<DimensionType> serverSyncTypes = new HashSet<>();

    private DataTimeFreezeEntities(ResourceLocation key) {
        super(key);
    }

    public void freezeEntity(Entity e) {
        if (this.serverActiveEntityFreeze.computeIfAbsent(e.dimension, dimType -> new HashSet<>()).add(e.getEntityId())) {
            this.serverSyncTypes.add(e.dimension);
            this.markDirty();
        }
    }

    public void unfreezeEntity(Entity e) {
        if (this.serverActiveEntityFreeze.getOrDefault(e.dimension, Collections.emptySet()).remove(e.getEntityId())) {
            this.serverSyncTypes.add(e.dimension);
            this.markDirty();
        }
    }

    public boolean isFrozen(Entity e) {
        return this.serverActiveEntityFreeze.getOrDefault(e.dimension, Collections.emptySet()).contains(e.getEntityId());
    }

    @Override
    public void clear(DimensionType dimType) {
        this.serverActiveEntityFreeze.remove(dimType);
    }

    @Override
    public void clearServer() {
        this.serverActiveEntityFreeze.clear();
        this.serverSyncTypes.clear();
    }

    @Override
    public void writeAllDataToPacket(CompoundNBT compound) {
        this.writeEntityInformation(compound, this.serverActiveEntityFreeze);
    }

    @Override
    public void writeDiffDataToPacket(CompoundNBT compound) {
        Map<DimensionType, Set<Integer>> entities = new HashMap<>();
        this.serverSyncTypes.forEach(type -> {
            entities.put(type, this.serverActiveEntityFreeze.getOrDefault(type, new HashSet<>()));
        });
        this.writeEntityInformation(compound, entities);
        this.serverSyncTypes.clear();
    }

    private void writeEntityInformation(CompoundNBT out, Map<DimensionType, Set<Integer>> entities) {
        CompoundNBT dimTag = new CompoundNBT();
        entities.forEach((dimType, entityIds) -> {
            ListNBT nbtEntities = new ListNBT();
            entityIds.forEach(id -> nbtEntities.add(IntNBT.valueOf(id)));
            dimTag.put(dimType.getRegistryName().toString(), nbtEntities);
        });
        out.put("dimTypes", dimTag);
    }

    public static class Provider extends AbstractDataProvider<DataTimeFreezeEntities, ClientTimeFreezeEntities> {

        public Provider(ResourceLocation key) {
            super(key);
        }

        @Override
        public DataTimeFreezeEntities provideServerData() {
            return new DataTimeFreezeEntities(getKey());
        }

        @Override
        public ClientTimeFreezeEntities provideClientData() {
            return new ClientTimeFreezeEntities();
        }

        @Override
        public ClientDataReader<ClientTimeFreezeEntities> createReader() {
            return new ClientTimeFreezeEntities.Reader();
        }
    }
}
