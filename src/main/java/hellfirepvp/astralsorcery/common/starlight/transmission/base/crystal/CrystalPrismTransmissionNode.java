/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.crystal.CrystalAttributes;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimplePrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionProvider;
import hellfirepvp.astralsorcery.common.tile.TilePrism;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalPrismTransmissionNode
 * Created by HellFirePvP
 * Date: 05.08.2016 / 00:07
 */
public class CrystalPrismTransmissionNode extends SimplePrismTransmissionNode {

    private CrystalAttributes attributes;
    private float additionalLoss = 1F;

    public CrystalPrismTransmissionNode(BlockPos thisPos, CrystalAttributes attributes) {
        super(thisPos);
        this.attributes = attributes;
    }

    public CrystalPrismTransmissionNode(BlockPos thisPos) {
        super(thisPos);
    }

    public boolean updateAdditionalLoss(float loss) {
        boolean didChange = this.additionalLoss != loss;
        this.additionalLoss = loss;
        return didChange;
    }

    @Override
    public void onTransmissionTick(World world, float starlightAmt, IWeakConstellation type) {
        TilePrism prism = MiscUtils.getTileAt(world, getLocationPos(), TilePrism.class, false);
        if (prism != null) {
            prism.transmissionTick(starlightAmt, type);
        }
    }

    @Override
    public float getTransmissionConsumptionMultiplier() {

        return additionalLoss;
    }
    @Override
    public boolean needsTransmissionUpdate() {
        return true;
    }

    @Override
    public CrystalAttributes getTransmissionProperties() {
        return attributes;
    }

    @Override
    public TransmissionProvider getProvider() {
        return new Provider();
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        super.readFromNBT(compound);

        this.attributes = CrystalAttributes.getCrystalAttributes(compound);
        this.additionalLoss = compound.getFloat("lossMultiplier");
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        super.writeToNBT(compound);

        if (this.attributes != null) {
            this.attributes.store(compound);
        }
        compound.putFloat("lossMultiplier", this.additionalLoss);
    }

    public static class Provider extends TransmissionProvider {

        @Override
        public CrystalPrismTransmissionNode get() {
            return new CrystalPrismTransmissionNode(null);
        }

    }

}
