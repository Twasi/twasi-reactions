package net.twasiplugin.reactions;

import net.twasi.core.plugin.api.TwasiUserPlugin;
import net.twasi.core.plugin.api.customcommands.TwasiPluginCommand;
import net.twasi.core.plugin.api.events.TwasiEnableEvent;
import net.twasi.core.plugin.api.events.TwasiInstallEvent;
import net.twasi.core.plugin.api.events.TwasiMessageEvent;
import net.twasi.core.services.ServiceRegistry;
import net.twasi.core.services.providers.DataService;
import net.twasiplugin.reactions.commands.ReactionCommand;
import net.twasiplugin.reactions.database.ReactionEntity;
import net.twasiplugin.reactions.database.ReactionRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class ReactionsUserPlugin extends TwasiUserPlugin {

    private List<ReactionEntity> reactions = new ArrayList<>();
    private ReactionRepository repo = ServiceRegistry.get(DataService.class).get(ReactionRepository.class);
    private List<TwasiPluginCommand> commands;

    public ReactionsUserPlugin() {
        this.commands = Collections.singletonList(new ReactionCommand(this));
    }

    @Override
    public void onEnable(TwasiEnableEvent e) {
        this.reactions = repo.getAllByUser(getTwasiInterface().getStreamer().getUser());
    }

    @Override
    public void onInstall(TwasiInstallEvent e) {
        e.getModeratorsGroup().addKey("twasi.reactions.manage");
        e.getAdminGroup().addKey("twasi.reactions.manage");
    }

    @Override
    public void onUninstall(TwasiInstallEvent e) {
        e.getModeratorsGroup().removeKey("twasi.reactions.manage");
        e.getAdminGroup().removeKey("twasi.reactions.manage");
    }

    public List<ReactionEntity> getReactions() {
        return reactions;
    }

    public void addReaction(ReactionEntity entity) {
        repo.add(entity);
        reactions.add(entity);
    }

    public void delReaction(ReactionEntity entity) {
        repo.remove(entity);
        reactions = reactions.stream().filter(elem -> !entity.getId().equals(elem.getId())).collect(Collectors.toList());
    }

    @Override
    public void onMessage(TwasiMessageEvent e) {
        if (e.getMessage().isCommand()) return;
        AtomicBoolean answeredOnce = new AtomicBoolean(false);
        Thread t1 = new Thread(() -> Arrays.asList(e.getMessage().getMessage().split(" ")).forEach(part -> reactions.forEach(reaction -> {
            if (reaction.getKey().equalsIgnoreCase(part) && e.getMessage().getSender().getGroups().contains(reaction.getPermissionGroup())) {
                if (answeredOnce.get()) return;
                answeredOnce.set(true);
                e.getMessage().reply(reaction.getReaction());
            }
        })));
        t1.setDaemon(true);
        t1.start();
    }

    @Override
    public List<TwasiPluginCommand> getCommands() {
        return commands;
    }
}
