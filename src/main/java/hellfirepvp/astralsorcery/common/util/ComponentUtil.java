/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ComponentUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ComponentUtil {

    private static final Pattern TAG_PATTERN = Pattern.compile(
            "<(/?)(color|bold|italic)(?:=\"?(#[0-9A-Fa-f]{6})\"?)?>");

    public static Component parseFormats(String str) {
        MutableComponent result = Component.literal("");

        Deque<Style> styleStack = new ArrayDeque<>();
        styleStack.push(Style.EMPTY);

        Matcher matcher = TAG_PATTERN.matcher(str);
        int lastEnd = 0;
        while (matcher.find()) {
            String text = str.substring(lastEnd, matcher.start());
            if (!text.isEmpty()) {
                result.append(Component.literal(text).setStyle(styleStack.peek()));
            }

            boolean closingTag = !matcher.group(1).isEmpty();
            if (closingTag) {
                if (styleStack.size() > 1) {
                    styleStack.pop();
                }
            } else {
                styleStack.push(applyTag(matcher.group(2), matcher.group(3), styleStack.peek()));
            }

            lastEnd = matcher.end();
        }

        String after = str.substring(lastEnd);
        if (!after.isEmpty()) {
            result.append(Component.literal(after).setStyle(styleStack.peek()));
        }
        return result;
    }

    private static Style applyTag(String tagName, String colorArg, Style baseStyle) {
        return switch (tagName) {
            case "color" -> baseStyle.withColor(TextColor.parseColor(colorArg).getOrThrow(IllegalArgumentException::new));
            case "bold" -> baseStyle.withBold(true);
            case "italic" -> baseStyle.withItalic(true);
            default -> baseStyle;
        };
    }

}
