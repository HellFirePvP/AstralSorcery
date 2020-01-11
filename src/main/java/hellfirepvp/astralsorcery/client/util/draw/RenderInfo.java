/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.draw;

import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderInfo
 * Created by HellFirePvP
 * Date: 08.07.2019 / 21:16
 */
public class RenderInfo implements ITickHandler {

    private static final RenderInfo instance = new RenderInfo();

    /** The X component of the entity's yaw rotation */
    private float rotationX;
    /** The combined X and Z components of the entity's pitch rotation */
    private float rotationXZ;
    /** The Z component of the entity's yaw rotation */
    private float rotationZ;
    /** The Y component (scaled along the Z axis) of the entity's pitch rotation */
    private float rotationYZ;
    /** The Y component (scaled along the X axis) of the entity's pitch rotation */
    private float rotationXY;

    private Vec3d view = Vec3d.ZERO;

    private RenderInfo() {}

    public static RenderInfo getInstance() {
        return instance;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        ActiveRenderInfo info = this.getARI();
        if (info != null) {
            this.rotationX = MathHelper.cos(info.getYaw() * ((float)Math.PI / 180F));
            this.rotationZ = MathHelper.sin(info.getYaw() * ((float)Math.PI / 180F));
            this.rotationYZ = -this.rotationZ * MathHelper.sin(info.getPitch() * ((float)Math.PI / 180F));
            this.rotationXY = this.rotationX * MathHelper.sin(info.getPitch() * ((float)Math.PI / 180F));
            this.rotationXZ = MathHelper.cos(info.getPitch() * ((float)Math.PI / 180F));
        }
    }

    public float getRotationX() {
        return rotationX;
    }

    public float getRotationXZ() {
        return rotationXZ;
    }

    public float getRotationZ() {
        return rotationZ;
    }

    public float getRotationYZ() {
        return rotationYZ;
    }

    public float getRotationXY() {
        return rotationXY;
    }

    @Nullable
    public ActiveRenderInfo getARI() {
        GameRenderer gr = Minecraft.getInstance().gameRenderer;
        if (gr != null) {
            return gr.getActiveRenderInfo();
        }
        return null;
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.RENDER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.START;
    }

    @Override
    public String getName() {
        return "RenderInfo Cache Compat";
    }
}
