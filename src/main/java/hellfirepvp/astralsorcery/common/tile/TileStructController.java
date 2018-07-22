/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileStructController
 * Created by HellFirePvP
 * Date: 25.01.2018 / 20:09
 */
public class TileStructController extends TileEntityTick {

    private StructType type;

    public TileStructController() {}

    @Override
    public void update() {
        super.update();

        if(!world.isRemote) {
            if(ticksExisted % 60 == 0) {
                if (this.type == null) {
                    this.world.setBlockToAir(this.pos);
                } else {
                    PatternBlockArray match = this.type.matchProvider.apply(null);
                    if (match == null || !match.matches(this.world, this.pos.down(3))) {
                        this.world.setBlockToAir(this.pos);
                    }
                }
            }
        } else {
            playAmbientEffects();
        }
    }

    @SideOnly(Side.CLIENT)
    private void playAmbientEffects() {
        if(!ConstellationSkyHandler.getInstance().isNight(this.world) || rand.nextInt(5) != 0) return;
        if(this.type == null) return;

        if (type == StructType.GATE) {
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

            if (rand.nextInt(35) == 0) {
                Vector3 posFrom = new Vector3(getPos()).add(0.4 + rand.nextFloat() * 0.2, 0.4 + rand.nextFloat() * 0.2, 0.4 + rand.nextFloat() * 0.2);
                Vector3 posTo = new Vector3(getPos()).addZ(rand.nextBoolean() ? 2 : -1);
                posTo.addY(-1 + rand.nextFloat() * 3).addX(rand.nextFloat());
                EffectHandler.getInstance().lightning(posFrom, posTo).setOverlayColor(new Color(0x3C00FF));
            }
        } else if (type == StructType.SUPPORT) {

        }
    }

    public void setType(StructType type) {
        this.type = type;
    }

    @Override
    public void onFirstTick() {}

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.type = StructType.values()[MathHelper.clamp(compound.getInteger("structtype"), 0, 1)];
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        compound.setInteger("structtype", this.type == null ? StructType.GATE.ordinal() : this.type.ordinal());
    }

    public static enum StructType {

        GATE((v) -> MultiBlockArrays.patternSmallRuin),
        SUPPORT((v) -> null),
        ARC((v) -> null),
        LENS((v) -> null),
        CORE((v) -> null);

        private Function<Void, PatternBlockArray> matchProvider;

        StructType(Function<Void, PatternBlockArray> matchProvider) {
            this.matchProvider = matchProvider;
        }

    }

}
