package hellfirepvp.astralsorcery.common.constellation.perk;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IPlayerCapabilityPerks
 * Created by HellFirePvP
 * Date: 17.11.2016 / 00:02
 */
public interface IPlayerCapabilityPerks {

    public void updatePerks(List<ConstellationPerk> perks);

    public List<ConstellationPerk> getCurrentPlayerPerks();

    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

        private static IPlayerCapabilityPerks serializerInstance = PlayerPerkHelper.PLAYER_PERKS.getDefaultInstance();

        @Override
        public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == PlayerPerkHelper.PLAYER_PERKS;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
            return hasCapability(capability, facing) ? PlayerPerkHelper.PLAYER_PERKS.<T>cast(serializerInstance) : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return (NBTTagCompound) PlayerPerkHelper.PLAYER_PERKS.getStorage().writeNBT(PlayerPerkHelper.PLAYER_PERKS, serializerInstance, null);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            PlayerPerkHelper.PLAYER_PERKS.getStorage().readNBT(PlayerPerkHelper.PLAYER_PERKS, serializerInstance, null, nbt);
        }

    }

    //TODO store/load properly
    public static class Storage implements Capability.IStorage<IPlayerCapabilityPerks> {

        @Override
        public NBTBase writeNBT(Capability<IPlayerCapabilityPerks> capability, IPlayerCapabilityPerks instance, EnumFacing side) {
            NBTTagCompound out = new NBTTagCompound();
            return out;
        }

        @Override
        public void readNBT(Capability<IPlayerCapabilityPerks> capability, IPlayerCapabilityPerks instance, EnumFacing side, NBTBase nbt) {

        }

    }

    public static class Impl implements IPlayerCapabilityPerks {

        private List<ConstellationPerk> perks = new LinkedList<>();

        @Override
        public void updatePerks(List<ConstellationPerk> perks) {
            this.perks = perks;
        }

        @Override
        public List<ConstellationPerk> getCurrentPlayerPerks() {
            return Collections.unmodifiableList(perks);
        }

    }

}
