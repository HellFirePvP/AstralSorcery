package hellfirepvp.astralsorcery.client.util.image;

import org.apache.logging.log4j.util.TriConsumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ImageTemplates
 * Created by HellFirePvP
 * Date: 06.01.2021 / 09:36
 */
public class ImageTemplates {

    public static final int TR_0 = 0xFFFFFFFF;
    public static final int TR_1 = 0xC0FFFFFF;
    public static final int TR_2 = 0x80FFFFFF;
    public static final int TR_3 = 0x40FFFFFF;

    public static ImageTemplate getLargeStar() {
        return new ImageTemplate.Quad(7, 7) {
            @Override
            public void place(TriConsumer<Integer, Integer, Integer> setColor) {
                setColor.accept(3, 0, TR_3);
                setColor.accept(0, 3, TR_3);
                setColor.accept(6, 3, TR_3);
                setColor.accept(3, 6, TR_3);

                setColor.accept(2, 2, TR_3);
                setColor.accept(4, 2, TR_3);
                setColor.accept(2, 4, TR_3);
                setColor.accept(4, 4, TR_3);

                setColor.accept(3, 1, TR_2);
                setColor.accept(1, 3, TR_2);
                setColor.accept(3, 5, TR_2);
                setColor.accept(5, 3, TR_2);

                setColor.accept(3, 2, TR_1);
                setColor.accept(2, 3, TR_1);
                setColor.accept(3, 4, TR_1);
                setColor.accept(4, 3, TR_1);

                setColor.accept(3, 3, TR_0);
            }
        };
    }

    public static ImageTemplate getSmallStar() {
        return new ImageTemplate.Quad(3, 3) {
            @Override
            public void place(TriConsumer<Integer, Integer, Integer> setColor) {
                setColor.accept(1, 0, TR_3);
                setColor.accept(0, 1, TR_3);
                setColor.accept(1, 2, TR_3);
                setColor.accept(2, 1, TR_3);

                setColor.accept(1, 1, TR_1);
            }
        };
    }

    public static ImageTemplate getDot(int color) {
        return new ImageTemplate.Quad(1, 1) {
            @Override
            public void place(TriConsumer<Integer, Integer, Integer> setColor) {
                setColor.accept(0, 0, color);
            }
        };
    }
}
