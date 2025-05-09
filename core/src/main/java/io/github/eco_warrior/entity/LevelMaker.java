package io.github.eco_warrior.entity;

import com.badlogic.gdx.Gdx;

public abstract class LevelMaker {

    protected String displayTimer(float timerSeconds){
        return "Countdown : " + Math.round(timerSeconds) + " s";
    }


    protected boolean isFinished() {
        //do something here
        return true;
    }

    protected abstract void winningDisplay();
    protected abstract void losingDisplay();
}
