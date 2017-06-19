package tzpp.model;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import tzpp.controller.Common;
import tzpp.model.graphModel.Edge;
import tzpp.model.graphModel.Node;
import tzpp.model.graphModel.NodeCircle;

import java.util.ArrayList;

public class MethodOfPotentials {
    private GraphicsContext gcTable;             // графічний контекст поля для таблиць
    private GraphicsContext gcGraph;             // графічний контекст поля для графу
    private Canvas canvasForGraph;
    private Double fieldWidth;              // ширина графічної частини
    private Double fieldHeight;             // висота графічної частини
    private Integer size;                   // розмір квадратика
    private Integer colsCount;            // кількість колонок
    private Integer rowsCount;              // кількість рядків
    private Integer cols;            // ітератор колонок
    private Integer rows;              // ітератор рядків

    private ArrayList<Node> nodesA;         // Виробники
    private ArrayList<Node> nodesB;         // Споживачі
    private int N;                          // кількість виробників
    private int M;                          // кількість споживачів
    private ArrayList<Double> a;            // запаси виробників
    private ArrayList<Double> b;            // потреби споживачів

    private static TableCell[][] C;         // кожна клітинка таблиці (вартість)
    private static Double[][] X;            // план перевезень
    private static Double[][] D;            // оцінки небазисних змінних (базисні нульові)

    private Double[] u;                    // потенціали виробників
    private Double[] v;                    // потенціали споживачів
    private Double F;                       // цільова функція

    private Boolean isOptimal;
    private Boolean ok;
    private static Boolean[][] T;           // зберігає місця, де вже були нулі (при виродженості)

    private float counter;          // контролює глибину рекурсії
    private Integer iteration;      // рахує ітерації

    // допоміжні масиви при визначенні потенціалів
    private Boolean[] ub;
    private Boolean[] vb;

    // Змінні, необхідні для пошуку циклу
    private ArrayList<Integer> l1;
    private ArrayList<Integer> l2;
    private int i1, j1;

    // Координати змінних, що вводяться та виводяться з базису
    private Pair<Integer, Integer> toBasis;
    private Pair<Integer, Integer> fromBasis;

    private TZLPmodel tzlpModel;

    public MethodOfPotentials(MPZKmodel mpzk, Canvas canvasForTable, Canvas canvasForGraph) {
        gcTable = canvasForTable.getGraphicsContext2D();
        fieldWidth = canvasForTable.getWidth();
        fieldHeight = canvasForTable.getHeight();
        gcGraph = canvasForGraph.getGraphicsContext2D();
        gcGraph.clearRect(0, 0, canvasForGraph.getWidth(), canvasForGraph.getHeight());
        gcTable.clearRect(0, 0, fieldWidth, fieldHeight);
        nodesA = mpzk.getTZLP().getOutputNodes();
        nodesB = mpzk.getTZLP().getInputNodes();
        N = nodesA.size();
        M = nodesB.size();
        a = mpzk.getTZLP().getOutputNodesResource();
        b = mpzk.getTZLP().getInputNodesResource();
        C = mpzk.getTZLP().getTableTZLP();
        X = mpzk.getX();
        D = new Double[N][M];
        u = new Double[nodesA.size()];
        v = new Double[nodesB.size()];
        F = mpzk.getF();
        isOptimal = false;
        ok = false;
        T = new Boolean[N][M];
        iteration = 0;
        size = mpzk.getTZLP().getSize();
        l1 = new ArrayList<>();
        l2 = new ArrayList<>();
        colsCount = 0;
        rowsCount = 0;
        cols = 0;
        rows = 0;
        Common.setGc(gcTable);
        tzlpModel = mpzk.getTZLP();
    }

    // Старт метода потенциалов
    public void findAndDraw() {
        drawBorders();


        while (!isOptimal) {
            // Під час визначення оцінок небазисних змінних ця змінна може набути хибного значення
            isOptimal = true;
            // Знайти потенціали
            findPotentials();

            // Знайти оцінки небазисних змінних та максимальну серед них
            Pair<NodeCircle, Double> maxD = findMaxD();
            // Якщо рішення не оптимальне (а отже існує така додатня клітинка)
            if (!isOptimal) {
                System.out.println(maxD.getKey().getX() + " " + maxD.getKey().getY() + " " + maxD.getValue());
                // Визначаємо координати початку циклу
                i1 = (int) maxD.getKey().getX();
                j1 = (int) maxD.getKey().getY();
                toBasis = new Pair<>(i1, j1);
                // Шукаємо цикл
                if (findCicle(i1, j1)) {
                    System.out.println(l1.toString() + " \n" + l2.toString());
                    drawColumnsHeader();
                    drawRowsHeader();
                    drawTable();
                    if (cols >= colsCount - 2) {
                        cols = 0;
                        rows++;
                    } else cols++;
                    iteration++;
                    // вводимо то виводимо змінні з базису, перераховуємо цикл
                    reCalculate();
                } else {
                    System.out.println("Цикл не знайдено");
                    break;
                }
            } else {
                System.out.println("Знайдено оптимальне рішення");
                if (iteration > ((colsCount) * (rowsCount) - colsCount - rowsCount)) {
                    cols = 0;
                    rows = 0;
                    gcTable.clearRect(0, 0, fieldWidth, fieldHeight);
                }
                drawColumnsHeader();
                drawRowsHeader();
                drawTable();
            }
        }
    }

    // Перераховує змінні, що належать циклу
    private void reCalculate() {
        fromBasis = null;
        if (l1.size() != l2.size()) System.out.println("Дані введені некоректно");
        if (l1.size() < 4) System.out.println("Цикл має менше чотирьох елементів");
        Double difference = null;
        Boolean isPlus = true;
        for (int k = 0; k < l1.size(); k++) {
            int i = l1.get(k);
            int j = l2.get(k);
            if (!isPlus) {
                Double volume = X[i][j];
                if (difference == null) {
                    difference = volume;
                    fromBasis = new Pair<>(i, j);
                } else if (volume < difference) {
                    fromBasis = new Pair<>(i, j);
                    difference = volume;
                }
                isPlus = true;
            } else isPlus = false;
        }
        if (difference == null) System.out.println("Не вдалося визначити змінну, що виводиться з базису");
        else {
            isPlus = true;
            X[i1][j1] = 0.0;
            for (int i = 0; i < l1.size(); i++) {
                if (isPlus) {
                    X[l1.get(i)][l2.get(i)] = X[l1.get(i)][l2.get(i)] + difference;
                    isPlus = false;
                } else {
                    X[l1.get(i)][l2.get(i)] = X[l1.get(i)][l2.get(i)] - difference;
                    isPlus = true;
                }
            }
            X[fromBasis.getKey()][fromBasis.getValue()] = null;
        }
    }


    // Знаходимо Ui та Vj (трохи надлишковий метод - забагато циклів)
    private void findPotentials() {
        isOptimal = true;
        ub = new Boolean[u.length];     // допоміжні змінні
        vb = new Boolean[v.length];

        for (int i = 0; i < ub.length; i++) ub[i] = false;
        for (int i = 0; i < vb.length; i++) vb[i] = false;
        u[0] = 0.0;                       // перший потенціал нульовий
        ub[0] = true;
        int count = 1;
        int tmp = 0;
        do {
            for (int i = 0; i < N; i++)
                if (ub[i])
                    for (int j = 0; j < M; j++)
                        if (X[i][j] != null && !vb[j]) {
                            v[j] = C[i][j].getCosts() - u[i];
                            vb[j] = true;
                            count++;
                        }
            for (int j = 0; j < M; j++)
                if (vb[j])
                    for (int i = 0; i < N; i++)
                        if (X[i][j] != null && !ub[i]) {
                            u[i] = C[i][j].getCosts() - v[j];
                            ub[i] = true;
                            count++;
                        }
            tmp++;
        } while ((count < (M + N)) && (tmp < M * N));
    }

    // пошук циклу
    private Boolean findCicle(int i0, int j0) {
        counter = M * N; // максимальне число ітерацій
        l1.clear();
        l2.clear();
        return (lookHorizontally(i0, j0));
    }

    // шукаємо шлях по горизонталі
    private Boolean lookHorizontally(int i0, int j0) {
        counter -= 1;
        if (counter == 0) {
            System.out.println("Занадто багато ітерацій при пошуці циклу");
            return false;
        }
        for (int j = 0; j < M; j++) {
            if (j == j0) continue;
            if (X[i0][j] == null) continue;
            if (lookVertically(i0, j)) {
                l1.add(i0);
                l2.add(j);
                return true;
            }
        }
        return false; // цикл не знайдено
    }

    // шукаємо шлях по вертикалі
    private Boolean lookVertically(int i0, int j0) {
        for (int i = 0; i < N; i++) {
            if (j0 == j1 && i == i1) {
                l1.add(i);
                l2.add(j0);
                return true; // цикл закінчено
            }
            if (i == i0) continue;
            if (X[i][j0] == null) continue;
            if (lookHorizontally(i, j0)) {
                l1.add(i);
                l2.add(j0);
                return true;
            }
        }
        return false; // цикл не знайдено
    }

    // Знаходимо оцінки небазисних змінних
    private Pair<NodeCircle, Double> findMaxD() {
        Pair<NodeCircle, Double> maxD = new Pair<>(new NodeCircle(0, 0), 0.0);

        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++) {
                // Якщо клітинка не вільна
                if (X[i][j] != null) D[i][j] = 0.0;
                else D[i][j] = u[i] + v[j] - C[i][j].getCosts();       // знаходимо оцінку
                if (D[i][j] > 0) {
                    isOptimal = false;                              // ознака оптимальності порушена
                    if (D[i][j] >= maxD.getValue()) {
                        maxD = new Pair<>(new NodeCircle(i, j), D[i][j]);
                    }
                }
            }
        return maxD;
    }

    // Малює клітинки
    private void drawBorders() {
        gcTable.setStroke(Color.BLACK);
        gcTable.setLineWidth(2);
        Double i = 0.0;
        Double j = 0.0;
        while (i < fieldWidth) {
            gcTable.strokeLine(i, 0, i, fieldHeight);
            i += size * (b.size() + 6);
            colsCount++;
        }
        while (j < fieldHeight) {
            gcTable.strokeLine(0, j, fieldWidth, j);
            j += size * (a.size() + 6);
            rowsCount++;
        }
    }

    // Малює заголовок рядків таблиці
    private void drawRowsHeader() {
        Common.setGc(gcTable);
        int dx = cols * (b.size() + 6);
        int dy = rows * (a.size() + 6);
        int i = 3;
        for (Node node : nodesA) {
            node.draw(gcTable, size, Color.LIGHTBLUE, dx + 1, dy + i);
            Common.drawCell(Color.LIGHTGREEN, dx + 2, dy + i++, u[i - 4].toString());
        }
    }

    // Малює заголовок колонок таблиці
    private void drawColumnsHeader() {
        Common.setGc(gcTable);
        int dx = cols * (b.size() + 6);
        int dy = rows * (a.size() + 6);
        int i = 3;
        gcTable.setLineWidth(10);
        gcTable.fillText(("Ітерація # " + iteration), dx * size + 40, (dy) * size + 80);

        for (Node node : nodesB) {
            node.draw(gcTable, size, Color.LIGHTBLUE, dx + i, dy + 1);
            Common.drawCell(Color.LIGHTGREEN, dx + i++, dy + 2, v[i - 4].toString());
        }
    }

    // Малює матрицю вартостей та оцінок
    private void drawTable() {
        Common.setGc(gcTable);
        int dx = cols * (b.size() + 6);
        int dy = rows * (a.size() + 6);
        for (int i = 0; i < X.length; i++) {
            for (int j = 0; j < X[0].length; j++) {
                if (X[i][j] == null) {
                    Common.drawCellWithTwoText(Color.LIGHTYELLOW, dx + j + 3, dy + i + 3,
                            C[i][j].getCosts().toString(), D[i][j].toString());
                } else {
                    Common.drawCellWithTwoText(Color.LIGHTPINK, dx + j + 3, dy + i + 3,
                            C[i][j].getCosts().toString(), X[i][j].toString());
                }
            }
        }

        gcTable.fillText(getTransportCost(), (dx * size) + 40, (dy + X[0].length + 4) * size);
    }

    // Підрахунок транспортних витрат
    private String getTransportCost() {
        Common.setGc(gcGraph);
        String summString = "Транспортні витрати :\n";
        Double dbrSum = 0.0;
        int iter = 0;
        for (int i = 0; i < X[0].length; i++)
            for (int j = 0; j < X[1].length; j++) {
                if (X[i][j] != null) {
                    Double currentSum = C[i][j].getCosts() * X[i][j];
                    summString += (currentSum.toString()) + " + ";
                    if (iter > 6) {
                        summString += "\n";
                        iter = 0;
                    }
                    iter++;
                    dbrSum += currentSum;
                }
            }
        int index = summString.length() - 3;
        return summString.substring(0, index) + " = " + dbrSum;
    }

    public void drawNodes(ArrayList<Node> nodes) {
        Common.setGc(gcGraph);
//        gcGraph.clearRect(0, 0, canvasForGraph.getWidth(), canvasForGraph.getHeight());
        for (Node node : nodes) {
            Common.determineColors(node.getType());
            Common.drawOval(node);
        }

    }

    public void drawEdges(ArrayList<Edge> edges) {
        Common.setGc(gcGraph);
//        for (Edge edge : edges) {
//            // Получаем id первого узла
//            Integer firstId = edge.getFirstNode().getId();
//            // Ищем индекс в массиве элементов
//            for (int i = 0; i < nodesA.size(); i++) {
//                // Если он существует
//                if (nodesA.get(i).getId().equals(firstId)) {
//                    // Получаем id второго узла
//                    Integer secondId = edge.getSecondNode().getId();
//                    if (!firstId.equals(secondId)) {
//                        // Ищем индекс в массиве элементов
//                        for (int j = 0; j < nodesB.size(); j++) {
//                            // Если такой существует
//                            if (X[i][j] != 0.0)
//                                if (nodesB.get(j).getId().equals(secondId)) {
//                                    // то рисуем
//                                    Double x1 = edge.getFirstNode().getNodeCircle().getX();
//                                    Double y1 = edge.getFirstNode().getNodeCircle().getY();
//                                    Double x2 = edge.getSecondNode().getNodeCircle().getX();
//                                    Double y2 = edge.getSecondNode().getNodeCircle().getY();
//                                    String str = "[ " + X[i][j].toString() + " - " + C[i][j].getCosts().toString() + " ]";
//                                    Common.drawArrow(x1, y1, x2, y2, str);
//                                    break;
//                                }
//                        }
//                        break;
//                    }
//                }
//            }
//        }

      for(int i = 0; i < nodesA.size(); i++){
          for (int j = 0; j < nodesB.size(); j++){
              Node node1 = nodesA.get(i);
              Node node2 = nodesB.get(j);
//              edges.stream().filter(n -> n.getFirstNode().equals(node1) && n.getSecondNode().equals(node2)).findFirst().orElseGet(() -> null);
              Double x1 = node1.getNodeCircle().getX();
              Double y1 = node1.getNodeCircle().getY();
              Double x2 = node2.getNodeCircle().getX();
              Double y2 = node2.getNodeCircle().getY();
              if(X[i][j] != null && C[i][j].getCosts() > 0){
                  String str = "[ " + X[i][j].toString() + " - " + C[i][j].getCosts().toString() + " ]";
                  Common.drawArrow(x1,y1,x2,y2,str);
              }

          }
      }
    }

}


