package ru.kackbip.impactMapping.api.projections;

import org.apache.commons.lang.builder.HashCodeBuilder;

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

        @Override
        public boolean equals(Object obj) {
            if(obj==null) return false;
            if(!(obj instanceof Goal)) return false;
            Goal goal = (Goal)obj;
            return title.equals(goal.getTitle()) &&
                    date.equals(goal.getDate());
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 31)
                    .append(title)
                    .append(date)
                    .toHashCode();
        }
    }
}
