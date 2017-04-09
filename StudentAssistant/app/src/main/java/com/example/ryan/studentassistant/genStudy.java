package com.example.ryan.studentassistant;

/**
 * Created by Ryan on 4/8/2017.
 */

import java.util.ArrayList;

public class genStudy {
    private int breakLength[], bufferMin, startHour, endHour, numClasses, hoursTotal, daysPerWeekStudy;
    private List<Class> classes;
    private List<studyTime> sTime;
    private char dayOfWeek;

    public genStudy(int buffMin, int startH, int endH, char dow, int hTotal, int dpwStudy, Class[] clss) {
        dayOfWeek = dow;
        bufferMin = buffMin;
        startHour = startH;
        endHour = endH;
        numClasses = numClass;
        daysPerWeekStudy = dpwStudy;
        classes = new ArrayList<Clss>();
        sTime = new ArrayList<studyTime>();

        // initialize array list and remove classes that do not occur on current day of week
        initClasses(clss);
        isolateClasses();
        hoursTotal = calcTotalHours(hTotal);

        // initialize breakLength
        breakLength = new int[classes.size()];
        initBreakLength();
    }

    // initializes classes array list
    private void initClasses(Class[] clss) {
        // assign clss objects to classes
        for (int i = 0; i < clss.length; i++)
            classes.add(clss[i]);
    }

    // initializes breakLength
    private void initBreakLength() {
        // get amount of time between each class
        for (int i = 0; i < breakLength.length; i++)
            breakLength[i] = classes.get(i).endTime() - classes.get(i+1).startTime();
    }

    // remove classes from array that do not occur on current day of week
    private void isolateClasses() {
        for (int i = 0; i < classes.size(); i++) {
            if (!classes.get(i).dayOfWeek().contains(dayOfWeek)) {
                classes.remove[i];
                i--;
            }
        }
    }

    public int calcTotalHours(int h) {
        int totalTime = 0;
        for (int i = 0; i < classes.size(); i++)
            totalTime += classes.endTime() - classes.startTime();

        totalTime += h;
        return totalTime;
    }

    public int calcTotalTime() {
        int totalTime = 0;
        for (int i = 0; i < classes.size(); i++)
            totalTime += classes.endTime() - classes.startTime();

        for (int i = 0; i < sTime.size(); i++)
            totalTime += sTime.endTime() - sTime.startTime();

        return totalTime;
    }

    public void generate() {
        if (startHour + bufferMin < classes.get(0).startTime())
            sTime.add(startHour, classes.get(0).startTime() - bufferMin);

        for (int i = 0; i < classes.size(); i++) {
            if (calcTotalTime() < hoursTotal && classes.get(i).endTime() + (bufferMin * 2) < classes.get(i + 1).startTime())
                sTime.add(classes.get(i).endTime() + bufferMin, classes.get(i + 1).startTime() - bufferMin);
        }

        if (calcTotalTime() < hoursTotal && classes[classes.size() - 1].endTime() + bufferMin < endHour)
            sTime.add(classes[i].endTime() + bufferMin, endHour - hoursTotal);
    }
}
