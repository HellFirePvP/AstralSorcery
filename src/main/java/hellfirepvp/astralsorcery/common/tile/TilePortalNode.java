/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TilePortalNode
 * Created by HellFirePvP
 * Date: 25.01.2018 / 20:09
 */
public class TilePortalNode extends TileEntityTick {

    private boolean isWorldGen = false;

    public TilePortalNode() {}

    @Override
    public void update() {
        super.update();

        if(!world.isRemote) {
            if(isWorldGen && ticksExisted % 60 == 0) {
                if(!MultiBlockArrays.patternSmallRuin.matches(this.world, this.pos.down(3))) {
                    this.world.setBlockToAir(this.pos);
                }
            }
        } else {
            playAmbientEffects();
        }
    }

    @SideOnly(Side.CLIENT)
    private void playAmbientEffects() {
        if(!ConstellationSkyHandler.getInstance().isNight(this.world) || rand.nextInt(5) != 0) return;

        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).gravity(0.004);
        p.offset(rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1), rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1), rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1));
        p.scale(0.2F + rand.nextFloat() * 0.1F).setAlphaMultiplier(0.8F);
        p.motion(
                rand.nextFloat() * 0.01F * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.01F * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * 0.01F * (rand.nextBoolean() ? 1 : -1)).setMaxAge(30 + rand.nextInt(20));
        p.setColor(new Color(0x3C00FF));

        if(rand.nextBoolean()) {
            Vector3 offset = new Vector3(rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1), rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1), rand.nextFloat() * 0.1 * (rand.nextBoolean() ? 1 : -1));
            p = EffectHelper.genericFlareParticle(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).gravity(0.004);
            p.offset(offset.getX(), offset.getY(), offset.getZ());
            p.scale(0.1F + rand.nextFloat() * 0.1F).setAlphaMultiplier(0.8F);
            p.motion(0, 0, 0).setMaxAge(30 + rand.nextInt(20));
            p.setColor(new Color(0x3C00FF));
            p = EffectHelper.genericFlareParticle(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5).gravity(0.004);
            p.offset(offset.getX(), offset.getY(), offset.getZ());
            p.scale(0.05F + rand.nextFloat() * 0.05F).setAlphaMultiplier(0.8F);
            p.motion(0, 0, 0).setMaxAge(30 + rand.nextInt(20));
            p.setColor(Color.WHITE);
        }
    }

    public void setWorldGen(boolean worldGen) {
        isWorldGen = worldGen;
    }

    public boolean isWorldGen() {
        return isWorldGen;
    }

    @Override
    public void onFirstTick() {}

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.isWorldGen = compound.getBoolean("worldgen");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setBoolean("worldgen", this.isWorldGen);
    }

}
