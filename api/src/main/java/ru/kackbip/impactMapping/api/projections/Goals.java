package ru.kackbip.impactMapping.api.projections;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ryashentsev on 08.10.2016.
 */

public class Goals {
    private List<Goal> goals;

    public Goals(List<Goal> goals){
        this.goals = new ArrayList<>(goals);
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public static class Goal {
        private String title;
        private Date date;

        public Goal(String title, Date date){
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
}
