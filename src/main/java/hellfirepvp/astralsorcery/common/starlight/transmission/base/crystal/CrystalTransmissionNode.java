/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission.base.crystal;

import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionProvider;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.crystal.CrystalProperties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalTransmissionNode
 * Created by HellFirePvP
 * Date: 05.08.2016 / 00:06
 */
public class CrystalTransmissionNode extends SimpleTransmissionNode {

    private CrystalProperties properties;
    private float additionalLoss = 1F;

    public CrystalTransmissionNode(BlockPos thisPos, CrystalProperties properties) {
        super(thisPos);
        this.properties = properties;
    }

    public CrystalTransmissionNode(BlockPos thisPos) {
        super(thisPos);
    }

    public void updateAdditionalLoss(float loss) {
        this.additionalLoss = loss;
    }

    @Override
    public void onTransmissionTick(World world) {
        //TODO lens
        //TileCrystalLens lens = MiscUtils.getTileAt(world, getLocationPos(), TileCrystalLens.class, false);
        //if(lens != null) {
        //    lens.onTransmissionTick();
        //}
    }

    @Override
    public float getAdditionalTransmissionLossMultiplier() {
        return additionalLoss;
    }

    @Override
    public boolean needsTransmissionUpdate() {
        return true;
    }

    @Override
    public CrystalProperties getTransmissionProperties() {
        return properties;
    }

    @Override
    public TransmissionProvider getProvider() {
        return new Provider();
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        super.readFromNBT(compound);

        this.properties = CrystalProperties.readFromNBT(compound);
        this.additionalLoss = compound.getFloat("lossMultiplier");
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        super.writeToNBT(compound);

        this.properties.writeToNBT(compound);
        compound.putFloat("lossMultiplier", this.additionalLoss);
    }

    public static class Provider extends TransmissionProvider {

        @Override
        public IPrismTransmissionNode get() {
            return new CrystalTransmissionNode(null);
        }

    }

}
