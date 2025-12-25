package dev.smughito.example;

import dev.smughito.eventcorelib.EventCoreLib;
import dev.smughito.eventcorelib.api.Team;
import dev.smughito.eventcorelib.api.TeamManager;
import dev.smughito.eventcorelib.api.PlayerTeamService;
import dev.smughito.eventcorelib.util.ChatFormatter;
import dev.smughito.eventcorelib.util.TabListFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class ExampleUsage extends JavaPlugin implements Listener {

    private TeamManager teamManager;
    private PlayerTeamService playerTeamService;
    private ChatFormatter chatFormatter;
    private TabListFormatter tabListFormatter;

    @Override
    public void onEnable() {
        EventCoreLib coreLib = EventCoreLib.getInstance();

        if (coreLib == null) {
            getLogger().severe("EventCoreLib not found! This plugin requires EventCoreLib to function.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        teamManager = coreLib.getTeamManager();
        playerTeamService = coreLib.getPlayerTeamService();
        chatFormatter = coreLib.getChatFormatter();
        tabListFormatter = coreLib.getTabListFormatter();

        getServer().getPluginManager().registerEvents(this, this);

        setupExampleTeams();

        getLogger().info("Example plugin enabled!");
    }

    private void setupExampleTeams() {
        Team redTeam = teamManager.createTeam("red", "Red Team", NamedTextColor.RED);
        redTeam.setPrefix("<red>[RED] </red>");
        redTeam.setSuffix("<red> ⚔</red>");

        Team blueTeam = teamManager.createTeam("blue", "Blue Team", NamedTextColor.BLUE);
        blueTeam.setPrefix("<blue>[BLUE] </blue>");
        blueTeam.setSuffix("<blue> 🛡</blue>");

        Team greenTeam = teamManager.createTeam("green", "Green Team", TextColor.color(0x00FF00));
        greenTeam.setPrefix("<gradient:green:dark_green>[GREEN]</gradient> ");
        greenTeam.setSuffix(" <green>🌿</green>");

        getLogger().info("Created 3 example teams!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Optional<Team> team = playerTeamService.getPlayerTeam(player.getUniqueId());

        if (team.isPresent()) {
            Component displayName = tabListFormatter.formatPlayerName(player, team);
            player.playerListName(displayName);

            player.sendMessage(Component.text("Welcome back to " + team.get().getDisplayName() + "!")
                .color(team.get().getColor()));
        } else {
            player.sendMessage(Component.text("You're not in a team yet! Use /team join <teamId>")
                .color(NamedTextColor.YELLOW));
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        Optional<Team> team = playerTeamService.getPlayerTeam(player.getUniqueId());

        Component chatMessage = chatFormatter.formatChatMessage(player, event.getMessage(), team);

        getServer().broadcast(chatMessage);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        if (command.getName().equalsIgnoreCase("team")) {
            if (args.length == 0) {
                player.sendMessage(Component.text("Usage: /team <join|leave|info>")
                    .color(NamedTextColor.YELLOW));
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "join":
                    if (args.length < 2) {
                        player.sendMessage(Component.text("Usage: /team join <teamId>")
                            .color(NamedTextColor.RED));
                        return true;
                    }

                    String teamId = args[1];
                    if (playerTeamService.assignToTeam(player.getUniqueId(), teamId)) {
                        Optional<Team> joinedTeam = teamManager.getTeam(teamId);
                        joinedTeam.ifPresent(team -> {
                            player.sendMessage(Component.text("You joined " + team.getDisplayName() + "!")
                                .color(team.getColor()));

                            Component displayName = tabListFormatter.formatPlayerName(player, Optional.of(team));
                            player.playerListName(displayName);
                        });
                    } else {
                        player.sendMessage(Component.text("Team not found!")
                            .color(NamedTextColor.RED));
                    }
                    break;

                case "leave":
                    if (playerTeamService.removeFromCurrentTeam(player.getUniqueId())) {
                        player.sendMessage(Component.text("You left your team!")
                            .color(NamedTextColor.GREEN));
                        player.playerListName(Component.text(player.getName()));
                    } else {
                        player.sendMessage(Component.text("You're not in a team!")
                            .color(NamedTextColor.RED));
                    }
                    break;

                case "info":
                    Optional<Team> team = playerTeamService.getPlayerTeam(player.getUniqueId());
                    if (team.isPresent()) {
                        Team t = team.get();
                        player.sendMessage(Component.text("Team: " + t.getDisplayName())
                            .color(t.getColor()));
                        player.sendMessage(Component.text("Members: " + t.getMemberCount())
                            .color(NamedTextColor.GRAY));
                    } else {
                        player.sendMessage(Component.text("You're not in a team!")
                            .color(NamedTextColor.RED));
                    }
                    break;

                case "list":
                    player.sendMessage(Component.text("Available teams:")
                        .color(NamedTextColor.GOLD));

                    teamManager.getAllTeams().forEach(t -> {
                        player.sendMessage(Component.text("  - " + t.getId() + " (" + t.getDisplayName() +
                            ") - " + t.getMemberCount() + " members")
                            .color(t.getColor()));
                    });
                    break;

                default:
                    player.sendMessage(Component.text("Unknown subcommand! Use /team <join|leave|info|list>")
                        .color(NamedTextColor.RED));
            }
            return true;
        }

        return false;
    }
}
