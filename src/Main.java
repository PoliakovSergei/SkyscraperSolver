import java.util.Scanner;

public class Main {

    public static final int gameSize = 6;

    public static void main(String[] args) {

        Scanner inputScanner = new Scanner(System.in);
        String[] constraintsArray = inputScanner.nextLine().split(",");
        int[][] skyskraperCity = new int[gameSize][gameSize];
        /* Шаг 1
         * Ищем в ограничениях gameSize, что позволит полностью заполнить строку/столбец
         * Ищем в ограничениях 1, что позволить заполнить граничную ячейку значением gameSize
         */
        for (int i = 0; i < constraintsArray.length; i++) {
            if (Integer.valueOf(constraintsArray[i]) == gameSize) {
                fillTheRowOrColumn(skyskraperCity, i);
            } else if (Integer.valueOf(constraintsArray[i]) == 1) {
                fillTheNearConstraintCellWithMaxValue(skyskraperCity, i);
            }
        }
        /* Шаг 2
         * Поиск с возвратом
         */
        backTrackAlgorithm(skyskraperCity, constraintsArray);

        // Вывод ответа
        for (int row = 0; row < gameSize; row++) {
            for (int column = 0; column < gameSize; column++) {
                System.out.print(skyskraperCity[row][column]);
                if ((row != gameSize - 1) || (column != gameSize - 1)) {
                    System.out.print(",");
                }
            }
        }

    }

    public static boolean backTrackAlgorithm(int[][] skyskraperArray, String[] constraints) {
        // Поиск свободной ячейки
        Pair<Integer, Integer> emptyCell = findEmptyCell(skyskraperArray);
        if (emptyCell == null) {
            return true;
        }
        // Заполнение ячеек
        for (int i = 1; i <= gameSize; i++) {
            skyskraperArray[emptyCell.getFirst()][emptyCell.getSecond()] = i;
            // Проверка конфликтов
            if (checkConflicts(skyskraperArray, constraints, emptyCell)) {
                if (backTrackAlgorithm(skyskraperArray, constraints)) {
                    return true;
                } else {
                    skyskraperArray[emptyCell.getFirst()][emptyCell.getSecond()] = 0;
                }
            } else {
                skyskraperArray[emptyCell.getFirst()][emptyCell.getSecond()] = 0;
            }
        }
        return false;
    }

    public static boolean checkConflicts(int[][] array, String[] constraints, Pair<Integer, Integer> cellToCheck) {
        if (checkRowConflicts(array, cellToCheck)
                && checkColumnConflicts(array, cellToCheck)
                && checkConstraintsConflicts(array, constraints)) {
            return true;
        }
        return false;
    }

    public static boolean checkRowConflicts(int[][] array, Pair<Integer, Integer> cellToCheck) {
        for (int i = 0; i < Main.gameSize; i++) {
            if (i != cellToCheck.getSecond()
                    && array[cellToCheck.getFirst()][i] == array[cellToCheck.getFirst()][cellToCheck.getSecond()]) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkColumnConflicts(int[][] array, Pair<Integer, Integer> cellToCheck) {
        for (int i = 0; i < Main.gameSize; i++) {
            if (i != cellToCheck.getFirst()
                    && array[i][cellToCheck.getSecond()] == array[cellToCheck.getFirst()][cellToCheck.getSecond()]) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkConstraintsConflicts(int[][] array, String[] constraints) {
        for(int i = 0; i < constraints.length; i++){
            int buildingsView = 0;
            int highterBuilding = 0;
            Thriple<Integer, Integer, Direction> startCellAndDirection = getNearestCellToConstraint(i);
            switch (startCellAndDirection.getThird()) {
                case UP:        // Проверка снизу вверх
                    for (int j = Main.gameSize - 1; j >= 0; j--) {
                        if (array[j][startCellAndDirection.getSecond()] > highterBuilding) {
                            highterBuilding = array[j][startCellAndDirection.getSecond()];
                            buildingsView++;
                        }
                        if(array[j][startCellAndDirection.getSecond()] == 0) {
                            return true;
                        }
                    }
                    if (Integer.valueOf(constraints[(Main.gameSize * 3) - startCellAndDirection.getSecond() - 1]) != 0
                            && Integer.valueOf(constraints[(Main.gameSize * 3) - startCellAndDirection.getSecond() - 1]) != buildingsView) {
                        return false;
                    }
                    break;
                case DOWN:        // Проверка сверху вниз
                    for (int j = 0; j < Main.gameSize; j++) {
                        if (array[j][startCellAndDirection.getSecond()] > highterBuilding) {
                            highterBuilding = array[j][startCellAndDirection.getSecond()];
                            buildingsView++;
                        }
                        if(array[j][startCellAndDirection.getSecond()] == 0) {
                            return true;
                        }
                    }
                    if (Integer.valueOf(constraints[startCellAndDirection.getSecond()]) != 0
                            && Integer.valueOf(constraints[startCellAndDirection.getSecond()]) != buildingsView) {
                        return false;
                    }
                    break;
                case LEFT:      // Проверка справа налево
                    for (int j = Main.gameSize - 1; j >= 0; j--) {
                        if (array[startCellAndDirection.getFirst()][j] > highterBuilding) {
                            highterBuilding = array[startCellAndDirection.getFirst()][j];
                            buildingsView++;
                        }
                        if(array[startCellAndDirection.getFirst()][j] == 0) {
                            return true;
                        }
                    }
                    if (Integer.valueOf(constraints[startCellAndDirection.getFirst() + Main.gameSize]) != 0
                            && Integer.valueOf(constraints[startCellAndDirection.getFirst() + Main.gameSize]) != buildingsView) {
                        return false;
                    }

                    break;
                case RIGHT:     // Проверка слева направо
                    for (int j = 0; j < Main.gameSize; j++) {
                        if (array[startCellAndDirection.getFirst()][j] > highterBuilding) {
                            highterBuilding = array[startCellAndDirection.getFirst()][j];
                            buildingsView++;
                        }
                        if(array[startCellAndDirection.getFirst()][j] == 0) {
                            return true;
                        }
                    }
                    if (Integer.valueOf(constraints[(Main.gameSize * 4) - startCellAndDirection.getFirst() - 1]) != 0
                            && Integer.valueOf(constraints[(Main.gameSize * 4) - startCellAndDirection.getFirst() - 1]) != buildingsView) {
                        return false;
                    }
                    break;
            }
        }
        //
        return true;
    }

    public static Pair<Integer, Integer> findEmptyCell(int[][] array) {
        for (int row = 0; row < gameSize; row++) {
            for (int column = 0; column < gameSize; column++) {
                if (array[row][column] == 0) {
                    return new Pair<>(row, column);
                }
            }
        }
        return null;
    }

    public static int[][] fillTheRowOrColumn(int[][] array, int constraintNum) {
        Thriple<Integer, Integer, Direction> startCellAndDirection = getNearestCellToConstraint(constraintNum);
        switch (startCellAndDirection.getThird()) {
            case UP:
                for (int i = gameSize - 1; i >= 0; i--) {
                    array[i][startCellAndDirection.getSecond()] = (gameSize - i);
                }
                break;
            case DOWN:
                for (int i = 0; i < gameSize; i++) {
                    array[i][startCellAndDirection.getSecond()] = i + 1;
                }
                break;
            case LEFT:
                for (int i = gameSize - 1; i >= 0; i--) {
                    array[startCellAndDirection.getFirst()][i] = (gameSize - i);
                }
                break;
            case RIGHT:
                for (int i = 0; i < gameSize; i++) {
                    array[startCellAndDirection.getFirst()][i] = i + 1;
                }
                break;
        }
        return array;
    }

    public static int[][] fillTheNearConstraintCellWithMaxValue(int[][] array, int constraintNum) {
        Thriple<Integer, Integer, Direction> cellToFill = getNearestCellToConstraint(constraintNum);
        array[cellToFill.getFirst()][cellToFill.getSecond()] = Main.gameSize;
        return array;
    }

    public static Thriple<Integer, Integer, Direction> getNearestCellToConstraint(int constraintID) {

        switch (constraintID / gameSize) {
            case 0: // Сверху вниз
                return new Thriple<>(0, constraintID % gameSize, Direction.DOWN);
            case 1: // Справа налево
                return new Thriple<>(constraintID % gameSize, gameSize - 1, Direction.LEFT);
            case 2: // Снизу вверх
                return new Thriple<>(gameSize - 1, (gameSize - 1) - constraintID % gameSize, Direction.UP);
            case 3: // Слева направо
                return new Thriple<>((gameSize - 1) - constraintID % gameSize, 0, Direction.RIGHT);
            default:
                return null;
        }
    }
}

class Pair<F, S> {
    private F first;
    private S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public void setSecond(S second) {
        this.second = second;
    }
}

class Thriple<F, S, T> extends Pair<F, S> {
    private T third;

    public Thriple(F first, S second, T third) {
        super(first, second);
        this.third = third;
    }

    public T getThird() {
        return third;
    }

    public void setThird(T third) {
        this.third = third;
    }
}

enum Direction {
    DOWN,
    UP,
    LEFT,
    RIGHT
}