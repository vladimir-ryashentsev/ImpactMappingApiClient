package ru.kackbip.impactMapping.api.projections;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
        private UUID id;

        public Goal(UUID id, String title, Date date){
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

        @Override
        public boolean equals(Object obj) {
            if(obj==null) return false;
            if(!(obj instanceof Goal)) return false;
            Goal goal = (Goal)obj;
            return id.equals(goal.getId());
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 31)
                    .append(id)
                    .toHashCode();
        }
    }
}
