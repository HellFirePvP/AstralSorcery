package hellfirepvp.astralsorcery.client.effect.text;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.IComplexEffect;
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
        private static final BindableResource fontTexture = AssetLoader.loadTexture(AssetLoader.TextureLocation.MISC, "overlayfont");
        private static final float uPixelWidth = 2371F;
        private static final float vPixelHeight = 158F;
        private static final Map<Character, RenderChar> loadedCharacters = new HashMap<Character, RenderChar>();
        public float zLevel = 10F;
        public float font_size_multiplicator = 0.25F;

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
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glDisable(GL11.GL_BLEND);
            if (lightning) {
                GL11.glEnable(GL11.GL_LIGHTING);
            }
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
            if (rend == null) throw new RuntimeException("Using OverlayFontRenderer with invalid chars!");
            return (rend.higherX - rend.lowerX) * font_size_multiplicator;
        }

        static {
            loadedCharacters.clear();

            loadedCharacters.put('A', new RenderChar(0, 84));
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
            loadedCharacters.put('Z', new RenderChar(2242, 129));
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
