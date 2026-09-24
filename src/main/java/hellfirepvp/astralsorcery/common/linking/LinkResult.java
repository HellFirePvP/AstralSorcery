/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.linking;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LinkResult
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LinkResult {

    private final boolean success;
    private final Component errorMessage;

    private LinkResult(boolean success, Component errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static LinkResult success() {
        return new LinkResult(true, null);
    }

    public static LinkResult failure(Component errorMessage) {
        return new LinkResult(false, errorMessage);
    }

    public static LinkResult failureInvalid() {
        return LinkResult.failure(Component.translatable("message.astralsorcery.linking.failure.invalid").withStyle(ChatFormatting.RED));
    }

    public boolean isSuccess() {
        return this.success;
    }

    public Optional<Component> getErrorMessage() {
        return Optional.ofNullable(this.errorMessage);
    }
}
