package com.games.deathrays;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView {

    static int score = 0, frameCount = 0;                       // счетчик очков и кадров
    final int CELLS_HOR = 42, CELLS_VERT = 52;
    final int CELL_SIZE = 8;                                    // размер ячейки сетки
    public int height, width;                                   // ширина и высота GameView
    boolean gameover, pause, isBotGameover;                     // состояние проигрыша и паузы
    Looper looper;                                              // дополнительный поток, для отрисовки
    Graphics graphics;                                          // вся графика
    Controller player, bot;                                     // управление и физика
    AI ai;                                                      // ИИ
    int MAX_TOUCH_TIME = 400,                                   // переменные свайпа
            MIN_DISTANCE = 25;
    long stateTime;
    float x, y;
    private float ratio = 1.0f;
    Random r;

    /**
     * @param attributeSet нужно для работы нашего view в составе layout activity_game
     */
    public GameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        init();                                                 // инициализация всех начальных переменных
        getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                looper.setRunning(true);
                looper.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                boolean retry = true;
                looper.setRunning(false);
                while (retry) {
                    try {
//                        ai.calculating.join();
                        looper.join();
                        retry = false;
                    } catch (InterruptedException e) {
                        System.out.println("Error! " + e);
                    }
                }
            }
        });


    }

    // инициализация всех начальных переменных
    void init() {
        r = new Random();
        gameover = false;
        pause = false;

        score = 0;

        ratio = (float) getResources().getDisplayMetrics().widthPixels / (CELLS_HOR * CELL_SIZE);
        this.setY(((getResources().getDisplayMetrics().heightPixels - 64 * getResources().getDisplayMetrics().scaledDensity)
                - CELLS_VERT * CELL_SIZE * ratio) / 2);
        width = (CELLS_HOR * CELL_SIZE);
        height = (CELLS_VERT * CELL_SIZE);

        MIN_DISTANCE *= ratio;

        graphics = new Graphics(this);

        // в контроллер передаем id объекта и gameview (0 - игрок, 1 - бот)
        player = new Controller(0, this);
        bot = new Controller(1, this);

        post(new Runnable() {
            @Override
            public void run() {
                if (((Main) getContext()).getSpeed() == 2)
                    ai.noEscape = true;
                else
                    ai.noEscape = false;
                if (((Main) getContext()).getSpeed() == 6)
                    graphics.setLevel(r.nextInt(5) + 1);

            }
        });

        ai = new AI(bot, this);

        looper = new Looper(this);
    }

    // меняет состояние игры на паузу и обратно
    protected void changePauseState() {
        pause = !pause;
    }


    // метод для остановки игры из других классов
    protected void finish() {
        looper.setRunning(false);
    }


    // отрисовка очередного кадра
    protected void update(final Canvas canvas) {
        if (!gameover) {
            canvas.scale(ratio, ratio);
            if (!pause) {   // не рисовать ничего нового во время паузы
                // запуск вычисления действий бота
                // очистка прозрачного (не использованного) цвета. короче чистим экран
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//                canvas.drawColor(Color.GRAY);

                graphics.draw(canvas, 10);//рисует карту
                graphics.draw(canvas, 9);//рисует эффекты
                graphics.draw(canvas, 1);//рисует бота
                graphics.draw(canvas, 0);//рисует игрока

                frameCount++;   // считает кадры

                if (frameCount == 10)    //если 10 кардров прошло - добавляем очко игроку
                    post(new Runnable() {   // менять что-то на layout можно только только так,
                        @Override           // через дополнительный поток класса
                        public void run() {
                            frameCount = 0; // обнуляем счетчик кадров
                            score++;        // добавляем очко
                            ((TextView) ((Main) getContext())   //обновляем очки на layout
                                    .findViewById(R.id.scoreValueText)).setText("" + score);
                        }
                    });
            } else {
                try {
                    ai.calculating.join();
                    post(new Runnable() {
                        @Override
                        public void run() {
                            ((Main) getContext()).findViewById(R.id.gameMenu).setVisibility(GONE);   //скрываем pause
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {    // если проигрыш
            looper.setRunning(false);   // останавливаем отрисовку
            post(new Runnable() {       // вызываем дополнительный поток
                @Override
                public void run() {
                    ((Main) getContext()).setContentView(R.layout.gameover);    // выводим экран проигрыша

                    ((Main) getContext()).bannerUpdate();
                    if (isBotGameover) {

                        if (((Main) getContext()).getMaxScore(((Main) getContext()).getSpeed()) <= score) {          // сравниваем максимальные очки из памяти
                            ((TextView) ((Main) getContext())
                                    .findViewById(R.id.gameoverText)).setText("You Win!!!");

                        }
                        ArrayList<String> a = ((Main) getContext()).getScores(((Main) getContext()).getSpeed());

                        if (a.size() != 0)
                            for (int i = 0; i < a.size(); i++) {
                                if (Integer.parseInt(a.get(i).split(" ")[0]) < score) {
                                    a.add(i, "" + score + new SimpleDateFormat(" dd.MM.yy").format(System.currentTimeMillis()));
                                    break;
                                }
                            }
                        else {
                            a.add("" + score + new SimpleDateFormat(" dd.MM.yy").format(System.currentTimeMillis()));
                        }


                        ((Main) getContext()).setScores(a, ((Main) getContext()).getSpeed());

                    }

                    ((TextView) ((Main) getContext()).findViewById(R.id.gameoverScore))
                            .setText("Your score: " + score);  //выводим количество набранных очков
                    ((TextView) ((Main) getContext()).findViewById(R.id.gameoverMaxScore))
                            .setText("Last record: " + ((Main) getContext())
                                    .getMaxScore(((Main) getContext()).getSpeed()));  //выводим количество набранных очков
                    score = 0;  //обнуляем очки
                }
            });
        }
    }

    /**
     * объявляет проигрыш, метод для объявления из других классов
     *
     * @param gameover Проиграл?
     */
    public void setGameover(boolean gameover) {
        this.gameover = gameover;
    }

    /**
     * Обработка жестов
     * @param event Движение
     */
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE: {
                /**Обновление позиции касания */
                if (System.currentTimeMillis() - stateTime > MAX_TOUCH_TIME) {
                    x = event.getX();
                    y = event.getY();
                    stateTime = System.currentTimeMillis();
                }
                /** Установка аправления движения игрока через свайп */
                if (Math.abs(event.getX() - x) > Math.abs(event.getY() - y)) {
                    if (event.getX() - x > MIN_DISTANCE) player.setDirection(3);        //вправо
                    else if (x - event.getX() > MIN_DISTANCE) player.setDirection(1);   //влево
                } else {
                    if (event.getY() - y > MIN_DISTANCE) player.setDirection(2);   //вниз
                    else if (y - event.getY() > MIN_DISTANCE) player.setDirection(0);   //вверх
                }

                break;
            }
        }

        return true;
    }

}
