/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ReadWriteLockable
 * Created by HellFirePvP
 * Date: 20.12.2020 / 20:34
 */
public interface ReadWriteLockable {

    ReadWriteLock getLock();

    default <T> T write(Supplier<T> fn) {
        return this.lock(this.getLock()::writeLock, fn);
    }

    default void write(Runnable run) {
        this.lock(this.getLock()::writeLock, MiscUtils.nullSupplier(run));
    }

    default <T> T read(Supplier<T> fn) {
        return this.lock(this.getLock()::readLock, fn);
    }

    default void read(Runnable run) {
        this.lock(this.getLock()::readLock, MiscUtils.nullSupplier(run));
    }

    default <T> T lock(Supplier<Lock> lock, Supplier<T> fn) {
        lock.get().lock();
        try {
            return fn.get();
        } finally {
            lock.get().unlock();
        }
    }
}
