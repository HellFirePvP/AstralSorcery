/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event.effect;

import hellfirepvp.astralsorcery.client.effect.handler.EffectHandler;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectRenderEventHandler
 * Created by HellFirePvP
 * Date: 30.05.2019 / 13:40
 */
public class EffectRenderEventHandler {

    private static final EffectRenderEventHandler INSTANCE = new EffectRenderEventHandler();

    private EffectRenderEventHandler() {}

    public static EffectRenderEventHandler getInstance() {
        return INSTANCE;
    }

    public void attachEventListeners(IEventBus bus) {
        bus.addListener(this::onDebugText);
        bus.addListener(ClientMiscEventHandler::onRender);
        bus.addListener(EventPriority.LOW, GatewayUIRenderHandler.getInstance()::render);
    }

    public void attachTickListeners(Consumer<ITickHandler> registrar) {
        registrar.accept(GatewayUIRenderHandler.getInstance());
    }

    private void onDebugText(RenderGameOverlayEvent.Text event) {
        if (Minecraft.getInstance().gameSettings.showDebugInfo) {
            event.getLeft().add("");
            //event.getLeft().add(TextFormatting.BLUE + "[AstralSorcery]" + TextFormatting.RESET + " Use Local persistent data: " + PersistentDataManager.INSTANCE.usePersistent());
            event.getLeft().add(TextFormatting.BLUE + "[AstralSorcery]" + TextFormatting.RESET + " EffectHandler:");
            event.getLeft().add(TextFormatting.BLUE + "[AstralSorcery]" + TextFormatting.RESET + " > Complex effects: " + EffectHandler.getInstance().getEffectCount());
        }
    }

}
