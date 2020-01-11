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
 * Class: WordGeneratorChinese
 * Created by HellFirePvP
 * Date: 30.09.2018 / 14:15
 */
//Credit to yuanjie000
public class WordGeneratorChinese extends RandomWordGenerator {

    private static String[] a = { "乾", "震", "坎", "艮", "坤", "巽", "离", "兑"};
    private static String[] b = { "角", "亢", "氐", "房", "心", "尾", "箕", "斗", "牛", "女", "虚", "危", "室", "壁", "奎", "娄", "胃", "昴", "毕", "觜", "参", "井", "鬼", "柳", "星", "张", "翼", "轸" };
    private static String suffix = "座";

    @Override
    public String generateWord(long seed, int length) {
        Random r = new Random(seed);
        StringBuilder word = new StringBuilder();
        word.append(a[r.nextInt(a.length)]);
        word.append(b[r.nextInt(b.length)]);
        word.append(suffix);
        return word.toString();
    }

}
