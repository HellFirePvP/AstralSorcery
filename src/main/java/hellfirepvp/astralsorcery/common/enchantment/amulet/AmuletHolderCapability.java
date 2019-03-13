/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.amulet;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AmuletHolderCapability
 * Created by HellFirePvP
 * Date: 19.05.2018 / 22:13
 */
public class AmuletHolderCapability implements INBTSerializable<NBTTagCompound> {

    public static final ResourceLocation CAP_AMULETHOLDER_NAME = new ResourceLocation(AstralSorcery.MODID, "cap_item_amulet_holder");

    @CapabilityInject(AmuletHolderCapability.class)
    public static Capability<AmuletHolderCapability> CAPABILITY_AMULET_HOLDER = null;

    private UUID holderUUID = null;

    public UUID getHolderUUID() {
        return holderUUID;
    }

    public void setHolderUUID(UUID holderUUID) {
        this.holderUUID = holderUUID;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound cmp = new NBTTagCompound();
        if(holderUUID != null) {
            cmp.setUniqueId("AS_Amulet_Holder", holderUUID);
        }
        return cmp;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if(nbt.hasUniqueId("AS_Amulet_Holder")) {
            this.holderUUID = nbt.getUniqueId("AS_Amulet_Holder");
        } else {
            this.holderUUID = null;
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

        private final AmuletHolderCapability defaultImpl = new AmuletHolderCapability();

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability.equals(CAPABILITY_AMULET_HOLDER);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return hasCapability(capability, facing) ? CAPABILITY_AMULET_HOLDER.cast(defaultImpl) : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return defaultImpl.serializeNBT();
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            defaultImpl.deserializeNBT(nbt);
        }

    }

    public static class Factory implements Callable<AmuletHolderCapability> {

        @Override
        public AmuletHolderCapability call() throws Exception {
            return new AmuletHolderCapability();
        }

    }

}
