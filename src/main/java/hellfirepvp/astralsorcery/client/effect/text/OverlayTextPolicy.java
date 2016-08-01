package hellfirepvp.astralsorcery.client.effect.text;

import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * HellFirePvP@Admin
 * Date: 08.07.2015 / 21:56
 * on SoulSorcery
 * OverlayTextPolicy
 */
public abstract class OverlayTextPolicy {

    public abstract void onCharRender(OverlayText.OverlayFontRenderer overlayFontRenderer, String toRenderFully, Character c, int index, OverlayText.OverlayFontRenderer.RenderChar render, float x, float y, float z, Color color, float alpha, float width, float height, int animationTick, int maxLivingTicks);

    public static enum Policies {

        NO_OP(new SilentPolicy()),
        WRITING(new WritingPolicy());
        //@Deprecated
        //WRITING_WITH_GLOWEFFECT(new FancyPolicy());

        private OverlayTextPolicy policy;

        private Policies(OverlayTextPolicy policy) {
            this.policy = policy;
        }

        public OverlayTextPolicy getPolicy() {
            return policy;
        }
    }

    //Does nothing except color setting what is done by the policies and not internally to prevent some stuff from going on...
    static class SilentPolicy extends OverlayTextPolicy {

        @Override
        public void onCharRender(OverlayText.OverlayFontRenderer overlayFontRenderer, String toRenderFully, Character c, int index, OverlayText.OverlayFontRenderer.RenderChar render, float x, float y, float z, Color color, float alpha, float width, float height, int animationTick, int maxLivingTicks) {
            GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        }
    }

    //Some kind of writing effect...
    static class WritingPolicy extends OverlayTextPolicy {

        public static float SHOW_ALL_CAP = 0.3F; //relative duration (to max. living of the message) until every content of the text is shown.

        @Override
        public void onCharRender(OverlayText.OverlayFontRenderer overlayFontRenderer, String toRenderFully, Character c, int index, OverlayText.OverlayFontRenderer.RenderChar render, float x, float y, float z, Color color, float alpha, float width, float height, int animationTick, int maxLivingTicks) {
            float posShowAtPercent = (((float) (index + 1)) / ((float) toRenderFully.length())) * SHOW_ALL_CAP;
            float currentRenderPercent = ((float) animationTick) / ((float) maxLivingTicks);
            alpha = evaluateAlpha(toRenderFully, index, posShowAtPercent, currentRenderPercent, alpha);
            GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        }

        private float evaluateAlpha(String toRenderFully, int index, float posShowAtPercent, float currentRenderPercent, float alpha) {
            if (currentRenderPercent > 0.9F) return evaluteEndAlpha(currentRenderPercent, alpha);

            if (currentRenderPercent < posShowAtPercent) return 0F;
            float diff = currentRenderPercent - posShowAtPercent;
            if (diff > 0.2F) return alpha;
            return alpha * (diff / 0.2F);
        }

        //currentRenderPercent > 90%
        private float evaluteEndAlpha(float currentRenderPercent, float alpha) {
            return alpha * ((1F - currentRenderPercent) * 10);
        }
    }

    /*static class FancyPolicy extends OverlayTextPolicy {

        @Override
        public void onCharRender(OverlayText.OverlayFontRenderer overlayFontRenderer, String toRenderFully, Character c, int index, OverlayText.OverlayFontRenderer.RenderChar render, float x, float y, float z, Color color, float alpha, float width, float height, int animationTick, int maxLivingTicks) {
            //Draw the text normally.
            OverlayTextPolicy policy = Policies.WRITING.getPolicy();
            overlayFontRenderer.zLevel = 20F;
            policy.onCharRender(overlayFontRenderer, toRenderFully, c, index, render, x, y, z, color, alpha, width, height, animationTick, maxLivingTicks);

            //Draw the effects...
        }
    }*/

}
