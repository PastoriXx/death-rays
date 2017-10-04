package com.games.deathrays;

import android.graphics.Canvas;

public class Looper extends Thread {
    /**
     * Кадры в секунду
     */
    static final long FPS = 30; // НЕ ТРОГАТЬ!
    protected Canvas canvas;
    /**
     * Объект класса GameView
     */
    private GameView gameView;
    /**
     * Задаем состояние потока
     */
    private boolean STATE_RUNNING = false;

    /**
     * Конструктор класса
     */
    public Looper(GameView gameView) {
        this.gameView = gameView;
        setDaemon(true);
        setName("MAIN GAME TrEAD");
        setPriority(Thread.MAX_PRIORITY);
    }

    /**
     * Задание состояния потока
     */
    protected void setRunning(boolean run) {
        STATE_RUNNING = run;
    }

    @Override
    public void run() {
        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        while (STATE_RUNNING) {
            startTime = System.currentTimeMillis();
            if (gameView.pause) continue; // если состояние паузы - пропустить следующий код

            canvas = null;
            try {
                canvas = gameView.getHolder().lockCanvas();
                synchronized (gameView.getHolder()) {
                    gameView.update(canvas);
                }
            } finally {
                if (canvas != null) {
                    gameView.getHolder().unlockCanvasAndPost(canvas);
                }
            }
            sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (Exception e) {
            }
        }
    }
}