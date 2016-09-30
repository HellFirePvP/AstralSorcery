package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.tile.base.TileReceiverBaseInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileRitualPedestal
 * Created by HellFirePvP
 * Date: 28.09.2016 / 13:47
 */
public class TileRitualPedestal extends TileReceiverBaseInventory {

    private boolean dirty = false;
    private boolean doesSeeSky = false;

    public TileRitualPedestal() {
        super(1);
    }

    @Override
    public void update() {
        super.update();

        if((ticksExisted & 15) == 0) {
            updateSkyState(worldObj.canSeeSky(getPos()));
        }

        if(dirty) {
            dirty = false;
            TransmissionReceiverRitualPedestal recNode = tryGetNode();
            if(recNode != null) {
                recNode.updateSkyState(doesSeeSky);
            }
        }
    }

    public boolean doesSeeSky() {
        return doesSeeSky;
    }

    protected void updateSkyState(boolean seesSky) {
        boolean update = doesSeeSky != seesSky;
        this.doesSeeSky = seesSky;
        if(update) {
            markForUpdate();
            markDirty();
        }
    }

    @Override
    protected void onInventoryChanged() {
        if(!worldObj.isRemote) {
            ItemStack in = getStackInSlot(0);
            if(in != null && in.getItem() != null &&
                    in.getItem() instanceof ItemTunedCrystalBase) {
                CrystalProperties properties = CrystalProperties.getCrystalProperties(in);
                Constellation tuned = ItemTunedCrystalBase.getConstellation(in);
                Constellation trait = ItemTunedCrystalBase.getTrait(in);
                TransmissionReceiverRitualPedestal recNode = tryGetNode();
                if(recNode != null) {
                    recNode.updateCrystalProperties(properties, tuned, trait);
                } else {
                    AstralSorcery.log.warn("[AstralSorcery] Updated inventory and tried to update pedestal state.");
                    AstralSorcery.log.warn("[AstralSorcery] Tried to find receiver node at dimId=" + worldObj.provider.getDimension() + " pos=" + getPos() + " - couldn't find it.");
                }
            }
        }
    }

    public void markDirty() {
        this.dirty = true;
    }

    @Override
    public String getInventoryName() {
        return getUnLocalizedDisplayName();
    }

    @Nullable
    @Override
    public String getUnLocalizedDisplayName() {
        return "tile.BlockRitualPedestal.name";
    }

    @Override
    public ITransmissionReceiver provideEndpoint(BlockPos at) {
        return new TransmissionReceiverRitualPedestal(at, doesSeeSky);
    }

    public static class TransmissionReceiverRitualPedestal extends SimpleTransmissionReceiver {

        private boolean doesSeeSky;
        private Constellation channeling, trait;
        private CrystalProperties properties;
        private int channeled = 0;

        public TransmissionReceiverRitualPedestal(@Nonnull BlockPos thisPos, boolean doesSeeSky) {
            super(thisPos);
            this.doesSeeSky = doesSeeSky;
        }

        @Override
        public void onStarlightReceive(World world, boolean isChunkLoaded, Constellation type, double amount) {

        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            super.readFromNBT(compound);

            doesSeeSky = compound.getBoolean("doesSeeSky");
            channeled = compound.getInteger("channeled");
            properties = CrystalProperties.readFromNBT(compound);
            channeling = Constellation.readFromNBT(compound, Constellation.getDefaultSaveKey() + "Normal");
            trait = Constellation.readFromNBT(compound, Constellation.getDefaultSaveKey() + "Trait");
        }

        @Override
        public void writeToNBT(NBTTagCompound compound) {
            super.writeToNBT(compound);

            compound.setBoolean("doesSeeSky", doesSeeSky);
            compound.setInteger("channeled", channeled);
            if(properties != null) {
                properties.writeToNBT(compound);
            }
            if(channeling != null) {
                channeling.writeToNBT(compound, Constellation.getDefaultSaveKey() + "Normal");
            }
            if(trait != null) {
                trait.writeToNBT(compound, Constellation.getDefaultSaveKey() + "Trait");
            }
        }

        @Override
        public TransmissionClassRegistry.TransmissionProvider getProvider() {
            return new PedestalReceiverProvider();
        }

        public void update(boolean doesSeeSky, Constellation bufferChanneling, Constellation trait) {
            this.doesSeeSky = doesSeeSky;
            this.channeling = bufferChanneling;
            this.trait = trait;
        }

        public void updateSkyState(boolean doesSeeSky) {
            this.doesSeeSky = doesSeeSky;
        }

        public void updateCrystalProperties(CrystalProperties properties, Constellation channeling, Constellation trait) {
            this.properties = properties;
            this.channeling = channeling;
            this.trait = trait;
        }

    }

    public static class PedestalReceiverProvider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public TransmissionReceiverRitualPedestal provideEmptyNode() {
            return new TransmissionReceiverRitualPedestal(null, false);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":TransmissionReceiverRitualPedestal";
        }

    }

}
