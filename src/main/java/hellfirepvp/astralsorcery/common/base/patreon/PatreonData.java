/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonData
 * Created by HellFirePvP
 * Date: 30.08.2019 / 23:25
 */
//Class deserialized into via GSON
public class PatreonData {

    private List<EffectEntry> effectList = Lists.newArrayList();

    public List<EffectEntry> getEffectList() {
        return Collections.unmodifiableList(effectList);
    }

    public static class EffectEntry {

        private String uuid;
        private String effectClass;
        private List<String> parameters = Lists.newArrayList();

        public String getUuid() {
            return uuid;
        }

        public String getEffectClass() {
            return effectClass;
        }

        public List<String> getParameters() {
            return parameters;
        }

    }

}
