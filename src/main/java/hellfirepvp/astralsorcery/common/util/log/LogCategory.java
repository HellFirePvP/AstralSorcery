/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.log;

import hellfirepvp.astralsorcery.common.data.config.entry.LogConfig;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LogCategory
 * Created by HellFirePvP
 * Date: 06.04.2019 / 13:19
 */
public enum LogCategory {

    PERKS,
    UNINTENDED_CHUNK_LOADING,
    STRUCTURE_MATCH,
    GATEWAY_CACHE;

    public boolean isEnabled() {
        return LogConfig.CONFIG.isLoggingEnabled(this);
    }

    public void info(Supplier<String> message) {
        LogUtil.info(this, message);
    }

    public void warn(Supplier<String> message) {
        LogUtil.warn(this, message);
    }

}
