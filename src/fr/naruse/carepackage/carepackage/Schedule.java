package fr.naruse.carepackage.carepackage;

public class Schedule {

    private final int every;
    private final int randomPercentage;
    private final boolean broadcast;

    public Schedule(int every, int randomPercentage, boolean broadcast) {
        this.every = every;
        this.randomPercentage = randomPercentage;
        this.broadcast = broadcast;
    }

    public int getEvery() {
        return every;
    }

    public int getRandomPercentage() {
        return randomPercentage;
    }

    public boolean canBroadcast() {
        return broadcast;
    }

    public static class Builder {

        public static Schedule fromString(String format) {
            int every = 1;
            int randomPercentage = 80;
            boolean broadcast = true;
            for (String s : format.replace("{", "").replace("}", "").split(",")) {
                String[] args = s.split(":");
                switch (args[0]){
                    case "every":
                        every = Integer.valueOf(args[1]);
                        break;
                    case "randomSpawn":
                        randomPercentage = Integer.valueOf(args[1]);
                        break;
                    case "broadcast":
                        broadcast = Boolean.valueOf(args[1]);
                        break;
                }
            }

            return new Schedule(every, randomPercentage, broadcast);
        }

    }
}
