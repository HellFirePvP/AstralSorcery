/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.lib;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.Window;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.shader.TransparencyChain;
import hellfirepvp.astralsorcery.client.shader.WrappedPostChain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ShaderProgramsAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ShaderProgramsAS {

    private static final List<WrappedPostChain> POST_CHAINS = new ArrayList<>();

    public static TransparencyChain TRANSPARENCY_COLOR = add(new TransparencyChain(AstralSorcery.key("shaders/post/transparency_color.json")));

    public static void init() {
        POST_CHAINS.forEach(WrappedPostChain::close);
        POST_CHAINS.forEach(ShaderProgramsAS::tryLoadChain);

        Window window = Minecraft.getInstance().getWindow();
        resize(window.getWidth(), window.getHeight());
    }

    public static void resize(int width, int height) {
        POST_CHAINS.forEach(chain -> chain.resize(width, height));
    }

    private static void tryLoadChain(WrappedPostChain wrappedChain) {
        try {
            wrappedChain.load();
        } catch (Exception exc) {
            String excMsg = "Failed to " + (exc instanceof JsonSyntaxException ? "parse" : "load") + " shader: " + wrappedChain.getChainKey();
            throw new LevelRenderer.TransparencyShaderException(excMsg, exc);
        }
    }

    public static <T> T add(T chain) {
        POST_CHAINS.add((WrappedPostChain) chain);
        return chain;
    }
}
