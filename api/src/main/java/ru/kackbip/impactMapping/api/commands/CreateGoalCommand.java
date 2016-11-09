package ru.kackbip.impactMapping.api.commands;

import java.util.Date;

/**
 * Created by ryashentsev on 13.10.2016.
 */

public class CreateGoalCommand {
    private String title;
    private Date date;

    public CreateGoalCommand(
            String title,
            Date date
    ){
        this.title = title;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }
}
