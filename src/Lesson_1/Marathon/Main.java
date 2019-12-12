package Lesson_1.Marathon;

import Lesson_1.Marathon.Competitor.*;
import Lesson_1.Marathon.Obstacle.*;

public class Main {

    public static void main(String[] args) {

        Course course = new Course(new Cross(80), new Wall(2), new Water(50), new Cross(120));

        Team team = new Team("Team 1", new Human("Боб"), new Human("Роб"), new Cat("Барсик"), new Dog("Бобик"));

        course.doIt(team);

        team.showResults();
        team.showTeamInfo();

    }
}

