package dev.smughito.eventcorelib.commands;

import dev.smughito.eventcorelib.api.Team;
import dev.smughito.eventcorelib.api.TeamManager;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TeamCommand implements CommandExecutor {

    private final TeamManager teamManager;

    public TeamCommand(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("eventcore.team") && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Nemáš oprávnění!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Použití: /team <create|delete|list>");
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "create":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.YELLOW + "/team create <id> <hexColor>");
                    return true;
                }

                String id = args[1];
                String colorHex = args[2];

                try {
                    TextColor color = TextColor.fromHexString(colorHex);
                    Team team = teamManager.createTeam(id, id, color); // displayName = id
                    sender.sendMessage(ChatColor.GREEN + "Tým '" + id + "' vytvořen s barvou " + colorHex);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "Chybný formát barvy: " + colorHex + " (musí být např. #FF0000)");
                }

                return true;

            case "delete":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.YELLOW + "/team delete <id>");
                    return true;
                }

                String deleteId = args[1];
                if (teamManager.deleteTeam(deleteId)) {
                    sender.sendMessage(ChatColor.GREEN + "Tým '" + deleteId + "' odstraněn.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Tým '" + deleteId + "' neexistuje.");
                }
                return true;

            case "list":
                sender.sendMessage(ChatColor.GREEN + "Seznam týmů:");
                for (Team t : teamManager.getAllTeams()) {
                    sender.sendMessage("- " + t.getId() + " (" + t.getColor() + ")");
                }
                return true;

            default:
                sender.sendMessage(ChatColor.RED + "Neznámý sub‑příkaz!");
                return true;
        }
    }
}
