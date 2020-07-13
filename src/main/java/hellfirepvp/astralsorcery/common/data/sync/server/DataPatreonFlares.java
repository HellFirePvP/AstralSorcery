/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.server;

import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.base.patreon.entity.PatreonPartialEntity;
import hellfirepvp.astralsorcery.common.data.sync.base.AbstractData;
import hellfirepvp.astralsorcery.common.data.sync.base.AbstractDataProvider;
import hellfirepvp.astralsorcery.common.data.sync.base.ClientDataReader;
import hellfirepvp.astralsorcery.common.data.sync.client.ClientPatreonFlares;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DataPatreonFlares
 * Created by HellFirePvP
 * Date: 30.08.2019 / 17:36
 */
public class DataPatreonFlares extends AbstractData {

    private Map<UUID, Set<PatreonPartialEntity>> entitiesServer = new HashMap<>();

    private Set<UUID> flarePlayerUpdates = new HashSet<>();
    private Set<UUID> flareRemovals = new HashSet<>();

    private DataPatreonFlares(ResourceLocation key) {
        super(key);
    }

    //Only actually called when there's an entity to be provided.
    @Nullable
    public PatreonPartialEntity createEntity(PlayerEntity player, PatreonEffect value) {
        UUID owner = player.getUniqueID();
        PatreonPartialEntity entity = value.createEntity(owner);
        if (entity == null) {
            return null;
        }
        entity.placeNear(player);

        entitiesServer.computeIfAbsent(owner, o -> new HashSet<>()).add(entity);

        flareRemovals.remove(owner);
        flarePlayerUpdates.add(owner);
        markDirty();

        return entity;
    }

    public void updateEntitiesOf(UUID playerUUID) {
        flareRemovals.remove(playerUUID);
        flarePlayerUpdates.add(playerUUID);
        markDirty();
    }

    public void removeEntities(UUID playerUUID) {
        flarePlayerUpdates.remove(playerUUID);
        flareRemovals.add(playerUUID);
        markDirty();

        this.entitiesServer.getOrDefault(playerUUID, Collections.emptySet())
                .forEach(p -> p.setRemoved(true));
    }

    @Nonnull
    public Collection<UUID> getOwners() {
        return this.entitiesServer.keySet();
    }

    @Nonnull
    public Collection<PatreonPartialEntity> getEntities(UUID playerUUID) {
        return this.entitiesServer.getOrDefault(playerUUID, Collections.emptySet());
    }

    @Nonnull
    public Collection<Collection<PatreonPartialEntity>> getEntities() {
        return new ArrayList<>(this.entitiesServer.values());
    }

    @Override
    public void clear(DimensionType dimType) {}

    @Override
    public void clearServer() {
        this.entitiesServer.clear();
        this.flarePlayerUpdates.clear();
        this.flareRemovals.clear();
    }

    @Override
    public void writeAllDataToPacket(CompoundNBT compound) {
        ListNBT entities = new ListNBT();
        for (UUID playerUUID : this.entitiesServer.keySet()) {
            CompoundNBT tag = new CompoundNBT();
            tag.putUniqueId("playerUUID", playerUUID);

            ListNBT entityList = new ListNBT();
            for (PatreonPartialEntity entity : this.entitiesServer.get(playerUUID)) {
                CompoundNBT entityNBT = new CompoundNBT();
                entityNBT.putUniqueId("id", entity.getEffectUUID());

                CompoundNBT data = new CompoundNBT();
                entity.writeToNBT(data);
                entityNBT.put("data", data);

                entityList.add(entityNBT);
            }
            tag.put("entityList", entityList);

            entities.add(tag);
        }
        compound.put("entities", entities);
    }

    @Override
    public void writeDiffDataToPacket(CompoundNBT compound) {
        ListNBT listUpdates = new ListNBT();
        for (UUID playerUUID : this.flarePlayerUpdates) {
            CompoundNBT tag = new CompoundNBT();
            tag.putUniqueId("playerUUID", playerUUID);

            ListNBT entityList = new ListNBT();
            for (PatreonPartialEntity entity : this.entitiesServer.get(playerUUID)) {
                CompoundNBT entityNBT = new CompoundNBT();
                entityNBT.putUniqueId("id", entity.getEffectUUID());

                CompoundNBT data = new CompoundNBT();
                entity.writeToNBT(data);
                entityNBT.put("data", data);

                entityList.add(entityNBT);
            }
            tag.put("entityList", entityList);
            listUpdates.add(tag);
        }

        ListNBT listRemovals = new ListNBT();
        for (UUID playerUUID : this.flareRemovals) {
            CompoundNBT playerTag = new CompoundNBT();
            playerTag.putUniqueId("playerUUID", playerUUID);
            listRemovals.add(playerTag);
        }

        compound.put("updates", listUpdates);
        compound.put("removals", listRemovals);

        this.flarePlayerUpdates.clear();
        this.flareRemovals.clear();
    }

    public static class Provider extends AbstractDataProvider<DataPatreonFlares, ClientPatreonFlares> {

        public Provider(ResourceLocation key) {
            super(key);
        }

        @Override
        public DataPatreonFlares provideServerData() {
            return new DataPatreonFlares(getKey());
        }

        @Override
        public ClientPatreonFlares provideClientData() {
            return new ClientPatreonFlares();
        }

        @Override
        public ClientDataReader<ClientPatreonFlares> createReader() {
            return new ClientPatreonFlares.Reader();
        }
    }
}
