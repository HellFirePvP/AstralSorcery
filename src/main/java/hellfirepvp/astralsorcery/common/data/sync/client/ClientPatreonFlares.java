/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.client;

import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.base.patreon.entity.PatreonPartialEntity;
import hellfirepvp.astralsorcery.common.data.sync.base.ClientData;
import hellfirepvp.astralsorcery.common.data.sync.base.ClientDataReader;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientPatreonFlares
 * Created by HellFirePvP
 * Date: 30.08.2019 / 17:39
 */
public class ClientPatreonFlares extends ClientData<ClientPatreonFlares> {

    private Map<UUID, Set<PatreonPartialEntity>> entitiesClient = new HashMap<>();

    @Nonnull
    public Collection<UUID> getOwners() {
        return this.entitiesClient.keySet();
    }

    @Nonnull
    public Collection<PatreonPartialEntity> getEntities(UUID playerUUID) {
        return this.entitiesClient.getOrDefault(playerUUID, Collections.emptySet());
    }

    @Nonnull
    public Collection<Collection<PatreonPartialEntity>> getEntities() {
        return new ArrayList<>(this.entitiesClient.values());
    }

    @Override
    public void clear(DimensionType dimType) {}

    @Override
    public void clearClient() {
        this.entitiesClient.clear();
    }

    public static class Reader extends ClientDataReader<ClientPatreonFlares> {

        @Override
        public void readFromIncomingFullSync(ClientPatreonFlares data, CompoundNBT compound) {
            data.entitiesClient.clear();

            ListNBT entities = compound.getList("entities", Constants.NBT.TAG_COMPOUND);
            for (INBT iNBT : entities) {
                CompoundNBT tag = (CompoundNBT) iNBT;

                UUID playerUUID = tag.getUniqueId("playerUUID");
                Set<PatreonPartialEntity> entitySet = new HashSet<>();

                ListNBT entityList = tag.getList("entityList", Constants.NBT.TAG_COMPOUND);
                for (INBT iEntityTag : entityList) {
                    CompoundNBT entityNBT = (CompoundNBT) iEntityTag;

                    UUID effectUUID = entityNBT.getUniqueId("id");
                    PatreonEffect effect = PatreonEffectHelper.getPatreonEffects(LogicalSide.CLIENT, playerUUID)
                            .stream()
                            .filter(eff -> eff.getEffectUUID().equals(effectUUID))
                            .findFirst()
                            .orElse(null);
                    if (effect == null) {
                        continue;
                    }
                    PatreonPartialEntity entity = effect.createEntity(playerUUID);
                    if (entity == null) {
                        continue;
                    }
                    entity.readFromNBT(entityNBT.getCompound("data"));
                    entitySet.add(entity);
                }

                data.entitiesClient.put(playerUUID, entitySet);
            }
        }

        @Override
        public void readFromIncomingDiff(ClientPatreonFlares data, CompoundNBT compound) {
            ListNBT entities = compound.getList("updates", Constants.NBT.TAG_COMPOUND);
            for (INBT iNBT : entities) {
                CompoundNBT tag = (CompoundNBT) iNBT;

                UUID playerUUID = tag.getUniqueId("playerUUID");
                Set<PatreonPartialEntity> entitySet = data.entitiesClient.computeIfAbsent(playerUUID, p -> new HashSet<>());

                ListNBT entityList = tag.getList("entityList", Constants.NBT.TAG_COMPOUND);
                for (INBT iEntityTag : entityList) {
                    CompoundNBT entityNBT = (CompoundNBT) iEntityTag;

                    UUID effectUUID = entityNBT.getUniqueId("id");
                    PatreonEffect effect = PatreonEffectHelper.getPatreonEffects(LogicalSide.CLIENT, playerUUID)
                            .stream()
                            .filter(eff -> eff.getEffectUUID().equals(effectUUID))
                            .findFirst()
                            .orElse(null);
                    if (effect == null) {
                        continue;
                    }
                    PatreonPartialEntity entity = entitySet.stream()
                            .filter(e -> e.getEffectUUID().equals(effectUUID))
                            .findFirst()
                            .orElse(null);
                    if (entity == null) {
                        entity = effect.createEntity(playerUUID);
                        if (entity == null) {
                            continue;
                        }
                        entitySet.add(entity);
                    }
                    entity.readFromNBT(entityNBT.getCompound("data"));
                }
            }

            ListNBT removals = compound.getList("removals", Constants.NBT.TAG_COMPOUND);
            for (INBT iNBT : removals) {
                CompoundNBT tag = (CompoundNBT) iNBT;

                UUID playerUUID = tag.getUniqueId("playerUUID");
                data.entitiesClient.remove(playerUUID);
            }
        }
    }
}
