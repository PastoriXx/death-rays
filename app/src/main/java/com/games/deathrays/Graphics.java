package com.games.deathrays;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;

public class Graphics {
    static Bitmap map, postEffect;  //картинка карты и эффектов, в gameview.update выводится на главный канвас
    /**
     * Переменные пост эффектов
     */
    static Paint postPaint = null;  //настройка цвета и прозрачности эффекта
    GameView view;
    Canvas _map, _postEffect;   // канвас для рисования картинки карты и эффектов
    boolean flag = false;   // рисовать ли эффекты
    Point post;             // точка где рисовать эффект
    int size;               // размер круга в эффектах

    byte[][] arrayMap;
    Paint paintMap;

    Graphics(GameView gameView) {
        view = gameView;
        // пустая картинка карты
        map = Bitmap.createBitmap(view.width,
                view.height, Bitmap.Config.ARGB_8888);
        _map = new Canvas(map);
        // стартовая позиция (путь)
        Paint p = new Paint();
        p.setColor(Color.BLUE);
        _map.drawRect(view.width / 2 - 3, view.height - 4 * view.CELL_SIZE - 3, view.width / 2 + 3, view.height - 4 * view.CELL_SIZE + 3, p);
        p.setColor(Color.RED);
        _map.drawRect(view.width / 2 - 3, 4 * view.CELL_SIZE - 3, view.width / 2 + 3, 4 * view.CELL_SIZE + 3, p);
        // пустая картинка эффекта
        postEffect = Bitmap.createBitmap(view.width,
                view.height, Bitmap.Config.ARGB_8888);
        _postEffect = new Canvas(postEffect);

        // Рамка карты
        paintMap = new Paint();
        paintMap.setColor(Color.GRAY);
        arrayMap = new byte[view.CELLS_HOR][view.CELLS_VERT];
        for (int i = 1; i < view.CELLS_VERT; i++)
            for (int j = 1; j < view.CELLS_HOR; j++) {
                if(i == 1 || i == view.CELLS_VERT - 1 || j == 1 || j == view.CELLS_HOR - 1)
                {
                    arrayMap[j][i] = 1;
                    _map.drawRect(j * view.CELL_SIZE - 3, i * view.CELL_SIZE - 3,
                            j * view.CELL_SIZE + 3, i * view.CELL_SIZE + 3, paintMap);
                }
                else
                    arrayMap[j][i] = 0;
            }



    }

    // настройки пост эффектов
    public void initPost(Point point) {
        flag = true;    //рисовать эффекты
        post = point;   // где рисовть
        postPaint = new Paint();    //как рисовать
        postPaint.setColor(Color.WHITE);    //цвет
        size = 0;       //начальный размер круга в эффектах
    }

    public void setLevel(int levelNum) {
        String[] level;

        switch (levelNum) {
            case 1:
                level = view.getResources().getStringArray(R.array.level1);
                break;
            case 2:
                level = view.getResources().getStringArray(R.array.level2);
                break;
            case 3:
                level = view.getResources().getStringArray(R.array.level3);
                break;
            case 4:
                level = view.getResources().getStringArray(R.array.level4);
                break;
            case 5:
                level = view.getResources().getStringArray(R.array.level5);
                break;
            case 6:
                level = view.getResources().getStringArray(R.array.level6);
                break;
            case 7:
                level = view.getResources().getStringArray(R.array.level7);
                break;
            case 8:
                level = view.getResources().getStringArray(R.array.level8);
                break;
            case 9:
                level = view.getResources().getStringArray(R.array.level9);
                break;
            case 10:
                level = view.getResources().getStringArray(R.array.level10);
                break;
            default:
                level = view.getResources().getStringArray(R.array.level1);
                break;
        }
        for (int i = 0; i < view.CELLS_VERT; i++)
            for (int j = 0; j < view.CELLS_HOR; j++) {
                arrayMap[j][i] = (byte) Character.getNumericValue(level[i].charAt(j));
            }

        paintMap.setColor(Color.GRAY);
        for (int i = 0; i < view.CELLS_VERT; i++) {
            for (int j = 0; j < view.CELLS_HOR; j++) {
                if (arrayMap[j][i] != 0) {
                    _map.drawRect(j * view.CELL_SIZE - 3, i * view.CELL_SIZE - 3,
                            j * view.CELL_SIZE + 3, i * view.CELL_SIZE + 3, paintMap);
                }
            }
        }
    }

    public void draw(Canvas canvas, int id) {
        switch (id) { // выводить на канвас, в зависимости от id
            case 0: {   // если это игрок
                Paint p = new Paint();
                p.setColor(Color.BLUE); //цвет игрока и следа

                int sizeController = view.player.size;    //размер круга
                view.player.stepNext();         // перемещение согласно направлению
                int x = view.player.getPos().x; //координаты
                int y = view.player.getPos().y;


                canvas.drawCircle(x, y, sizeController / 2, p);   // рисует на главный канвас круг игрока
                if (x % view.CELL_SIZE == 0 && y % view.CELL_SIZE == 0) {
//                if(view.player.getCell() != view.player.getPrevCell()){
                    if (view.player.isCollision() ||
                            view.player.getCell().equals(view.bot.getCell())) {
//                    System.out.println("P GAMEOVER");
                        view.isBotGameover = false;
                        view.setGameover(true);  //если столкнулся - проиграл
                        break;
                    } else
                        arrayMap[view.player.getPrevCell().x]
                                [view.player.getPrevCell().y] = 1;

                    _map.drawRect(x - 3, y - 3, x + 3, y + 3, p);   // прямоугольник следа рисуется в картинку карты
//                _map.drawCircle(x, y, 3, p);
                }
                break;
            }

            case 1: {   // если бот
                Paint p = new Paint();
                p.setColor(Color.RED);      // цвет круга бота

                int sizeController = view.bot.size;
//                view.bot.stepNext();
                view.ai.init();
                int x = view.bot.getPos().x;
                int y = view.bot.getPos().y;


                canvas.drawCircle(x, y, sizeController / 2, p);
                if (x % view.CELL_SIZE == 0 && y % view.CELL_SIZE == 0) {
                    if (view.bot.isCollision()) {
//                    System.out.println("B GAMEOVER");
                        view.isBotGameover = true;
                        view.setGameover(true);
                        break;
                    } else

                        arrayMap[view.bot.getPrevCell().x]
                                [view.bot.getPrevCell().y] = 1;

                    _map.drawRect(x - 3, y - 3, x + 3, y + 3, p);
//                _map.drawCircle(x, y, 3, p);
                }
                break;

            }

            case 9: {   // если эффекты
                if (flag && postPaint.getAlpha() > 1) { // пока прозрачность больше 1
                    _postEffect.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); // очистить картинку эффектов
                    postPaint.setAlpha(postPaint.getAlpha() - 15); // убавить яркость
                    size += 1;  // увеличить размер круга
                    _postEffect.drawCircle(post.x, post.y, size, postPaint); // нарисвать круг
                    canvas.drawBitmap(postEffect, 0, 0, null); // вывести картинку эффектов в главный канвас
                } else if (flag && postPaint.getAlpha() <= 1) { // если прозрачность уже равна 1
                    flag = false; // перестать рисовать эффекты
                }
                break;
            }

            case 10: {  // рисуем карту
                canvas.drawBitmap(map, 0, 0, null);

//                 for (int j = 0; j <= view.CELLS_VERT; j++) {
//                    canvas.drawLine(0, j * view.CELL_SIZE, view.CELL_SIZE * view.CELLS_HOR, j * view.CELL_SIZE, null);
//                    if(j <= view.CELLS_HOR)canvas.drawLine(j * view.CELL_SIZE, 0, j * view.CELL_SIZE, view.CELL_SIZE * view.CELLS_VERT, null);
//                 }

                break;
            }


        }
    }
}
