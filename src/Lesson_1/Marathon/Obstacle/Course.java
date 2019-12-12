package Lesson_1.Marathon.Obstacle;

import Lesson_1.Marathon.Competitor.*;
import Lesson_1.Marathon.Obstacle.*;

public class Course {

    private Obstacle[] obstacles;

    public Course(Obstacle... obstacles)
    {
        this.obstacles = obstacles;
    }

    public void doIt(Team team)
    {
        Competitor [] competitors = team.getTeam();
        for (Competitor c : competitors) {
            for (Obstacle o : obstacles) {
                o.doIt(c);
                if (!c.isOnDistance()) break;
            }
        }
    }
}
