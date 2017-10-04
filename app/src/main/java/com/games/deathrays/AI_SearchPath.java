package com.games.deathrays;

import android.graphics.Point;

import java.util.LinkedList;


class Cell {
    /**
     * Создает клетку с координатами x, y.
     *
     * @param blocked является ли клетка непроходимой
     */
    public Cell(int x, int y, boolean blocked) {
        this.x = x;
        this.y = y;
        this.blocked = blocked;
    }

    /**
     * Функция вычисления манхеттенского расстояния от текущей
     * клетки до finish
     *
     * @param finish конечная клетка
     * @return расстояние
     */
    public int mandist(Cell finish) {
        return 10 * (Math.abs(this.x - finish.x) + Math.abs(this.y - finish.y));
    }

    /**
     * Вычисление стоимости пути до соседней клетки finish
     *
     * @param finish соседняя клетка
     * @return 10, если клетка по горизонтали или вертикали от текущей, 14, если по диагонали
     * (это типа 1 и sqrt(2) ~ 1.44)
     */
    public int price(Cell finish) {
        if (this.x == finish.x || this.y == finish.y) {
            return 10;
        } else {
            return 14;
        }
    }

    /**
     * Устанавливает текущую клетку как стартовую
     */
    public void setAsStart() {
        this.start = true;
    }

    /**
     * Устанавливает текущую клетку как конечную
     */
    public void setAsFinish() {
        this.finish = true;
    }

    /**
     * Сравнение клеток
     *
     * @param second вторая клетка
     * @return true, если координаты клеток равны, иначе - false
     */
    public boolean equals(Cell second) {
        return (this.x == second.x) && (this.y == second.y);
    }

    public int x = -1;
    public int y = -1;
    public Cell parent = this;
    public boolean blocked = false;
    public boolean start = false;
    public boolean finish = false;
    public boolean road = false;
    public int F = 0;
    public int G = 0;
    public int H = 0;
}

class Table<T extends Cell> {

    /**
     * Создаем карту игры с размерами width и height
     */
    public Table(int width, int height) {
        this.width = width;
        this.height = height;
        this.table = new Cell[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                table[i][j] = new Cell(0, 0, false);
            }
        }
    }

    /**
     * Добавить клетку на карту
     */
    public void add(Cell cell) {
        table[cell.x][cell.y] = cell;
    }

    /**
     * Получить клетку по координатам x, y
     *
     * @return клетка, либо фейковая клетка, которая всегда блокирована (чтобы избежать выхода за границы)
     */
    @SuppressWarnings("unchecked")
    public T get(int x, int y) {
        if (x < width && x > 0 && y < height && y > 0) {
            return (T) table[x][y];
        }
        // а разве так можно делать в Java? оО но работает оО
        return (T) (new Cell(0, 0, true));
    }

    public int width;
    public int height;
    private Cell[][] table;
}

public class AI_SearchPath {

    /**
     * @param startPoint точка отправления
     * @param finishPoint точка цели
     * @param cellSize размеры ячейки сетки
     * @param gridWidth ширина сетки
     * @param gridHeight высота сетки
     * @param map байтовый массив данных карты
     */
    public boolean noroute = false;
    public LinkedList<Point> aStar(Point startPoint, final Point finishPoint,
                                           int cellSize, final int gridWidth, final int gridHeight,
                                           byte map[][]) {

//        startPoint.x /= cellSize;
//        startPoint.y /= cellSize;
//
//        finishPoint.x /= cellSize;
//        finishPoint.y /= cellSize;


        // Создадим все нужные списки
        Table<Cell> cellList = new Table<Cell>(gridWidth, gridHeight);
        Table blockList = new Table(gridWidth, gridHeight);
        LinkedList<Cell> openList = new LinkedList<Cell>();
        LinkedList<Cell> closedList = new LinkedList<Cell>();
        LinkedList<Cell> tmpList = new LinkedList<Cell>();

        // Создадим преграду
        for (int i = 0; i < gridWidth; i++)
            for (int j = 0; j < gridHeight; j++) {
                if (map[i][j] > 0)
                    blockList.add(new Cell(i, j, true));
            }


        // Заполним карту как-то клетками, учитывая преграду
        for (int i = 0; i < gridHeight; i++) {
            for (int j = 0; j < gridWidth; j++) {
                cellList.add(new Cell(j, i, blockList.get(j, i).blocked));
            }
        }

        // Стартовая и конечная
        cellList.get(startPoint.x, startPoint.y).setAsStart();
        cellList.get(finishPoint.x, finishPoint.y).setAsFinish();
        Cell start = cellList.get(startPoint.x, startPoint.y);
        Cell finish = cellList.get(finishPoint.x, finishPoint.y);

        // Фух, начинаем
        boolean found = false;
        noroute = false;

        //1) Добавляем стартовую клетку в открытый список.
        openList.push(start);

        //2) Повторяем следующее:
        while (!found && !noroute) {
            //a) Ищем в открытом списке клетку с наименьшей стоимостью F. Делаем ее текущей клеткой.
            Cell min = openList.getFirst();
            for (Cell cell : openList) {
                // тут я специально тестировал, при < или <= выбираются разные пути,
                // но суммарная стоимость G у них совершенно одинакова. Забавно, но так и должно быть.
                if (cell.F < min.F) min = cell;
            }

            //b) Помещаем ее в закрытый список. (И удаляем с открытого)
            closedList.push(min);
            openList.remove(min);
            //System.out.println(openList);

            //c) Для каждой из соседних 4х клеток ...
            tmpList.clear();

            tmpList.add(cellList.get(min.x, min.y - 1));
            tmpList.add(cellList.get(min.x + 1, min.y));
            tmpList.add(cellList.get(min.x, min.y + 1));
            tmpList.add(cellList.get(min.x - 1, min.y));

            for (Cell neightbour : tmpList) {
                //Если клетка непроходимая или она находится в закрытом списке, игнорируем ее. В противном случае делаем следующее.
                if (neightbour.blocked || closedList.contains(neightbour)) continue;

                //Если клетка еще не в открытом списке, то добавляем ее туда. Делаем текущую клетку родительской для это клетки. Расчитываем стоимости F, G и H клетки.
                if (!openList.contains(neightbour)) {
                    openList.add(neightbour);
                    neightbour.parent = min;
                    neightbour.H = neightbour.mandist(finish);
                    neightbour.G = neightbour.price(min);
                    neightbour.F = neightbour.H + neightbour.G;
                    continue;
                }

                // Если клетка уже в открытом списке, то проверяем, не дешевле ли будет путь через эту клетку. Для сравнения используем стоимость G.
                if (neightbour.G < min.G + neightbour.price(min)) {
                    // Более низкая стоимость G указывает на то, что путь будет дешевле. Эсли это так, то меняем родителя клетки на текущую клетку и пересчитываем для нее стоимости G и F.
                    neightbour.parent = min.parent; // вот тут я честно хз, надо ли min.parent или нет, вроде надо
                    neightbour.H = neightbour.mandist(finish);
                    neightbour.G = neightbour.price(min);
                    neightbour.F = neightbour.H + neightbour.G;
                }

                // Если вы сортируете открытый список по стоимости F, то вам надо отсортировать свесь список в соответствии с изменениями.
            }

            //d) Останавливаемся если:
            //Добавили целевую клетку в открытый список, в этом случае путь найден.
            //Или открытый список пуст и мы не дошли до целевой клетки. В этом случае путь отсутствует.

            if (openList.contains(finish)) {
                found = true;
            }

            if (openList.isEmpty()) {
                noroute = true;
            }
        }

        //3) Сохраняем путь. Двигаясь назад от целевой точки, проходя от каждой точки к ее родителю до тех пор, пока не дойдем до стартовой точки. Это и будет наш путь.
        LinkedList<Point> result = new LinkedList<Point>();
        if (!noroute) {
            Cell rd = finish.parent;
            while (!rd.equals(start)) {
                rd.road = true;
                result.push(new Point(rd.x, rd.y));
                rd = rd.parent;
                if (rd == null) break;
            }
        } else {
//            System.out.println("Путь не найден");
        }
        return result;
    }
}
