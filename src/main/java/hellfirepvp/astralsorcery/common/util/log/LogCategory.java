/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.log;

import hellfirepvp.astralsorcery.common.util.Provider;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LogCategory
 * Created by HellFirePvP
 * Date: 06.04.2019 / 13:19
 */
public enum LogCategory {

    PERKS,
    STRUCTURE_MATCH,
    TREE_BEACON;

    public void info(Provider<String> message) {
        LogUtil.info(this, message);
    }

    public void warn(Provider<String> message) {
        LogUtil.warn(this, message);
    }

}
