package hellfirepvp.astralsorcery.client.util.image;

import org.apache.logging.log4j.util.TriConsumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ImageTemplate
 * Created by HellFirePvP
 * Date: 06.01.2021 / 09:32
 */
public interface ImageTemplate {

    int getWidth();

    int getHeight();

    void place(TriConsumer<Integer, Integer, Integer> setColor);

    public static abstract class Quad implements ImageTemplate {

        private final int width, height;

        protected Quad(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public int getWidth() {
            return this.width;
        }

        @Override
        public int getHeight() {
            return this.height;
        }
    }
}
