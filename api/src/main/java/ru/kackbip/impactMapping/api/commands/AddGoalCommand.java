package ru.kackbip.impactMapping.api.commands;

import java.util.Date;
import java.util.UUID;

/**
 * Created by ryashentsev on 13.10.2016.
 */

public class AddGoalCommand {
    private String title;
    private Date date;
    private UUID id;

    public AddGoalCommand(
            UUID id,
            String title,
            Date date
    ){
        this.title = title;
        this.date = date;
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }
}
