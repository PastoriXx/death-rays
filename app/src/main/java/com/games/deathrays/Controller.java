package com.games.deathrays;

import android.graphics.Point;

public class Controller {
    GameView view;
    Point pos, curCell, prevCell;      // позиция объекта
    int speed = 2;  // скорость
    int stepCount = 0;  // счетчик шагов
//    int cellSize = 8;  // размер ячейки сетки

    int size,       //размер
            nextDirection,
            direction;     // направление
    int id;         // id объекта

    public Controller(int id, final GameView view) {
        this.view = view;

        this.id = id;
        size = 16;
        pos = new Point();
        prevCell = new Point();
        curCell = new Point();
        startPos();     // начальные установки для объекта
    }

    protected void setSpeed(int speed) {
        this.speed = speed;
    }

    private void startPos() {
        switch (id) { // установки в зависимости от id
            case 0:
                pos.x = (view.width / 2);
                pos.y = view.height - 4 * view.CELL_SIZE;
                curCell = getCell();
                direction = 0;
                break;
            case 1:
                pos.x = (view.width / 2);
                pos.y = 4 * view.CELL_SIZE;
                curCell = getCell();
                direction = 2;
                break;
        }
        nextDirection = direction;
    }

    public void setDirection(int direction) {
        if (direction != (this.direction + 2) % 4) // если направление не противоположно старому
            nextDirection = direction;
    }

    public int getId() {
        return id;
    }

    protected void setPosition() {

        switch (direction) {
            case 0: //вверх
                pos.y -= speed;
                break;
            case 3: //вправо
                pos.x += speed;
                break;
            case 2: //вниз
                pos.y += speed;
                break;
            case 1: //влево
                pos.x -= speed;
                break;
            default:
                break;
        }
    }

    protected Point getPrevCell() {
        return prevCell;
    }

    //получить позицию объекта. для других классов в основном
    protected Point getPos() {
        return new Point(pos.x, pos.y);
    }

    protected Point getCell() {
        return new Point(pos.x / view.CELL_SIZE, pos.y / view.CELL_SIZE);
    }

    protected void setPos(Point pos) {
        this.pos = pos;
    }

    // сделать перемещение согласно направлению и вернуть координаты
    protected Point stepNext() {
        if (getCell() != curCell) {
            prevCell = curCell;
            curCell = getCell();
        }
        if (pos.x % view.CELL_SIZE == 0
                && pos.y % view.CELL_SIZE == 0) {
            stepCount++;
            if (nextDirection != direction) {
                direction = nextDirection;
                if (id == 0)
                    view.graphics.initPost(getPos()); // рисовать эффект если был поворот
            }
        }
        setPosition();

        return getPos();
    }

    // метод получение позиции объекта через n шагов в заданном направлении
    protected Point getPosAfter(int n) {
        Point tmp = new Point(pos.x, pos.y); // запоминаем позицию объекта
        Point result = new Point();
        for (int i = 0; i < n; i++)
            result = stepNext(); //делаем n шагов и запоминаем
        pos.x = tmp.x; // восстанавливаем позицию объекта
        pos.y = tmp.y;

        return result;
    }

    protected Point getNextCell(int dir) {
        switch (dir) {
            case 0:
                return new Point(getCell().x, getCell().y - 1);
            case 1:
                return new Point(getCell().x - 1, getCell().y);
            case 2:
                return new Point(getCell().x, getCell().y + 1);
            case 3:
                return new Point(getCell().x + 1, getCell().y);
        }
        return null;
    }


    // определяет столкновение
    public boolean isCollision() {
        boolean result = false;
        if (getPos().x / view.CELL_SIZE < 1
                || getPos().x / view.CELL_SIZE > view.CELLS_HOR - 1
                || getPos().y / view.CELL_SIZE < 1
                || getPos().y / view.CELL_SIZE > view.CELLS_VERT - 1) result = true;
        else if (view.graphics.arrayMap[getPos().x / view.CELL_SIZE]
                [getPos().y / view.CELL_SIZE] != 0) result = true;
        return result;
    }
}
