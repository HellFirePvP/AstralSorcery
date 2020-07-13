/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.input.KeyBindingWrapper;
import hellfirepvp.astralsorcery.client.input.KeyDisablePerkAbilities;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static hellfirepvp.astralsorcery.client.lib.KeyBindingsAS.DISABLE_PERK_ABILITIES;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryKeyBindings
 * Created by HellFirePvP
 * Date: 13.05.2020 / 18:43
 */
public class RegistryKeyBindings {

    private static Set<KeyBindingWrapper> watchedKeyBindings = new HashSet<>();
    private static Set<KeyBindingWrapper> bindingsPressed = new HashSet<>();

    public static void init() {
        DISABLE_PERK_ABILITIES = register("disable_perk_abilities", GLFW.GLFW_KEY_V, KeyDisablePerkAbilities::new);

        MinecraftForge.EVENT_BUS.addListener(RegistryKeyBindings::onKeyInput);
    }

    private static KeyBindingWrapper register(String name, int glfwKey) {
        return register(name, glfwKey, keyBinding -> new KeyBindingWrapper(keyBinding) {});
    }

    private static KeyBindingWrapper register(String name, int glfwKey, Function<KeyBinding, KeyBindingWrapper> wrapperCreator) {
        KeyBinding keyBinding = new KeyBinding(String.format("key.%s.%s", AstralSorcery.MODID, name),
                KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, glfwKey, AstralSorcery.NAME);
        ClientRegistry.registerKeyBinding(keyBinding);
        KeyBindingWrapper wrapper = wrapperCreator.apply(keyBinding);
        watchedKeyBindings.add(wrapper);
        return wrapper;
    }

    private static void onKeyInput(InputEvent.KeyInputEvent event) {
        InputMappings.Input input = InputMappings.getInputByCode(event.getKey(), event.getScanCode());
        KeyBindingWrapper eventKey = MiscUtils.iterativeSearch(watchedKeyBindings, keyBinding -> keyBinding.getKeyBinding().getKey().equals(input));
        if (eventKey != null) {
            boolean isPressed = eventKey.getKeyBinding().isKeyDown();
            boolean wasPressed = bindingsPressed.contains(eventKey);
            if (isPressed != wasPressed) {
                if (isPressed) {
                    bindingsPressed.add(eventKey);
                    eventKey.onKeyDown();
                } else {
                    bindingsPressed.remove(eventKey);
                    eventKey.onKeyUp();
                }
            }
        }
    }
}
