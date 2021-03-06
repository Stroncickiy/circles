package com.jarst.dto;

import com.jarst.domain.Relationship;
import com.jarst.domain.User;
import lombok.*;

@Builder
@ToString(exclude = {"user", "relationship"})
@EqualsAndHashCode
public class RelatedUserDTO {

    private final User user;

    @Getter
    private final UserStats userStats;

    private final Relationship relationship;

    @Getter
    @Setter
    private Boolean isMyself = null;

    public long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getUsername();
    }

    public String getName() {
        return user.getName();
    }

    public Long getRelationshipId() {
        return relationship.getId();
    }

}
