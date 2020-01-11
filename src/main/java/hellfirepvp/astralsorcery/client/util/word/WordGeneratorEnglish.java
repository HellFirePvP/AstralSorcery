/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.word;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WordGeneratorEnglish
 * Created by HellFirePvP
 * Date: 29.09.2018 / 10:59
 */
public class WordGeneratorEnglish extends RandomWordGenerator {

    private static String[] vows = new String[] { "a", "e", "i", "o", "u" };
    private static String[] cons = new String[] { "b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "ph", "qu", "r", "s", "t", "v", "w", "x", "y", "z", "tt", "ch", "sh" };

    @Override
    public String generateWord(long seed, int length) {
        Random sRand = new Random(seed);
        boolean toggleVowel = sRand.nextFloat() > 0.8F;
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < length; i++) {
            String[] sel = toggleVowel ? vows : cons;
            word.append(sel[sRand.nextInt(sel.length)]);
            toggleVowel = !toggleVowel;
        }
        return word.toString();
    }
}
