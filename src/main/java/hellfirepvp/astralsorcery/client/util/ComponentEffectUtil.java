/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ComponentEffectUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ComponentEffectUtil {

    public static Component randomObfuscatedComponent(long effectTick, Component... options) {
        if (options.length == 0) {
            return Component.empty();
        }

        int maxLength = 0;
        for (Component cmp : options) {
            maxLength = Math.max(maxLength, cmp.getString().length());
        }

        int readableTicks = 15;
        int ticksPerLetter = 1;
        int waitTicks = 0;

        int obfuscatingTicks = maxLength * ticksPerLetter;
        int deobfuscatingTicks = maxLength * ticksPerLetter;
        int cycleLength = readableTicks + obfuscatingTicks + waitTicks + deobfuscatingTicks;

        int totalCycles = options.length;
        int currentCycle = (int) ((effectTick / cycleLength) % totalCycles);
        int cycleTick = (int) (effectTick % cycleLength);

        Component current = options[currentCycle];

        if (cycleTick < readableTicks) {
            return current;
        }

        cycleTick -= readableTicks;

        if (cycleTick < obfuscatingTicks) {
            String currentStr = current.getString();
            int lettersObfuscated = Math.min(currentStr.length(), (cycleTick / ticksPerLetter) + 1);
            return partialComponent(current, currentStr.length() - lettersObfuscated, currentStr.length());
        }

        cycleTick -= obfuscatingTicks;

        if (cycleTick < waitTicks) {
            Component wrapper = Component.empty().append(current).withStyle(ChatFormatting.RESET);
            return Component.empty().append(wrapper).withStyle(ChatFormatting.OBFUSCATED);
        }

        cycleTick -= waitTicks;

        Component next = options[(currentCycle + 1) % totalCycles];
        String nextStr = next.getString();
        int readablePart = Math.min(nextStr.length(), (cycleTick / ticksPerLetter) + 1);
        return partialComponent(next, readablePart, nextStr.length());
    }

    private static Component partialComponent(Component text, int obfuscateStart, int obfuscateEnd) {
        Component result = Component.empty();

        String textStr = text.getString();
        for (int i = 0; i < textStr.length(); i++) {
            MutableComponent letter = Component.literal(String.valueOf(textStr.charAt(i)));
            if (i >= obfuscateStart && i < obfuscateEnd) {
                letter = letter.withStyle(ChatFormatting.OBFUSCATED);
            } else {
                letter = letter.withStyle(text.getStyle());
            }
            result = result.copy().append(letter);
        }

        return result;
    }

    public static Component applyStyle(Component cmp, Style style) {
        if (cmp instanceof MutableComponent mutable) {
            mutable.setStyle(style.applyTo(cmp.getStyle()));
        }
        return cmp;
    }

    public static Component applyStyleNested(Component cmp, Style style) {
        applyStyle(cmp, style);
        cmp.getSiblings().forEach(child -> applyStyleNested(child, style));
        return cmp;
    }
}
