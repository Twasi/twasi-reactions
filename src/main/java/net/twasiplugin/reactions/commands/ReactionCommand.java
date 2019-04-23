package net.twasiplugin.reactions.commands;

import net.twasi.core.database.models.User;
import net.twasi.core.database.models.permissions.PermissionGroups;
import net.twasi.core.plugin.api.customcommands.TwasiCustomCommandEvent;
import net.twasi.core.plugin.api.customcommands.TwasiPluginCommand;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.DataService;
import net.twasi.core.translations.renderer.TranslationRenderer;
import net.twasiplugin.reactions.ReactionsUserPlugin;
import net.twasiplugin.reactions.database.ReactionEntity;
import net.twasiplugin.reactions.database.ReactionRepository;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class ReactionCommand extends TwasiPluginCommand {

    private ReactionsUserPlugin userPlugin;
    private ReactionRepository repo = ServiceRegistry.get(DataService.class).get(ReactionRepository.class);

    public ReactionCommand(ReactionsUserPlugin twasiUserPlugin) {
        super(twasiUserPlugin);
        this.userPlugin = twasiUserPlugin;
    }

    @Override
    protected boolean execute(TwasiCustomCommandEvent e) {
        TranslationRenderer renderer = e.getRenderer();
        User user = e.getStreamer().getUser();

        if (!e.hasArgs()) {
            e.reply(renderer.render("syntax"));
            return false;
        }

        switch (e.getArgs().get(0).toLowerCase()) {
            case "add":

                if (e.getArgs().size() >= 3) {
                    String key = e.getArgs().get(1);
                    String reaction = e.getArgsAsOne().substring(("add " + key).length());
                    if (userPlugin.getReactions().stream().anyMatch(r -> r.getKey().equalsIgnoreCase(key))) {
                        e.reply(renderer.render("add.duplicate"));
                        return false;
                    }
                    ReactionEntity entity = new ReactionEntity(user, key, reaction, PermissionGroups.VIEWER);
                    userPlugin.addReaction(entity);
                    renderer.bindObject("new", entity);
                    e.reply(renderer.render("add.success"));
                    return true;
                } else {
                    e.reply(renderer.render("add.syntax"));
                    return false;
                }

            case "del":

                if (e.getArgs().size() >= 2) {
                    String key = e.getArgs().get(1);
                    ReactionEntity reaction = repo.getReaction(user, key);
                    if (reaction != null) {
                        userPlugin.delReaction(reaction);
                        renderer.bindObject("deleted", reaction);
                        e.reply(renderer.render("del.success"));
                        return true;
                    } else {
                        e.reply(renderer.render("del.not_found"));
                        return false;
                    }
                } else {
                    e.reply(renderer.render("del.syntax"));
                    return false;
                }

            case "edit":

                if (e.getArgs().size() >= 3) {
                    String key = e.getArgs().get(1);
                    String newContent = e.getArgsAsOne().substring(("edit " + key).length());
                    ReactionEntity reaction = repo.getReaction(user, key);
                    if (reaction != null) {
                        userPlugin.delReaction(reaction);
                        reaction = new ReactionEntity(user, reaction.getKey(), newContent, reaction.getPermissionGroup());
                        userPlugin.addReaction(reaction);
                        renderer.bindObject("edited", reaction);
                        e.reply(renderer.render("edit.success"));
                        return true;
                    } else {
                        e.reply(renderer.render("edit.not_found"));
                        return false;
                    }
                } else {
                    e.reply(renderer.render("edit.syntax"));
                    return false;
                }

            case "list":

                List<ReactionEntity> all = repo.getAllByUser(user);
                if (all.size() > 0) {
                    renderer.bind("reactions", String.join(", ", all.stream().map(ReactionEntity::getKey).collect(Collectors.toList())));
                    e.reply(renderer.render("list.success"));
                } else {
                    e.reply(renderer.render("list.empty"));
                }
                return true;

            default:
                e.reply(renderer.render("syntax"));
                return false;
        }
    }

    @Override
    public String getCommandName() {
        return "reactions";
    }

    @Override
    public boolean allowsListing() {
        return false;
    }

    @Override
    public boolean allowsTimer() {
        return false;
    }

    @Override
    public Duration getCooldown() {
        return Duration.ZERO;
    }

    @Override
    public String requirePermissionKey() {
        return "twasi.reactions.manage";
    }
}
