/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileAttunementAltar
 * Created by HellFirePvP
 * Date: 17.11.2019 / 07:49
 */
public class TileAttunementAltar extends TileEntityTick {

    //TESR
    public static final int MAX_START_ANIMATION_TICK = 60;
    public static final int MAX_START_ANIMATION_SPIN = 100;
    public int activationTick = 0;
    public int prevActivationTick = 0;
    public boolean animate = false, tesrLocked = true;

    public TileAttunementAltar() {
        super(TileEntityTypesAS.ATTUNEMENT_ALTAR);
    }

    @Override
    public void tick() {
        super.tick();

        if (!getWorld().isRemote()) {

        } else {
            tickEffects();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickEffects() {
        if (!hasMultiblock() || !doesSeeSky()) {
            tickEffectNonActive();
            return;
        }

        tickEffectActive();
    }

    @OnlyIn(Dist.CLIENT)
    private void tickEffectNonActive() {
        animate = false;

        prevActivationTick = activationTick;
        if (activationTick > 0) {
            activationTick--;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void tickEffectActive() {
        animate = true;

        prevActivationTick = activationTick;
        if (activationTick < MAX_START_ANIMATION_TICK) {
            activationTick++;
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().expand(3.5, 2, 3.5);
    }
}
