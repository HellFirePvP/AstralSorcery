/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import hellfirepvp.astralsorcery.client.effect.handler.EffectHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContextRenderHandler
 * Created by HellFirePvP
 * Date: 30.05.2019 / 13:40
 */
public class ContextRenderHandler {

    private static ContextRenderHandler INSTANCE = new ContextRenderHandler();

    private ContextRenderHandler() {}

    public static ContextRenderHandler getInstance() {
        return INSTANCE;
    }

    public void attachEventListeners(IEventBus bus) {
        bus.addListener(this::onDebugText);
        bus.addListener(EffectHandler.getInstance()::render);
    }

    private void onDebugText(RenderGameOverlayEvent.Text event) {
        if(Minecraft.getInstance().gameSettings.showDebugInfo) {
            event.getLeft().add("");
            //event.getLeft().add(TextFormatting.BLUE + "[AstralSorcery]" + TextFormatting.RESET + " Use Local persistent data: " + PersistentDataManager.INSTANCE.usePersistent());
            event.getLeft().add(TextFormatting.BLUE + "[AstralSorcery]" + TextFormatting.RESET + " EffectHandler:");
            event.getLeft().add(TextFormatting.BLUE + "[AstralSorcery]" + TextFormatting.RESET + " > Complex effects: " + EffectHandler.getDebugEffectCount());
        }
    }

}
