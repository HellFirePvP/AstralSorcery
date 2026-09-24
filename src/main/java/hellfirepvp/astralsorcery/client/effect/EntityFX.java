/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

import hellfirepvp.astralsorcery.client.effect.function.*;
import hellfirepvp.astralsorcery.client.util.RenderVectorUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EntityFX
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EntityFX {

    protected final RandomSource rand = RandomSource.create();
    private static long counter = 0;
    private final long id;

    protected int age = 0;
    protected int maxAge = 40;
    protected int ageRefreshCount = 0;

    protected Vector3 pos;
    protected Vector3 prevPos;
    protected Vector3 motion = new Vector3();
    protected Vector3 gravity = new Vector3();
    private FXMotionFunction motionFunction = FXMotionFunction.IDENTITY;
    private FXPositionFunction positionFunction = FXPositionFunction.IDENTITY;
    private FXCollisionFunction collisionFunction = FXCollisionFunction.NO_COLLISION;
    private FXPersistenceFunction persistenceFunction = FXPersistenceFunction.RENDER_DISTANCE;

    private FXRefreshFunction refreshFunction = FXRefreshFunction.NO_REFRESH;
    private final Map<String, Object> customData = new HashMap<>();

    private boolean requestRemoval = false;
    private boolean removed = true;

    protected EntityFX(Vector3 pos) {
        this.id = counter++;
        this.pos = pos;
        this.prevPos = pos;
    }

    public final long getId() {
        return id;
    }

    public <T extends EntityFX> T setMaxAge(int maxAge) {
        this.maxAge = maxAge;
        return (T) this;
    }

    public int getMaxAge() {
        return this.maxAge;
    }

    public int getAge() {
        return this.age;
    }

    public int getAgeRefreshCount() {
        return this.ageRefreshCount;
    }

    public <T extends EntityFX> T setGravity(Vector3 gravity) {
        this.gravity = gravity;
        return (T) this;
    }

    public <T extends EntityFX> T setMotion(Vector3 motion) {
        this.motion = motion;
        return (T) this;
    }

    public <T extends EntityFX> T setPos(Vector3 pos) {
        this.pos = pos;
        this.prevPos = pos;
        return (T) this;
    }

    public <T extends EntityFX> T addPos(Vector3 pos) {
        return this.setPos(this.getPos().add(pos));
    }

    public Vector3 getPos() {
        return this.pos.copy();
    }

    public Vector3 getPrevPos() {
        return this.prevPos.copy();
    }

    public Vector3 getInterpolatedPos(float pTicks) {
        return RenderVectorUtil.interpolate(this.getPrevPos(), this.getPos(), pTicks);
    }

    public Vector3 getMotion() {
        return this.motion.copy();
    }

    public Vector3 getGravity() {
        return this.gravity.copy();
    }

    public <T extends EntityFX> T refresh(FXRefreshFunction<?> refreshFunction) {
        this.refreshFunction = refreshFunction;
        return (T) this;
    }

    public <T extends EntityFX> T motion(FXMotionFunction<?> motionFunction) {
        this.motionFunction = motionFunction;
        return (T) this;
    }

    public <T extends EntityFX> T position(FXPositionFunction<?> positionFunction) {
        this.positionFunction = positionFunction;
        return (T) this;
    }

    public <T extends EntityFX> T collision(FXCollisionFunction<?> collisionFunction) {
        this.collisionFunction = collisionFunction;
        return (T) this;
    }

    public <T extends EntityFX> T persistence(FXPersistenceFunction<?> persistenceFunction) {
        this.persistenceFunction = persistenceFunction;
        return (T) this;
    }

    public <T> T getOrCreateData(String str, Supplier<T> defaultProvider) {
        return (T) this.customData.computeIfAbsent(str, s -> defaultProvider.get());
    }

    @Nullable
    public <T> Optional<T> getData(String str) {
        return Optional.ofNullable((T) this.customData.get(str));
    }

    public void tick() {
        this.age++;

        if (this.canRemove()) {
            if (!this.refreshFunction.shouldRefresh(this)) {
                return;
            }
            this.age = 0;
            this.ageRefreshCount++;
        }

        Vector3 newMotion = this.motionFunction.updateMotion(this, this.getMotion().add(this.getGravity()));
        Vector3 collidedMotion = this.collisionFunction.collide(this, newMotion);
        if (collidedMotion.getY() != newMotion.getY()) { // On ground
            collidedMotion.multiply(0.5F);
        }
        this.motion = collidedMotion;
        Vector3 newPos = this.positionFunction.updatePosition(this, this.getPos(), this.getMotion());
        this.prevPos = this.pos.copy();
        this.pos = newPos;
    }

    protected boolean canEffectPersist(Entity viewEntity) {
        return this.persistenceFunction.canEffectPersist(this, viewEntity);
    }

    public boolean canRemove() {
        return this.age >= this.maxAge || this.removed;
    }

    public boolean isRemoved() {
        return this.removed;
    }

    public boolean isRemovalRequested() {
        return this.requestRemoval;
    }

    public void setRemoved() {
        this.removed = true;
        this.requestRemoval = false;
    }

    public void requestRemoval() {
        this.requestRemoval = true;
    }

    public void setActive() {
        this.removed = false;
        this.requestRemoval = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityFX that = (EntityFX) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
