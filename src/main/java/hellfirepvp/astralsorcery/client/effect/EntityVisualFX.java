/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXRenderOffsetFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXScaleFunction;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.OffsetAABB;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EntityVisualFX
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class EntityVisualFX extends EntityFX {

    private static final AABB ORIGIN_BOX = new AABB(0, 0, 0, 0, 0, 0);
    private OffsetAABB renderBox = new OffsetAABB(ORIGIN_BOX);

    private float alpha = 1F;
    private float scale = 1F;

    private FXAlphaFunction alphaFunction = FXAlphaFunction.CONSTANT;
    private FXScaleFunction scaleFunction = FXScaleFunction.IDENTITY;
    private FXColorFunction colorFunction = FXColorFunction.WHITE;

    private FXRenderOffsetFunction renderOffsetFunction = FXRenderOffsetFunction.IDENTITY;

    protected EntityVisualFX(Vector3 pos) {
        super(pos);
        this.updateBoundingBox();
    }

    protected void setRenderBox(AABB box) {
        this.renderBox = new OffsetAABB(box);
    }

    protected void updateBoundingBox() {
        this.renderBox.setOffset(this.pos);
    }

    public <T extends EntityVisualFX> T setAlpha(float alpha) {
        this.alpha = alpha;
        return (T) this;
    }

    public <T extends EntityVisualFX> T setScale(float scale) {
        this.scale = scale;
        return (T) this;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public float getScale() {
        return this.scale;
    }

    public float getAlpha(float pTicks) {
        return this.alphaFunction.getAlpha(this, this.getAlpha(), pTicks);
    }

    public int getAlphaI(float pTicks) {
        return Mth.clamp((int) (this.getAlpha(pTicks) * 255F), 0, 255);
    }

    public float getScale(float pTicks) {
        return this.scaleFunction.getScale(this, this.getScale(), pTicks);
    }

    public ColorWrapper getColor(float pTicks) {
        return this.colorFunction.getColor(this, pTicks);
    }

    public Vector3 getRenderOffset(Vector3 pos, float pTicks) {
        return this.renderOffsetFunction.getRenderOffset(this, pos, pTicks);
    }

    @Override
    public <T extends EntityFX> T setPos(Vector3 pos) {
        super.setPos(pos);
        this.updateBoundingBox();
        return (T) this;
    }

    @Override
    public void tick() {
        super.tick();
        this.updateBoundingBox();
    }

    public abstract void render(EffectTemplate<?> ctx, Camera renderInfo, VertexConsumer vb, float pTicks);

    public AABB getRenderBoundingBox() {
        return this.renderBox.getMovedBox().inflate(1);
    }

    public int getLight(float pTicks) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return 0;
        BlockPos fxPos = this.getPos().toBlockPos();
        return level.hasChunkAt(fxPos) ? LevelRenderer.getLightColor(level, fxPos) : 0;
    }

    public <T extends EntityVisualFX> T alpha(FXAlphaFunction<?> alphaFunction) {
        this.alphaFunction = alphaFunction;
        return (T) this;
    }

    public <T extends EntityVisualFX> T scale(FXScaleFunction<?> scaleFunction) {
        this.scaleFunction = scaleFunction;
        return (T) this;
    }

    public <T extends EntityVisualFX> T color(FXColorFunction<?> colorFunction) {
        this.colorFunction = colorFunction;
        return (T) this;
    }

    public <T extends EntityVisualFX> T renderOffset(FXRenderOffsetFunction<?> renderOffsetFunction) {
        this.renderOffsetFunction = renderOffsetFunction;
        return (T) this;
    }
}
