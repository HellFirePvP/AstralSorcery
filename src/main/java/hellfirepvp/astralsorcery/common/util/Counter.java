/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: Counter
 * Created by HellFirePvP
 * Date: 05.04.2017 / 21:30
 */
public class Counter {

    public int value;

    public Counter(int value) {
        this.value = value;
    }

    public void decrement() {
        value--;
    }

    public void increment() {
        value++;
    }

}
