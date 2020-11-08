/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerReference
 * Created by HellFirePvP
 * Date: 18.10.2020 / 20:29
 */
public class PlayerReference {

    private final UUID playerUUID;
    private final IFormattableTextComponent playerName;

    public PlayerReference(UUID playerUUID, IFormattableTextComponent playerName) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
    }

    public static PlayerReference of(PlayerEntity player) {
        ITextComponent txt = player.getDisplayName();
        if (txt instanceof IFormattableTextComponent) {
            return new PlayerReference(player.getUniqueID(), (IFormattableTextComponent) txt);
        }
        return new PlayerReference(player.getUniqueID(), new StringTextComponent("").append(txt));
    }

    public boolean isPlayer(PlayerEntity player) {
        return this.getPlayerUUID().equals(player.getUniqueID());
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public ITextComponent getPlayerName() {
        return this.playerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerReference that = (PlayerReference) o;
        return Objects.equals(playerUUID, that.playerUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerUUID);
    }

    @Nullable
    public ServerPlayerEntity getOnlinePlayer() {
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        if (server == null) {
            throw new IllegalArgumentException("Called getOnlinePlayer on clientside or while no server is running!");
        }
        return server.getPlayerList().getPlayerByUUID(this.playerUUID);
    }

    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        this.writeToNBT(tag);
        return tag;
    }

    public void writeToNBT(CompoundNBT tag) {
        tag.putUniqueId("playerUUID", this.playerUUID);
        tag.putString("playerName", ITextComponent.Serializer.toJson(this.playerName));
    }

    public void write(PacketBuffer buf) {
        ByteBufUtils.writeUUID(buf, this.playerUUID);
        ByteBufUtils.writeTextComponent(buf, this.playerName);
    }

    public static PlayerReference deserialize(CompoundNBT tag) {
        return new PlayerReference(tag.getUniqueId("playerUUID"), ITextComponent.Serializer.getComponentFromJson(tag.getString("playerName")));
    }

    public static PlayerReference read(PacketBuffer buf) {
        return new PlayerReference(ByteBufUtils.readUUID(buf), ByteBufUtils.readTextComponent(buf));
    }
}
