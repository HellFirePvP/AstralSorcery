/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXCube;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileAreaOfInfluence;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.EnumSet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AreaOfInfluencePreview
 * Created by HellFirePvP
 * Date: 25.04.2020 / 15:53
 */
public class AreaOfInfluencePreview implements ITickHandler {

    public static final AreaOfInfluencePreview INSTANCE = new AreaOfInfluencePreview();

    private static final int MAX_LIFE = 40;
    private static final float alphaTick = 1F / MAX_LIFE;
    private static final float sizeCube1 = 0.9F, sizeCube2 = 1.05F;

    private DimensionType tileDimension = null;
    private BlockPos tilePosition = null;
    private FXCube effect1 = null, effect2 = null;

    private AreaOfInfluencePreview() {}

    public void show(TileAreaOfInfluence aoeTile) {
        if (!(aoeTile instanceof TileEntity)) {
            return;
        }
        this.tileDimension = aoeTile.getDimensionType();
        this.tilePosition = aoeTile.getEffectOriginPosition();
    }

    public void clearClient() {
        this.tileDimension = null;
        this.tilePosition = null;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        if (tileDimension == null || tilePosition == null) {
            this.removeEffects();
            return;
        }
        World clientWorld = Minecraft.getInstance().world;
        if (clientWorld == null) {
            this.clearClient();
            this.removeEffects();
            return;
        }
        DimensionType clientDimType = clientWorld.getDimension().getType();
        if (!clientDimType.equals(this.tileDimension)) {
            this.clearClient();
            this.removeEffects();
            return;
        }
        TileAreaOfInfluence aoeTile = MiscUtils.getTileAt(clientWorld, this.tilePosition, TileAreaOfInfluence.class, true);
        if (aoeTile != null && aoeTile.providesEffect() && shouldContinueEffect(aoeTile)) {
            this.effect1 = uptickEffect(this.effect1, sizeCube1, aoeTile);
            this.effect2 = uptickEffect(this.effect2, sizeCube2, aoeTile);
        } else {
            this.effect1 = downtickEffect(this.effect1, sizeCube1, aoeTile);
            this.effect2 = downtickEffect(this.effect2, sizeCube2, aoeTile);
            if (this.effect1 == null && this.effect2 == null) {
                clearClient();
            }
        }
    }

    private boolean shouldContinueEffect(TileAreaOfInfluence aoeTile) {
        if (Minecraft.getInstance().player == null) {
            return false;
        }
        float effectRadius = aoeTile.getRadius();
        if (effectRadius <= 0F) {
            return false;
        }
        Vector3 offset = aoeTile.getEffectPosition();
        double distance = offset.distance(Minecraft.getInstance().player);
        if (distance > effectRadius * 3) {
            return false;
        }
        return distance <= 48;
    }

    private FXCube downtickEffect(@Nullable FXCube cube, float sizeMultiplier, @Nullable TileAreaOfInfluence aoeTile) {
        if (cube != null && !cube.isRemoved()) {
            if (aoeTile != null) {
                updateEffect(cube, sizeMultiplier, aoeTile);
            }
            cube.setAlphaMultiplier(MathHelper.clamp(cube.getAlphaMultiplier() - alphaTick, 0F, 0.75F));
            if (!this.canRefresh(cube)) {
                cube = null;
            }
        }
        return cube;
    }

    private FXCube uptickEffect(@Nullable FXCube cube, float sizeMultiplier, TileAreaOfInfluence aoeTile) {
        if (cube != null) {
            if (cube.isRemoved()) {
                EffectHelper.refresh(cube, EffectTemplatesAS.CUBE_AREA_OF_EFFECT);
            }
            cube.setAlphaMultiplier(MathHelper.clamp(cube.getAlphaMultiplier() + alphaTick, 0F, 0.75F));
            updateEffect(cube, sizeMultiplier, aoeTile);
        } else {
            cube = createCube(sizeMultiplier, aoeTile);
        }
        return cube;
    }

    private void updateEffect(FXCube cube, float sizeMultiplier, TileAreaOfInfluence aoeTile) {
        Color c = aoeTile.getEffectColor();
        if (c != null) {
            cube.color(VFXColorFunction.constant(c));
        } else {
            cube.color(VFXColorFunction.WHITE);
        }
        cube.setScaleMultiplier(aoeTile.getRadius() * sizeMultiplier);
        cube.setPosition(aoeTile.getEffectPosition());
    }

    private FXCube createCube(float sizeMultiplier, TileAreaOfInfluence aoeTile) {
        FXCube cube = EffectHelper.of(EffectTemplatesAS.CUBE_AREA_OF_EFFECT)
                .spawn(aoeTile.getEffectPosition())
                .tumble()
                .setTumbleIntensityMultiplier(0.06F)
                .setAlphaMultiplier(2 * alphaTick)
                .alpha((fx, alpha, pTicks) -> alpha)
                .color(VFXColorFunction.WHITE)
                .refresh(fx -> canRefresh((FXCube) fx));
        updateEffect(cube, sizeMultiplier, aoeTile);
        return cube;
    }

    private void removeEffects() {
        if (this.effect1 != null) {
            this.effect1.setAlphaMultiplier(0F);
            this.effect1 = null;
        }
        if (this.effect2 != null) {
            this.effect2.setAlphaMultiplier(0F);
            this.effect2 = null;
        }
    }

    private boolean canRefresh(FXCube cube) {
        return cube.getAlpha(1F) > 0F;
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.CLIENT);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Area of Effect Preview";
    }
}
