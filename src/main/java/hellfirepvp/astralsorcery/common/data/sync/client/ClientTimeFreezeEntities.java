package hellfirepvp.astralsorcery.common.data.sync.client;

import hellfirepvp.astralsorcery.common.data.sync.base.ClientData;
import hellfirepvp.astralsorcery.common.data.sync.base.ClientDataReader;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.Constants;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientTimeFreezeEntities
 * Created by HellFirePvP
 * Date: 03.11.2020 / 20:47
 */
public class ClientTimeFreezeEntities extends ClientData<ClientTimeFreezeEntities> {

    private final Map<DimensionType, Set<Integer>> clientActiveEntityFreeze = new HashMap<>();

    public boolean isFrozen(Entity e) {
        return this.clientActiveEntityFreeze.getOrDefault(e.dimension, Collections.emptySet()).contains(e.getEntityId());
    }

    @Override
    public void clear(DimensionType dimType) {
        this.clientActiveEntityFreeze.remove(dimType);
    }

    @Override
    public void clearClient() {
        this.clientActiveEntityFreeze.clear();
    }

    public static class Reader extends ClientDataReader<ClientTimeFreezeEntities> {

        @Override
        public void readFromIncomingFullSync(ClientTimeFreezeEntities data, CompoundNBT compound) {
            this.readEntityInformation(data, compound);
        }

        @Override
        public void readFromIncomingDiff(ClientTimeFreezeEntities data, CompoundNBT compound) {
            this.readEntityInformation(data, compound);
        }

        private void readEntityInformation(ClientTimeFreezeEntities data, CompoundNBT compound) {
            CompoundNBT dimTypes = compound.getCompound("dimTypes");
            for (String key : dimTypes.keySet()) {
                DimensionType type = DimensionType.byName(new ResourceLocation(key));

                ListNBT list = dimTypes.getList(key, Constants.NBT.TAG_INT);
                Set<Integer> entities = new HashSet<>();
                for (int i = 0; i < list.size(); i++) {
                    entities.add(list.getInt(i));
                }

                data.clientActiveEntityFreeze.put(type, entities);
            }
        }
    }
}
