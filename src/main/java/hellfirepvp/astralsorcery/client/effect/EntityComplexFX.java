/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

import hellfirepvp.astralsorcery.client.effect.context.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.function.RefreshFunction;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.BufferBuilder;

import java.util.Objects;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityComplexFX
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:18
 */
public abstract class EntityComplexFX implements IComplexEffect {

    protected static final Random rand = new Random();
    private static long counter = 0;

    private final long id;
    protected int age = 0;
    protected int maxAge = 40;

    protected Vector3 pos;

    private RefreshFunction refreshFunction = RefreshFunction.DESPAWN;

    protected boolean removeRequested = false;
    private boolean flagRemoved = true;

    protected EntityComplexFX(Vector3 pos) {
        this.id = counter;
        counter++;
        this.pos = pos;
    }

    public final long getId() {
        return id;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public int getAge() {
        return age;
    }

    public Vector3 getPosition() {
        return pos;
    }

    public void setPosition(Vector3 pos) {
        this.pos = pos.clone();
    }

    public void refresh(RefreshFunction<?> refreshFunction) {
        this.refreshFunction = refreshFunction;
    }

    @Override
    public void tick() {
        this.age++;

        if (this.age >= this.maxAge && refreshFunction.shouldRefresh(this) && RenderingUtils.canEffectExist(this)) {
            this.age = 0;
        }
    }

    public abstract <T extends EntityComplexFX> void render(BatchRenderContext<T> ctx, BufferBuilder buf, float pTicks);

    public void resetLifespan() {
        this.age = 0;
    }

    @Override
    public boolean canRemove() {
        return this.age >= this.maxAge || removeRequested;
    }

    public void requestRemoval() {
        this.removeRequested = true;
    }

    public boolean isRemoved() {
        return this.flagRemoved;
    }

    public void flagAsRemoved() {
        this.flagRemoved = true;
        this.removeRequested = false;
    }

    public void clearRemoveFlag() {
        this.flagRemoved = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityComplexFX that = (EntityComplexFX) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
