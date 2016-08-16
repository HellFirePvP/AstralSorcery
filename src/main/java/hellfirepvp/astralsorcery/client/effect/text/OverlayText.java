package hellfirepvp.astralsorcery.client.effect.text;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.IComplexEffect;
import hellfirepvp.astralsorcery.client.util.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.BindableResource;
import hellfirepvp.astralsorcery.client.util.AssetLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HellFirePvP@Admin
 * Date: 08.07.2015 / 20:32
 * on SoulSorcery
 * OverlayText
 */
public final class OverlayText implements IComplexEffect {

    public OverlayFontRenderer fontRendererObj;

    private final String text;
    private int ticksUntilRemoval;
    private Color color = Color.WHITE;
    private float alpha = 0.5F;

    private OverlayTextPolicy policy = OverlayTextPolicy.Policies.NO_OP.getPolicy();

    private int animationTick = 0;

    public OverlayText(String text, int ticksUntilRemoval) {
        this.text = text;
        this.ticksUntilRemoval = ticksUntilRemoval;
        this.fontRendererObj = new OverlayFontRenderer(this.policy, ticksUntilRemoval);
    }

    public OverlayText(String text, int ticksUntilRemoval, OverlayTextProperties properties) {
        this(text, ticksUntilRemoval);
        if (properties != null) {
            if (properties.color != null) this.color = properties.color;
            if (properties.alpha != -1) this.alpha = properties.alpha;
            if (properties.policy != null) {
                this.policy = properties.policy;
                this.fontRendererObj = new OverlayFontRenderer(this.policy, ticksUntilRemoval);
            }
        }
    }

    public void doRender(ScaledResolution resolution, float partialTicks) {
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();
        int strWidth = fontRendererObj.getStringWidth(text.toUpperCase());
        int renderY = height / 5;
        int renderX = width / 2 - strWidth / 2;
        fontRendererObj.drawString(text.toUpperCase(), renderX, renderY, color, alpha, animationTick);
    }

    public static class OverlayTextProperties {

        private Color color;
        private float alpha = -1;
        private OverlayTextPolicy policy;

        public OverlayTextProperties() {
        }

        public OverlayTextProperties setColor(Color color) {
            this.color = color;
            return this;
        }

        public OverlayTextProperties setAlpha(float alpha) {
            this.alpha = alpha;
            return this;
        }

        public OverlayTextProperties setPolicy(OverlayTextPolicy policy) {
            this.policy = policy;
            return this;
        }

        public OverlayTextProperties setPolicy(OverlayTextPolicy.Policies policy) {
            this.policy = policy.getPolicy();
            return this;
        }
    }

    public void tick() {
        ticksUntilRemoval--;
        animationTick++;
    }

    @Override
    public boolean canRemove() {
        return ticksUntilRemoval <= 0;
    }

    @Override
    public void render(float pTicks) {
        doRender(new ScaledResolution(Minecraft.getMinecraft()), pTicks);
    }

    @Override
    public RenderTarget getRenderTarget() {
        return RenderTarget.OVERLAY_TEXT;
    }

    public static class OverlayFontRenderer {

        //Very much hardcoded stuff down there, but it works :P
        private static final int SPACE_CHAR_SIZE = 72;
        private static final BindableResource fontTexture = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "overlayfont");
        private static final float uPixelWidth = 13169F;
        //private static final float uPixelWidth = 2371F;
        private static final float vPixelHeight = 561F;
        //private static final float vPixelHeight = 158F;
        private static final Map<Character, RenderChar> loadedCharacters = new HashMap<Character, RenderChar>();
        public float zLevel = 10F;
        public float font_size_multiplicator = 0.05F;

        private OverlayTextPolicy policy;
        private int maxLiving;

        public OverlayFontRenderer() {
            this(null, 0);
        }

        public OverlayFontRenderer(OverlayTextPolicy policy, int maxLivingTicks) {
            this.policy = policy;
            this.maxLiving = maxLivingTicks;
        }

        public void drawString(String string, float x, float y, Color color, float alpha, int animationTick) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            boolean lightning = GL11.glGetBoolean(GL11.GL_LIGHTING);
            if (lightning) {
                GL11.glDisable(GL11.GL_LIGHTING);
            }
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            fontTexture.bind();
            char[] contents = string.toCharArray();
            for (int i = 0; i < contents.length; i++) {
                Character c = contents[i];
                if (c.equals(' ')) {
                    x += getCharWidth(c);
                    continue;
                }
                renderCharAt(string, c, x, y, animationTick, color, alpha, i);
                x += getCharWidth(c);
            }
            /*GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glDisable(GL11.GL_BLEND);
            if (lightning) {
                GL11.glEnable(GL11.GL_LIGHTING);
            }*/
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }

        private void renderCharAt(String full, Character c, float x, float y, int animationTick, Color color, float alpha, int index) {
            RenderChar rend = loadedCharacters.get(c);
            float aWidth = rend.def_width * font_size_multiplicator;
            float aHeight = rend.def_height * font_size_multiplicator;
            if (policy != null) {
                policy.onCharRender(this, full, c, index, rend, x, y, zLevel, color, alpha, aWidth, aHeight, animationTick, maxLiving);
            }
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
            GL11.glTexCoord2f(rend.lowerX / uPixelWidth, rend.lowerY / vPixelHeight);
            GL11.glVertex3f(x, y, zLevel);
            GL11.glTexCoord2f(rend.lowerX / uPixelWidth, rend.higherY / vPixelHeight);
            GL11.glVertex3f(x, y + aHeight, zLevel);
            GL11.glTexCoord2f(rend.higherX / uPixelWidth, rend.lowerY / vPixelHeight);
            GL11.glVertex3f(x + aWidth, y, zLevel);
            GL11.glTexCoord2f(rend.higherX / uPixelWidth, rend.higherY / vPixelHeight);
            GL11.glVertex3f(x + aWidth, y + aHeight, zLevel);
            GL11.glEnd();
        }

        public int getStringWidth(String string) {
            int width = 0;
            for (Character c : string.toCharArray()) {
                width += getCharWidth(c);
            }
            return width;
        }

        public float getCharWidth(Character c) {
            if (c.equals(' ')) return SPACE_CHAR_SIZE * font_size_multiplicator;
            RenderChar rend = loadedCharacters.get(c);
            if (rend == null) throw new RuntimeException("Using OverlayFontRenderer with invalid chars! (Character: " + String.valueOf(c) + ")");
            return (rend.higherX - rend.lowerX) * font_size_multiplicator;
        }

        static {
            loadedCharacters.clear();

            loadedCharacters.put('!',  new RenderChar(32, 42));
            loadedCharacters.put('"',  new RenderChar(77, 62));
            loadedCharacters.put('#',  new RenderChar(151, 122));
            loadedCharacters.put('$',  new RenderChar(281, 154));
            loadedCharacters.put('%',  new RenderChar(438, 175));
            loadedCharacters.put('&',  new RenderChar(615, 182));
            loadedCharacters.put('\'', new RenderChar(803, 34));
            loadedCharacters.put('(',  new RenderChar(839, 120));
            loadedCharacters.put(')',  new RenderChar(959, 120));
            loadedCharacters.put('*',  new RenderChar(1079, 115));
            loadedCharacters.put('+',  new RenderChar(1199, 125));
            loadedCharacters.put(',',  new RenderChar(1326, 42));
            loadedCharacters.put('-',  new RenderChar(1362, 125));
            loadedCharacters.put('.',  new RenderChar(1482, 32));
            loadedCharacters.put('/',  new RenderChar(1513, 180));
            loadedCharacters.put('0',  new RenderChar(1692, 153));
            loadedCharacters.put('1',  new RenderChar(1838, 63));
            loadedCharacters.put('2',  new RenderChar(1901, 161));
            loadedCharacters.put('3',  new RenderChar(2063, 179));
            loadedCharacters.put('4',  new RenderChar(2241, 218));
            loadedCharacters.put('5',  new RenderChar(2458, 184));
            loadedCharacters.put('6',  new RenderChar(2642, 190));
            loadedCharacters.put('7',  new RenderChar(2831, 197));
            loadedCharacters.put('8',  new RenderChar(3031, 187));
            loadedCharacters.put('9',  new RenderChar(3225, 176));
            loadedCharacters.put(':',  new RenderChar(3411, 33));
            loadedCharacters.put(';',  new RenderChar(3433, 43));
            loadedCharacters.put('<',  new RenderChar(3478, 181));
            loadedCharacters.put('=',  new RenderChar(3650, 157));
            loadedCharacters.put('>',  new RenderChar(3790, 183));
            loadedCharacters.put('?',  new RenderChar(3972, 138));
            loadedCharacters.put('@',  new RenderChar(4112, 179));
            loadedCharacters.put('A',  new RenderChar(4293, 220));
            loadedCharacters.put('B',  new RenderChar(4513, 201));
            loadedCharacters.put('C',  new RenderChar(4714, 280));
            loadedCharacters.put('D',  new RenderChar(4994, 200));
            loadedCharacters.put('E',  new RenderChar(5197, 290));
            loadedCharacters.put('F',  new RenderChar(5487, 292));
            loadedCharacters.put('G',  new RenderChar(5779, 171));
            loadedCharacters.put('H',  new RenderChar(5953, 216));
            loadedCharacters.put('I',  new RenderChar(6170, 48));
            loadedCharacters.put('J',  new RenderChar(6215, 193));
            loadedCharacters.put('K',  new RenderChar(6407, 152));
            loadedCharacters.put('L',  new RenderChar(6561, 257));
            loadedCharacters.put('M',  new RenderChar(6821, 186));
            loadedCharacters.put('N',  new RenderChar(7010, 156));
            loadedCharacters.put('O',  new RenderChar(7157, 158));
            loadedCharacters.put('P',  new RenderChar(7306, 148));
            loadedCharacters.put('Q',  new RenderChar(7454, 160));
            loadedCharacters.put('R',  new RenderChar(7612, 188));
            loadedCharacters.put('S',  new RenderChar(7798, 189));
            loadedCharacters.put('T',  new RenderChar(7988, 286));
            loadedCharacters.put('U',  new RenderChar(8274, 200));
            loadedCharacters.put('V',  new RenderChar(8474, 163));
            loadedCharacters.put('W',  new RenderChar(8637, 261));
            loadedCharacters.put('X',  new RenderChar(8899, 323));
            loadedCharacters.put('Y',  new RenderChar(9222, 151));
            loadedCharacters.put('Z',  new RenderChar(9370, 329));
            loadedCharacters.put('[',  new RenderChar(9699, 92));
            loadedCharacters.put('\\', new RenderChar(9787, 183));
            loadedCharacters.put(']',  new RenderChar(9969, 85));
            loadedCharacters.put('^',  new RenderChar(10053, 80));
            loadedCharacters.put('_',  new RenderChar(10127, 257));
            loadedCharacters.put('`',  new RenderChar(10384, 69));
            loadedCharacters.put('a',  new RenderChar(10456, 66));
            loadedCharacters.put('b',  new RenderChar(10521, 65));
            loadedCharacters.put('c',  new RenderChar(10587, 46));
            loadedCharacters.put('d',  new RenderChar(12841, 73));
            loadedCharacters.put('e',  new RenderChar(10799, 51));
            loadedCharacters.put('f',  new RenderChar(10851, 175));
            loadedCharacters.put('g',  new RenderChar(11026, 150));
            loadedCharacters.put('h',  new RenderChar(11176, 74));
            loadedCharacters.put('i',  new RenderChar(11250, 35));
            loadedCharacters.put('j',  new RenderChar(11286, 152));
            loadedCharacters.put('k',  new RenderChar(11436, 103));
            loadedCharacters.put('l',  new RenderChar(11542, 34));
            loadedCharacters.put('m',  new RenderChar(11577, 98));
            loadedCharacters.put('n',  new RenderChar(11675, 64));
            loadedCharacters.put('o',  new RenderChar(11742, 64));
            loadedCharacters.put('p',  new RenderChar(11805, 73));
            loadedCharacters.put('q',  new RenderChar(11878, 65));
            loadedCharacters.put('r',  new RenderChar(11942, 67));
            loadedCharacters.put('s',  new RenderChar(12008, 80));
            loadedCharacters.put('t',  new RenderChar(12085, 118));
            loadedCharacters.put('u',  new RenderChar(12203, 67));
            loadedCharacters.put('v',  new RenderChar(12269, 74));
            loadedCharacters.put('w',  new RenderChar(12344, 107));
            loadedCharacters.put('x',  new RenderChar(12450, 74));
            loadedCharacters.put('y',  new RenderChar(12525, 138));
            loadedCharacters.put('z',  new RenderChar(12664, 80));
            loadedCharacters.put('{',  new RenderChar(12743, 100));
            loadedCharacters.put('}',  new RenderChar(12912, 93));
            loadedCharacters.put('~',  new RenderChar(13018, 144));
            //Old one.
            /*loadedCharacters.put('A', new RenderChar(0, 84));
            loadedCharacters.put('B', new RenderChar(114, 76));
            loadedCharacters.put('C', new RenderChar(224, 107));
            loadedCharacters.put('D', new RenderChar(350, 76));
            loadedCharacters.put('E', new RenderChar(430, 113));
            loadedCharacters.put('F', new RenderChar(560, 114));
            loadedCharacters.put('G', new RenderChar(685, 64));
            loadedCharacters.put('H', new RenderChar(754, 85));
            loadedCharacters.put('I', new RenderChar(844, 13));
            loadedCharacters.put('J', new RenderChar(880, 74));
            loadedCharacters.put('K', new RenderChar(960, 58));
            loadedCharacters.put('L', new RenderChar(1046, 100));
            loadedCharacters.put('M', new RenderChar(1180, 71));
            loadedCharacters.put('N', new RenderChar(1256, 55));
            loadedCharacters.put('O', new RenderChar(1318, 54));
            loadedCharacters.put('P', new RenderChar(1378, 47));
            loadedCharacters.put('Q', new RenderChar(1431, 61));
            loadedCharacters.put('R', new RenderChar(1496, 72));
            loadedCharacters.put('S', new RenderChar(1577, 71));
            loadedCharacters.put('T', new RenderChar(1652, 113));
            loadedCharacters.put('U', new RenderChar(1782, 77));
            loadedCharacters.put('V', new RenderChar(1862, 60));
            loadedCharacters.put('W', new RenderChar(1928, 101));
            loadedCharacters.put('X', new RenderChar(2032, 125));
            loadedCharacters.put('Y', new RenderChar(2158, 58));
            loadedCharacters.put('Z', new RenderChar(2242, 129));*/
        }

        public static class RenderChar {

            private int lowerX, lowerY, higherX, higherY;
            private int def_width, def_height;

            public RenderChar(int lowerX, int width) {
                this(lowerX, 0, lowerX + width, (int) vPixelHeight);
            }

            public RenderChar(int lowerX, int lowerY, int higherX, int higherY) {
                this.lowerX = lowerX;
                this.lowerY = lowerY;
                this.higherX = higherX;
                this.higherY = higherY;
                this.def_width = higherX - lowerX;
                this.def_height = higherY - lowerY;
            }
        }
    }
}
