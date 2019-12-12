package Lesson_1.Marathon.Competitor;

public class Team {

    private String teamName;
    private Competitor[] members;


    public Team(String teamName, Competitor... members) {
        this.teamName = teamName;
        this.members = members;
    }
    public Competitor[] getTeam() {
        return members;
    }

    public String getTeamName() {
        return teamName;
    }

    public void showResults() {
        System.out.println("Winners:");
        for (Competitor m: members) {
            if(m.isOnDistance())
                m.info();
        }
    }
    public void showTeamInfo() {
        System.out.println("Team Name: " + teamName);
        for (Competitor m: members) {
            m.showInfo();
        }
    }



}
