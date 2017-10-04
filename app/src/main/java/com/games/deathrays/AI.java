package com.games.deathrays;

import android.graphics.Point;

import java.util.LinkedList;
import java.util.Random;

public class AI {

    public Thread calculating;
    /**
     * Вся логика, которая выдает конечный результат в виде направления движения. Здесь вызываются
     * все методы, просчитывающие движения бота.
     */

    protected LinkedList<Point> way = new LinkedList<Point>();
    AI_SearchPath searchPath;
    Controller bot;
    GameView view;
    Random random;
    boolean noEscape = false;
    static byte[][] map = new byte[1][2];
    private int direction;  // направление бота

    AI(Controller bot, GameView view) {
        this.bot = bot;
        this.view = view;
        this.direction = bot.direction;
        init(); // вычисление направления
    }

    /**
     * метод для запуска вычисления из других классов
     */
    protected void init() {
        searchPath = new AI_SearchPath();
        random = new Random();
        bot.setDirection(logicDirection());
        bot.stepNext();
    }

    int logicDirection() {
        int distance;
        if (Math.abs(bot.getCell().x - view.player.getCell().x) < 10
                && Math.abs(bot.getCell().y - view.player.getCell().y) < 10) distance = 2;
        else distance = 7;
        if (bot.stepCount > distance && !noEscape) {
            bot.stepCount = 0;
            runAstar();
        } else if (bot.stepCount != distance && !noEscape) {
            for (Point p : way) {
                if (bot.getCell().x + 1 < view.CELLS_HOR)
                    if (new Point(bot.getCell().x + 1, bot.getCell().y).equals(p)) direction = 3;
                if (bot.getCell().x - 1 > -1)
                    if (new Point(bot.getCell().x - 1, bot.getCell().y).equals(p)) direction = 1;
                if (bot.getCell().y + 1 < view.CELLS_VERT)
                    if (new Point(bot.getCell().x, bot.getCell().y + 1).equals(p)) direction = 2;
                if (bot.getCell().y - 1 > 0)
                    if (new Point(bot.getCell().x, bot.getCell().y - 1).equals(p)) direction = 0;

            }

        }

        preventCollision();
        return direction;
    }

    void runAstar() {
        calculating = new Thread(new Runnable() {
            @Override
            public void run() {
                way = searchPath.aStar(bot.getNextCell(direction), view.player.getCell(), view.CELL_SIZE
                        , view.CELLS_HOR, view.CELLS_VERT, view.graphics.arrayMap);
                if (searchPath.noroute) noEscape = true;
            }
        });
        calculating.setDaemon(true);
        calculating.setPriority(Thread.MIN_PRIORITY);
        calculating.start();
//            РИСОВАНИЕ ПУТИ
//            view.graphics._map.drawColor(Color.BLACK);
//            int i = 0;
//            for (Point p : way) {
//                Paint paint = new Paint();
//                i++;
//                paint.setColor(Color.argb(255, 255, 20 * i, 20 * i));
//                view.graphics._map.drawCircle(p.x * view.CELL_SIZE,
//                        p.y * view.CELL_SIZE, 4, paint);
//            }
    }

    /**
     * Предупреждение столкновения с картой
     */
    private void preventCollision() {
        map = view.graphics.arrayMap;
//        for (int i = 0; i < 2; i++) {
            if (getMap(bot.getNextCell(direction).x, bot.getNextCell(direction).y) != 0) {
                int direction = this.direction;
                direction = (direction + 1) % 4;
                if (getMap(bot.getNextCell(direction).x, bot.getNextCell(direction).y) != 0)
                    this.direction = (this.direction + 3) % 4;
                else this.direction = (this.direction + 1) % 4;
            }
//        }
    }

    int getMap(int x, int y) {
        if (x > 0 && y > 1 && x < view.CELLS_HOR && y < view.CELLS_VERT) return map[x][y];
        return 1;
    }
}
