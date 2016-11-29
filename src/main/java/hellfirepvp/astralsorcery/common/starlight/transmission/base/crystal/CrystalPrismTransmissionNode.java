package hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimplePrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalPrismTransmissionNode
 * Created by HellFirePvP
 * Date: 05.08.2016 / 00:07
 */
public class CrystalPrismTransmissionNode extends SimplePrismTransmissionNode {

    private CrystalProperties properties;
    private float additionalLoss = 1F;

    public CrystalPrismTransmissionNode(@Nonnull BlockPos thisPos, CrystalProperties properties) {
        super(thisPos);
        this.properties = properties;
    }

    public CrystalPrismTransmissionNode(@Nonnull BlockPos thisPos) {
        super(thisPos);
    }

    public void updateAdditionalLoss(float loss) {
        this.additionalLoss = loss;
    }

    @Override
    public float getAdditionalTransmissionLossMultiplier() {
        return additionalLoss;
    }

    @Override
    public CrystalProperties getTransmissionProperties() {
        return properties;
    }

    @Override
    public TransmissionClassRegistry.TransmissionProvider getProvider() {
        return new Provider();
    }


    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.properties = CrystalProperties.readFromNBT(compound);
        this.additionalLoss = compound.getFloat("lossMultiplier");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        this.properties.writeToNBT(compound);
        compound.setFloat("lossMultiplier", this.additionalLoss);
    }

    public static class Provider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public IPrismTransmissionNode provideEmptyNode() {
            return new CrystalPrismTransmissionNode(null);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":CrystalPrismTransmissionNode";
        }

    }

}
