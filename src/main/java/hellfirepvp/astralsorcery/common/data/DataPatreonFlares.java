/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data;

import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.base.patreon.flare.PatreonPartialEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DataPatreonFlares
 * Created by HellFirePvP
 * Date: 23.06.2018 / 14:17
 */
public class DataPatreonFlares extends AbstractData {

    private Map<UUID, PatreonPartialEntity> patreonFlaresClient = new HashMap<>();

    private Map<UUID, PatreonPartialEntity> patreonFlaresServer = new HashMap<>();
    private List<UUID> flareAdditions = new LinkedList<>();
    private List<UUID> flareRemovals = new LinkedList<>();

    private NBTTagCompound clientReadBuffer = new NBTTagCompound();

    @Nullable
    public PatreonPartialEntity getEntity(Side side, UUID uuid) {
        if (side == Side.CLIENT) {
            return patreonFlaresClient.get(uuid);
        } else {
            return patreonFlaresServer.get(uuid);
        }
    }

    public Collection<PatreonPartialEntity> getEntities(Side side) {
        return side == Side.CLIENT ? patreonFlaresClient.values() : patreonFlaresServer.values();
    }

    //Only actually called when there's an entity to be provided.
    public PatreonPartialEntity createEntity(EntityPlayer player, PatreonEffectHelper.PatreonEffect value) {
        UUID owner = player.getUniqueID();
        PatreonPartialEntity entity = value.createEntity(owner);
        entity.setPositionNear(player);

        patreonFlaresServer.put(owner, entity);
        flareRemovals.remove(owner);
        if (!flareAdditions.contains(owner)) {
            flareAdditions.add(owner);
        }
        markDirty();

        return entity;
    }

    public void updateEntity(PatreonPartialEntity flare) {
        UUID owner = flare.getOwnerUUID();
        flareRemovals.remove(owner);
        if (!flareAdditions.contains(owner)) {
            flareAdditions.add(owner);
        }
        markDirty();
    }

    public void destroyEntity(PatreonPartialEntity flare) {
        UUID owner = flare.getOwnerUUID();
        flareAdditions.remove(owner);
        if (!flareRemovals.contains(owner)) {
            flareRemovals.add(owner);
        }
        markDirty();

        flare.setRemoved(true);
    }

    public void cleanUp(Side side) {
        if (side == Side.CLIENT) {
            this.patreonFlaresClient.clear();
            this.clientReadBuffer = new NBTTagCompound();
        } else {
            this.patreonFlaresServer.clear();
            this.flareRemovals.clear();
            this.flareAdditions.clear();
        }
    }

    @Override
    public void writeAllDataToPacket(NBTTagCompound compound) {
        NBTTagList entries = new NBTTagList();
        for (Map.Entry<UUID, PatreonPartialEntity> flares : this.patreonFlaresServer.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();

            tag.setUniqueId("owner", flares.getKey());

            NBTTagCompound flareTag = new NBTTagCompound();
            flares.getValue().writeToNBT(flareTag);
            tag.setTag("flareData", flareTag);

            entries.appendTag(tag);
        }
        compound.setTag("flareAdditions", entries);
        compound.setTag("flareRemovals", new NBTTagList());
    }

    @Override
    public void writeToPacket(NBTTagCompound compound) {
        NBTTagList additions = new NBTTagList();
        for (UUID added : this.flareAdditions) {
            NBTTagCompound tag = new NBTTagCompound();
            PatreonPartialEntity flare = this.patreonFlaresServer.get(added);
            if (flare == null) continue;

            tag.setUniqueId("owner", added);

            NBTTagCompound flareTag = new NBTTagCompound();
            flare.writeToNBT(flareTag);
            tag.setTag("flareData", flareTag);

            additions.appendTag(tag);
        }
        compound.setTag("flareAdditions", additions);
        NBTTagList removals = new NBTTagList();
        for (UUID added : this.flareRemovals) {
            NBTTagCompound tag = new NBTTagCompound();

            tag.setUniqueId("owner", added);

            removals.appendTag(tag);
        }
        compound.setTag("flareRemovals", removals);
    }

    @Override
    public void readRawFromPacket(NBTTagCompound compound) {
        this.clientReadBuffer = compound;
    }

    @Override
    public void handleIncomingData(AbstractData serverData) {
        if (serverData == null || !(serverData instanceof DataPatreonFlares)) return;

        NBTTagList add = ((DataPatreonFlares) serverData).clientReadBuffer.getTagList("flareAdditions", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < add.tagCount(); i++) {
            NBTTagCompound cmp = add.getCompoundTagAt(i);
            UUID owner = cmp.getUniqueId("owner");
            NBTTagCompound flareTag = cmp.getCompoundTag("flareData");

            PatreonEffectHelper.PatreonEffect pe = PatreonEffectHelper.getEffect(Side.SERVER, owner);
            if (pe != null && pe.hasPartialEntity()) {
                PatreonPartialEntity entity = this.patreonFlaresClient.get(owner);
                if (entity == null) {
                    entity = pe.createEntity(owner);
                    if (entity == null) {
                        throw new IllegalStateException("FATAL ERROR: Eventhough a PatreonEffect guaranteed a proper partial entity, it was unable to provide one!");
                    }
                    this.patreonFlaresClient.put(owner, entity);
                }
                entity.readFromNBT(flareTag);
            }
        }

        NBTTagList remove = ((DataPatreonFlares) serverData).clientReadBuffer.getTagList("flareRemovals", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < remove.tagCount(); i++) {
            NBTTagCompound cmp = remove.getCompoundTagAt(i);
            UUID owner = cmp.getUniqueId("owner");

            PatreonPartialEntity flare = this.patreonFlaresClient.remove(owner);
            if (flare != null) {
                flare.setRemoved(true);
            }
        }
    }
    public static class Provider extends ProviderAutoAllocate<DataPatreonFlares> {


        public Provider(String key) {
            super(key);
        }

        @Override
        public DataPatreonFlares provideNewInstance(Side side) {
            return new DataPatreonFlares();
        }

    }
}
