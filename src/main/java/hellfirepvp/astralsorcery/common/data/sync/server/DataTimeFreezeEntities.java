/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.server;

import hellfirepvp.astralsorcery.common.data.sync.base.AbstractData;
import hellfirepvp.astralsorcery.common.data.sync.base.AbstractDataProvider;
import hellfirepvp.astralsorcery.common.data.sync.base.ClientDataReader;
import hellfirepvp.astralsorcery.common.data.sync.client.ClientTimeFreezeEntities;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DataTimeFreezeEntities
 * Created by HellFirePvP
 * Date: 03.11.2020 / 20:46
 */
public class DataTimeFreezeEntities extends AbstractData {

    private final Map<RegistryKey<World>, Set<Integer>> serverActiveEntityFreeze = new HashMap<>();

    private final Set<RegistryKey<World>> serverSyncTypes = new HashSet<>();

    private DataTimeFreezeEntities(ResourceLocation key) {
        super(key);
    }

    public void freezeEntity(Entity e) {
        RegistryKey<World> dim = e.getEntityWorld().getDimensionKey();
        if (this.serverActiveEntityFreeze.computeIfAbsent(dim, dimType -> new HashSet<>()).add(e.getEntityId())) {
            this.serverSyncTypes.add(dim);
            this.markDirty();
        }
    }

    public void unfreezeEntity(Entity e) {
        RegistryKey<World> dim = e.getEntityWorld().getDimensionKey();
        if (this.serverActiveEntityFreeze.getOrDefault(dim, Collections.emptySet()).remove(e.getEntityId())) {
            this.serverSyncTypes.add(dim);
            this.markDirty();
        }
    }

    public boolean isFrozen(Entity e) {
        RegistryKey<World> dim = e.getEntityWorld().getDimensionKey();
        return this.serverActiveEntityFreeze.getOrDefault(dim, Collections.emptySet()).contains(e.getEntityId());
    }

    @Override
    public void clear(RegistryKey<World> dimType) {
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
        Map<RegistryKey<World>, Set<Integer>> entities = new HashMap<>();
        this.serverSyncTypes.forEach(type -> {
            entities.put(type, this.serverActiveEntityFreeze.getOrDefault(type, new HashSet<>()));
        });
        this.writeEntityInformation(compound, entities);
        this.serverSyncTypes.clear();
    }

    private void writeEntityInformation(CompoundNBT out, Map<RegistryKey<World>, Set<Integer>> entities) {
        CompoundNBT dimTag = new CompoundNBT();
        entities.forEach((dim, entityIds) -> {
            ListNBT nbtEntities = new ListNBT();
            entityIds.forEach(id -> nbtEntities.add(IntNBT.valueOf(id)));
            dimTag.put(dim.getLocation().toString(), nbtEntities);
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
