/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

import hellfirepvp.astralsorcery.client.effect.function.RefreshFunction;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityComplexFX
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:18
 */
public abstract class EntityComplexFX {

    protected static final Random rand = new Random();
    private static long counter = 0;

    private final long id;
    protected int age = 0;
    protected int maxAge = 40;
    protected int ageRefreshCount = 0;

    protected Vector3 pos;

    private RefreshFunction refreshFunction = RefreshFunction.DESPAWN;
    private Map<String, Object> customData = new HashMap<>();

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

    public <T extends EntityComplexFX> T setMaxAge(int maxAge) {
        this.maxAge = maxAge;
        return (T) this;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public int getAge() {
        return age;
    }

    public <T extends EntityComplexFX> T move(Vector3 change) {
        this.setPosition(this.getPosition().add(change));
        return (T) this;
    }

    public Vector3 getPosition() {
        return pos.clone();
    }

    public <T extends EntityComplexFX> T setPosition(Vector3 pos) {
        this.pos = pos.clone();
        return (T) this;
    }

    public <T extends EntityComplexFX> T addPosition(Vector3 offset) {
        this.pos.add(offset);
        return (T) this;
    }

    public <T extends EntityComplexFX> T  refresh(RefreshFunction<?> refreshFunction) {
        this.refreshFunction = refreshFunction;
        return (T) this;
    }

    public <T> T getOrCreateData(String str, Supplier<T> defaultProvider) {
        return (T) this.customData.computeIfAbsent(str, s -> defaultProvider.get());
    }

    @Nullable
    public <T> T getData(String str) {
        return (T) this.customData.get(str);
    }

    public void tick() {
        this.age++;

        if (this.canRemove() && refreshFunction.shouldRefresh(this) && RenderingUtils.canEffectExist(this)) {
            this.resetLifespan();
            this.ageRefreshCount++;
        }
    }

    public void resetLifespan() {
        this.age = 0;
    }

    public int getAgeRefreshCount() {
        return ageRefreshCount;
    }

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

    public void setActive() {
        this.flagRemoved = false;
        this.removeRequested = false;
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
