/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.util.fluid.CompatFluidStack;
import hellfirepvp.astralsorcery.common.util.fluid.handler.CompatFluidHandlerItemStack;
import hellfirepvp.astralsorcery.common.util.fluid.handler.ICompatFluidHandlerItem;
import hellfirepvp.astralsorcery.common.util.fluid.handler.tank.CompatFluidTank;
import hellfirepvp.astralsorcery.common.util.fluid.handler.tank.ICompatFluidTank;
import hellfirepvp.astralsorcery.common.util.fluid.handler.ICompatFluidHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.concurrent.Callable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryCapabilities
 * Created by HellFirePvP
 * Date: 19.07.2019 / 13:59
 */
public class RegistryCapabilities {

    private RegistryCapabilities() {}

    public static void initialize() {
        register(ICompatFluidHandler.class, new TankFluidStorage<>(), () -> new CompatFluidTank(CompatFluidStack.BUCKET_VOLUME));
        register(ICompatFluidHandlerItem.class, new TankFluidStorage<>(), () -> new CompatFluidHandlerItemStack(new ItemStack(Items.BUCKET), CompatFluidStack.BUCKET_VOLUME));
    }

    private static <T> void register(Class<T> capabilityClass, Capability.IStorage<T> capStorage, Callable<T> capProvider) {
        CapabilityManager.INSTANCE.register(capabilityClass, capStorage, capProvider);
    }

    private static class TankFluidStorage<T extends ICompatFluidHandler> implements Capability.IStorage<T> {

        @Override
        public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
            if (!(instance instanceof ICompatFluidTank)) {
                throw new RuntimeException("ICompatFluidHandler instance does not implement ICompatFluidTank");
            }

            CompoundNBT nbt = new CompoundNBT();
            ICompatFluidTank tank = (ICompatFluidTank) instance;
            CompatFluidStack fluid = tank.getFluid();
            if (fluid != null) {
                nbt.put("Fluid", fluid.serialize());
            } else {
                nbt.putString("Empty", "");
            }
            nbt.putInt("Capacity", tank.getCapacity());
            return nbt;
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
            if (!(instance instanceof CompatFluidTank)) {
                throw new RuntimeException("ICompatFluidHandler instance is not instance of CompatFluidTank");
            }

            CompoundNBT tags = (CompoundNBT) nbt;
            CompatFluidTank tank = (CompatFluidTank) instance;
            tank.setCapacity(tags.getInt("Capacity"));
            if (tags.contains("Empty")) {
                tank.setFluid(null);
            } else {
                tank.setFluid(CompatFluidStack.deserialize(tags.getCompound("Fluid")));
            }
        }
    }

}
