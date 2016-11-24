package ru.kackbip.impactMapping.api.commands;

import java.util.UUID;

/**
 * Created by ryashentsev on 13.10.2016.
 */

public class RemoveGoalCommand {
    private UUID id;

    public RemoveGoalCommand(UUID id){
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
