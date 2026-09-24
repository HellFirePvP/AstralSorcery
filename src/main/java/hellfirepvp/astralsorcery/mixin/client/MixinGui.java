/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin.client;

import hellfirepvp.astralsorcery.client.helper.RenderAstrolabeOverlay;
import hellfirepvp.astralsorcery.common.item.AstrolabeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MixinGui
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(Gui.class)
public class MixinGui {

    @Inject(method = "renderSpyglassOverlay", at = @At("HEAD"), cancellable = true)
    public void renderAstrolabeOverlay(GuiGraphics guiGraphics, float scopeScale, CallbackInfo ci) {
        //The player exists and is scoping, that's how renderSpyglassOverlay is called. No more safety needed
        if (AstrolabeItem.isUsingAstrolabe(Minecraft.getInstance().player)) {
            RenderAstrolabeOverlay.render(guiGraphics, scopeScale);
            ci.cancel();
        }
    }

}
