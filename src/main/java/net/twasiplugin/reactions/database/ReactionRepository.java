package net.twasiplugin.reactions.database;

import net.twasi.core.database.lib.Repository;
import net.twasi.core.database.models.User;

import java.util.List;

public class ReactionRepository extends Repository<ReactionEntity> {

    public List<ReactionEntity> getAllByUser(User user) {
        return store.createQuery(ReactionEntity.class).field("user").equal(user).asList();
    }

    public ReactionEntity getReaction(User user, String key){
        return store.createQuery(ReactionEntity.class).field("user").equal(user).field("key").equal(key.toLowerCase()).get();
    }

}
