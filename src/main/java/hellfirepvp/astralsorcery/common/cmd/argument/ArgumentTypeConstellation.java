/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.cmd.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import hellfirepvp.astralsorcery.common.constellation.*;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ArgumentTypeConstellation
 * Created by HellFirePvP
 * Date: 21.07.2019 / 20:21
 */
public class ArgumentTypeConstellation implements ArgumentType<IConstellation> {

    public static final SimpleCommandExceptionType CONSTELLATION_NOT_FOUND =
            new SimpleCommandExceptionType(new TranslationTextComponent("astralsorcery.command.argument.constellation.notfound"));

    private final Predicate<IConstellation> filter;

    private ArgumentTypeConstellation(Predicate<IConstellation> filter) {
        this.filter = filter;
    }

    @Override
    public IConstellation parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation name = ResourceLocation.read(reader);
        for (IConstellation cst : ConstellationRegistry.getAllConstellations()) {
            if (!filter.test(cst)) {
                continue;
            }
            if (cst.getRegistryName().equals(name)) {
                return cst;
            }
        }
        throw CONSTELLATION_NOT_FOUND.createWithContext(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggestIterable(
                ConstellationRegistry.getAllConstellations()
                        .stream()
                        .filter(this.filter)
                        .map(IConstellation::getRegistryName)
                        .collect(Collectors.toList()),
                builder);
    }

    public static ArgumentTypeConstellation weak() {
        return new ArgumentTypeConstellation(c -> c instanceof IWeakConstellation);
    }

    public static ArgumentTypeConstellation major() {
        return new ArgumentTypeConstellation(c -> c instanceof IMajorConstellation);
    }

    public static ArgumentTypeConstellation minor() {
        return new ArgumentTypeConstellation(c -> c instanceof IMinorConstellation);
    }

    public static ArgumentTypeConstellation any() {
        return new ArgumentTypeConstellation(c -> true);
    }

}
