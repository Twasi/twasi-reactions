package net.twasiplugin.reactions.database;

import jdk.nashorn.internal.ir.annotations.Reference;
import net.twasi.core.database.models.BaseEntity;
import net.twasi.core.database.models.User;
import net.twasi.core.database.models.permissions.PermissionGroups;
import org.mongodb.morphia.annotations.*;

@Entity(value = "twasi.reactions", noClassnameStored = true)
@Indexes(@Index(fields = {@Field("user"), @Field("key")}, options = @IndexOptions(unique = true)))
public class ReactionEntity extends BaseEntity {

    @Reference
    private User user;

    private String key;
    private String reaction;

    private PermissionGroups permissionGroup;

    public ReactionEntity(User user, String key, String reaction, PermissionGroups permissionGroup) {
        this.user = user;
        this.key = key.toLowerCase();
        this.reaction = reaction;
        this.permissionGroup = permissionGroup;
    }

    public ReactionEntity() {
    }

    public User getUser() {
        return user;
    }

    public String getKey() {
        return key.toLowerCase();
    }

    public String getReaction() {
        return reaction;
    }

    public PermissionGroups getPermissionGroup() {
        return permissionGroup;
    }
}
