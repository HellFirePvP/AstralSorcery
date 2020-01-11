/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.client;

import hellfirepvp.astralsorcery.common.data.sync.base.ClientData;
import hellfirepvp.astralsorcery.common.data.sync.base.ClientDataReader;
import hellfirepvp.astralsorcery.common.data.sync.server.DataTimeFreezeEffects;
import hellfirepvp.astralsorcery.common.util.time.TimeStopEffectHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientTimeFreezeEffects
 * Created by HellFirePvP
 * Date: 31.08.2019 / 14:02
 */
public class ClientTimeFreezeEffects extends ClientData<ClientTimeFreezeEffects> {

    private Map<DimensionType, List<TimeStopEffectHelper>> clientActiveFreezeZones = new HashMap<>();

    @Nonnull
    public List<TimeStopEffectHelper> getTimeStopEffects(IWorld world) {
        return getTimeStopEffects(world.getDimension().getType());
    }

    @Nonnull
    public List<TimeStopEffectHelper> getTimeStopEffects(DimensionType dimType) {
        return clientActiveFreezeZones.getOrDefault(dimType, Collections.emptyList());
    }

    private void applyChange(DataTimeFreezeEffects.ServerSyncAction action) {
        DimensionType type = action.getDimType();
        switch (action.getType()) {
            case ADD:
                List<TimeStopEffectHelper> zones = clientActiveFreezeZones.computeIfAbsent(type, (id) -> new LinkedList<>());
                zones.add(action.getInvolvedEffect());
                break;
            case REMOVE:
                if (clientActiveFreezeZones.containsKey(type)) {
                    clientActiveFreezeZones.get(type).remove(action.getInvolvedEffect());
                }
                break;
            case CLEAR:
                clientActiveFreezeZones.remove(type);
                break;
            default:
                break;
        }
    }

    @Override
    public void clear(DimensionType dimType) {
        this.clientActiveFreezeZones.remove(dimType);
    }

    @Override
    public void clearClient() {
        this.clientActiveFreezeZones.clear();
    }

    public static class Reader extends ClientDataReader<ClientTimeFreezeEffects> {

        @Override
        public void readFromIncomingFullSync(ClientTimeFreezeEffects data, CompoundNBT compound) {
            data.clientActiveFreezeZones.clear();

            CompoundNBT dimTag = compound.getCompound("dimTypes");
            for (String dimKey : dimTag.keySet()) {
                DimensionType dimType = DimensionManager.getRegistry().getValue(new ResourceLocation(dimKey)).orElse(null);
                if (dimType == null) {
                    continue;
                }

                List<TimeStopEffectHelper> effects = new LinkedList<>();
                ListNBT listEffects = dimTag.getList(dimKey, Constants.NBT.TAG_COMPOUND);
                for (INBT iNBT : listEffects) {
                    effects.add(TimeStopEffectHelper.deserializeNBT((CompoundNBT) iNBT));
                }
                data.clientActiveFreezeZones.put(dimType, effects);
            }
        }

        @Override
        public void readFromIncomingDiff(ClientTimeFreezeEffects data, CompoundNBT compound) {
            ListNBT changes = compound.getList("changes", Constants.NBT.TAG_COMPOUND);
            for (INBT iNBT : changes) {
                DataTimeFreezeEffects.ServerSyncAction action = DataTimeFreezeEffects.ServerSyncAction.deserializeNBT((CompoundNBT) iNBT);
                if (action != null) {
                    data.applyChange(action);
                }
            }
        }
    }
}
