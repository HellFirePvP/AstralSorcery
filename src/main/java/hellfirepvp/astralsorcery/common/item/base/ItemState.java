/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemState
 * Created by HellFirePvP
 * Date: 07.05.2016 / 14:31
 */
public class ItemState {

    private Map<String, ItemProperty<?>> properties = new HashMap<>();

    public ItemState defineProperty(ItemProperty<?> property, String key) {
        this.properties.put(key, property);
        return this;
    }

    public <T> T getPropertyValue(String key) {
        return (T) properties.get(key);
    }

    public static abstract class ItemProperty<T> {

        public abstract T getValue();

    }

    public static class ItemPropertyBoolean extends ItemProperty<Boolean> {

        private Boolean value;

        public ItemPropertyBoolean(Boolean value) {
            this.value = value;
        }

        @Override
        public Boolean getValue() {
            return value;
        }

    }

    public static class ItemPropertyInteger extends ItemProperty<Integer> {

        private Integer value;

        public ItemPropertyInteger(Integer value) {
            this.value = value;
        }

        @Override
        public Integer getValue() {
            return value;
        }

    }

    public static class ItemPropertyString extends ItemProperty<String> {

        private String value;

        public ItemPropertyString(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

    }

}
