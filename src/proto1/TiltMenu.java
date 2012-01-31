/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proto1;

import java.util.ArrayList;
import java.util.Collection;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.*;

/**
 *
 * @author jordane
 */
public class TiltMenu {

    private double initialAngle;
    private int nbPartition = 0;
    private double totalAngle;
    private double length;
    private ArrayList<Quarter> qArray;
    private Line line;
    private Group totalG;

    public enum Type {

        SIZE, OPACITY, CANCEL
    };

    public TiltMenu(double initialAngle, double totalAngle, double length) {
        qArray = new ArrayList<>();
        line = new Line();
        line.setStroke(Color.GOLD);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        line.setStrokeWidth(5);
        this.length = length;
        this.totalG = new Group();
        this.initialAngle = initialAngle;
        this.totalAngle = totalAngle;

    }

    public void addItem(Type... t) {
        nbPartition = t.length;
        qArray.removeAll(qArray);
        totalG.getChildren().removeAll(totalG.getChildren());

        double tetaQuart = totalAngle / nbPartition;

        for (int i = 0; i < nbPartition; i++) {
            double tetaG = initialAngle - tetaQuart * i;
            qArray.add(new Quarter(
                    length * Math.cos(tetaG),
                    -length * Math.sin(tetaG),
                    (length / Math.cos(tetaQuart / 2)) * Math.cos(tetaG - tetaQuart / 2),
                    -(length / Math.cos(tetaQuart / 2)) * Math.sin(tetaG - tetaQuart / 2),
                    length * Math.cos(tetaG - tetaQuart),
                    -length * Math.sin(tetaG - tetaQuart)));
            totalG.getChildren().add(qArray.get(i).getPath());
            switch (t[i]) {
                case OPACITY:
                    qArray.get(i).setType(Type.OPACITY);
                    break;
                case SIZE:
                    qArray.get(i).setType(Type.SIZE);
                    break;
                case CANCEL:
                    qArray.get(i).setType(Type.CANCEL);
                    break;
            }
        }
        totalG.getChildren().add(line);


    }

    public void setIndicator(double angle) {

        line.setStartX(0);
        line.setStartY(0);

        line.setEndX((length + 10) * Math.cos(angle));
        line.setEndY((-length + 10) * Math.sin(angle));
        double tetaQuart = totalAngle / nbPartition;


        for (int i = 0; i < qArray.size(); i++) {
            double tetaG = initialAngle - tetaQuart * i;
            if (angle < tetaG && angle > tetaG - tetaQuart) {
                qArray.get(i).illuminate(true);
            } else {
                qArray.get(i).illuminate(false);
            }

        }

    }

    public void setPosition(double x, double y) {
        totalG.setLayoutX(x);
        totalG.setLayoutY(y);
    }

    public Group getMenu() {
        return totalG;
    }

    private class Quarter {

        private double radius;
        private double length;
        public boolean illuminated;
        private Type type;
        private Path p;
        private QuadCurveTo q;
        Stop[] stops = new Stop[]{new Stop(0, Color.web("#0E3A80", 1)), new Stop(1, Color.web("#446CAB", 1))};
        LinearGradient lg = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);

        public Quarter(double x0, double y0, double x1, double y1, double x2, double y2) {
            p = new Path();
            p.setSmooth(true);
            q = new QuadCurveTo(x1, y1, x2, y2);
            p.getElements().add(new MoveTo(0, 0));
            p.getElements().add(new LineTo(x0, y0));
            p.getElements().add(q);
            p.getElements().add(new LineTo(0, 0));
            p.setFill(lg);
        }

        public void setType(Type t) {
            type = t;
        }

        private void setGradient(Color w1, Color w2) {
            stops = new Stop[]{new Stop(0, w1), new Stop(1, w2)};
            lg = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
            p.setFill(lg);
        }

        public Path getPath() {
            return p;
        }

        public void illuminate(boolean b) {
            illuminated = b;
            if (b) {
                setGradient(Color.web("#4974BA", 1), Color.web("#86AAE3", 1));
            } else {
                setGradient(Color.web("#0E3A80", 1), Color.web("#446CAB", 1));
            }
        }
    }

    public void setVisible(boolean b) {
        totalG.setVisible(b);
    }

    public Type selected() {
        Type temp = Type.CANCEL;
        for (Quarter q : qArray) {
            if (q.illuminated) {
                switch (q.type) {
                    case OPACITY:
                        temp = Type.OPACITY;
                        break;
                    case SIZE:
                        temp = Type.SIZE;
                        break;
                    case CANCEL:
                        temp = Type.CANCEL;
                        break;

                }
            }
        }
        return temp;
    }
}
