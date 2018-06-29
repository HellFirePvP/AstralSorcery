/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data;

import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.base.patreon.flare.PartialEntityFlare;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
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

    private Map<UUID, PartialEntityFlare> patreonFlaresClient = new HashMap<>();

    private Map<UUID, PartialEntityFlare> patreonFlaresServer = new HashMap<>();
    private List<UUID> flareAdditions = new LinkedList<>();
    private List<UUID> flareRemovals = new LinkedList<>();

    private NBTTagCompound clientReadBuffer = new NBTTagCompound();

    @Nullable
    public PartialEntityFlare getFlare(Side side, UUID uuid) {
        if (side == Side.CLIENT) {
            return patreonFlaresClient.get(uuid);
        } else {
            return patreonFlaresServer.get(uuid);
        }
    }

    public Collection<PartialEntityFlare> getFlares(Side side) {
        return side == Side.CLIENT ? patreonFlaresClient.values() : patreonFlaresServer.values();
    }

    public PartialEntityFlare createFlare(EntityPlayer player, PatreonEffectHelper.PatreonEffect value) {
        UUID owner = player.getUniqueID();
        PartialEntityFlare flare = new PartialEntityFlare(value.getChosenColor(), owner);
        flare.setPositionNear(player);

        patreonFlaresServer.put(owner, flare);
        flareRemovals.remove(owner);
        if (!flareAdditions.contains(owner)) {
            flareAdditions.add(owner);
        }
        markDirty();

        return flare;
    }

    public void updateFlare(PartialEntityFlare flare) {
        UUID owner = flare.getOwnerUUID();
        flareRemovals.remove(owner);
        if (!flareAdditions.contains(owner)) {
            flareAdditions.add(owner);
        }
        markDirty();
    }

    public void destoryFlare(PartialEntityFlare flare) {
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
        for (Map.Entry<UUID, PartialEntityFlare> flares : this.patreonFlaresServer.entrySet()) {
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
            PartialEntityFlare flare = this.patreonFlaresServer.get(added);
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
            if (pe != null && pe.getChosenColor() != null) {
                PartialEntityFlare flare = this.patreonFlaresClient.get(owner);
                if (flare == null) {
                    flare = new PartialEntityFlare(pe.getChosenColor(), owner);
                    this.patreonFlaresClient.put(owner, flare);
                }
                flare.readFromNBT(flareTag);
            }
        }

        NBTTagList remove = ((DataPatreonFlares) serverData).clientReadBuffer.getTagList("flareRemovals", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < remove.tagCount(); i++) {
            NBTTagCompound cmp = remove.getCompoundTagAt(i);
            UUID owner = cmp.getUniqueId("owner");

            PartialEntityFlare flare = this.patreonFlaresClient.remove(owner);
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
