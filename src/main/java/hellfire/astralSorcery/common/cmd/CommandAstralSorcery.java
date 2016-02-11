package hellfire.astralSorcery.common.cmd;

import hellfire.astralSorcery.api.constellation.IConstellation;
import hellfire.astralSorcery.api.constellation.IConstellationTier;
import hellfire.astralSorcery.common.constellation.ConstellationRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 09.02.2016 18:41
 */
public class CommandAstralSorcery extends CommandBase {

    private List<String> cmdAliases = new ArrayList<String>();

    public CommandAstralSorcery() {
        this.cmdAliases.add("astralsorcery");
        this.cmdAliases.add("as");
    }

    @Override
    public String getCommandName() {
        return "astralsorcery";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/astralsorcery <action> [player] [arguments...]";
    }

    @Override
    public List<String> getCommandAliases() {
        return cmdAliases;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 1;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            sender.addChatMessage(new ChatComponentText("§cNot enough arguments."));
            sender.addChatMessage(new ChatComponentText("§cType \"/astralsorcery help\" for help"));
            return;
        }
        if(args.length >= 1) {
            String identifier = args[0];
            if(identifier.equalsIgnoreCase("help")) {
                displayHelp(sender);
            } else if(identifier.equalsIgnoreCase("constellation") || identifier.equalsIgnoreCase("constellations")) {
                listConstellations(sender);
            }
        }
    }

    private void displayHelp(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText("§a/astralsorcery constellations§7 - lists all constellations"));
    }

    private void listConstellations(ICommandSender sender) {
        for(IConstellationTier tier : ConstellationRegistry.ascendingTiers()) {
            sender.addChatMessage(new ChatComponentText("§cTier: " + tier.tierNumber() + " - showupChance: " + tier.getShowupChance()));
            for(IConstellation c : tier.getConstellations()) {
                sender.addChatMessage(new ChatComponentText("§7" + c.getName()));
            }
        }
    }

}
