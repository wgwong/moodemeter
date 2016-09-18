package hackmit2016.moodometer;

import java.util.List;

public class DangerLevelDeterminer {

    public enum Danger {
        LOW, MEDIUM, HIGH;
    }

    private enum MoodLevel {
        BAD, OKAY, GREAT;
    }

    public Danger getDangerLevel(List<Integer> moods) {
        // Cases to consider:
        // 1. low moods for last two days (high)
        // 2. "decreasing" moods for last three days with average <= 3 (high)
        // 3. "decreasing" moods for last three days with average <= 10/3 (medium)
        // 4. average value of last three days is 0.5 lower than average of other days (medium)
        // 5. average mood for last three days with average >= 3.5 and not last two days great (medium)
        // 6. great moods for last two days (low)
        // 7. everything is medium?

        int numMoods = moods.size();

        if (condition1(moods, numMoods)) {
            // Low moods for last two days
            return Danger.HIGH;
        } else if (condition2(moods, numMoods)) {
            // "Decreasing" moods for last three days with average <= 3
            return Danger.HIGH;
        } else if (condition3(moods, numMoods)) {
            // "Decreasing" moods for last three days with average <= 10/3
            return Danger.MEDIUM;
        } else if (condition4(moods, numMoods)) {
            // Average value of last three days is 0.5 lower than average of other days
            return Danger.MEDIUM;
        } else if (condition5(moods, numMoods)) {
            // Average mood for last three days with average 3.5
            return Danger.MEDIUM;
        } else if (condition6(moods, numMoods)) {
            // Great moods for last two days
            return Danger.LOW;
        } else {
            return Danger.MEDIUM;
        }
    }

    private boolean condition1(List<Integer> moods, int numMoods) {
        return getMoodLevel(moods.get(numMoods - 1)) == MoodLevel.BAD &&
                getMoodLevel(moods.get(numMoods - 2)) == MoodLevel.BAD;
    }

    private boolean condition2(List<Integer> moods, int numMoods) {
        double thirdLastMood = moods.get(numMoods - 3);
        double secondLastMood = moods.get(numMoods - 2);
        double lastMood = moods.get(numMoods - 1);
        return (thirdLastMood <= secondLastMood &&
                secondLastMood <= lastMood &&
                lastMood != 3 &&
                getLastThreeMoodAverage(moods, numMoods) <= 3.0);
    }

    private boolean condition3(List<Integer> moods, int numMoods) {
        double thirdLastMood = moods.get(numMoods - 3);
        double secondLastMood = moods.get(numMoods - 2);
        double lastMood = moods.get(numMoods - 1);
        return (thirdLastMood <= secondLastMood &&
                secondLastMood <= lastMood &&
                getLastThreeMoodAverage(moods, numMoods) <= 10.0 / 3.0);
    }

    private boolean condition4(List<Integer> moods, int numMoods) {
        double averageMood = getAverageMood(moods, numMoods);
        double lastThreeMoodAverage = getLastThreeMoodAverage(moods, numMoods);
        return averageMood - lastThreeMoodAverage >= 0.5;
    }

    private boolean condition5(List<Integer> moods, int numMoods) {
        return getLastThreeMoodAverage(moods, numMoods) >= 3 && !condition6(moods, numMoods);
    }

    private boolean condition6(List<Integer> moods, int numMoods) {
        return getMoodLevel(moods.get(numMoods - 1)) == MoodLevel.GREAT &&
                getMoodLevel(moods.get(numMoods - 2)) == MoodLevel.GREAT;
    }

    private double getAverageMood(List<Integer> moods, int numMoods) {
        double averageMood = 0;
        for (int mood : moods) {
            averageMood += mood;
        }
        averageMood /= numMoods;
        return averageMood;
    }

    private double getLastThreeMoodAverage(List<Integer> moods, int numMoods) {
        double thirdLastMood = moods.get(numMoods - 3);
        double secondLastMood = moods.get(numMoods - 2);
        double lastMood = moods.get(numMoods - 1);
        return (thirdLastMood + secondLastMood + lastMood) / 3;
    }



    private MoodLevel getMoodLevel(int mood) {
        if (mood <= 2) {
            return MoodLevel.BAD;
        } else if (mood == 3) {
            return MoodLevel.OKAY;
        } else {
            return MoodLevel.GREAT;
        }
    }

}
