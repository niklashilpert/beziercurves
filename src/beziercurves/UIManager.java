package beziercurves;

import java.awt.Point;

import javax.swing.*;
import java.awt.*;

public class UIManager {
    
    private final Point p1 = new Point(100, 100);
    private final Point p2 = new Point(600, 100);
    private final Point p3 = new Point(100, 600);
    private final Point p4 = new Point(600, 600);
    
    public UIManager() {
        JFrame frame = new JFrame("Les curves de Bezier");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new BezierPanel();
        panel.setPreferredSize(new Dimension(700, 700));
        panel.setLayout(null);
        
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private class BezierPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(4));
            
            g.setColor(Color.red);
            paintPoint(g2, p1);
            paintPoint(g2, p2);
            paintPoint(g2, p3);
            paintPoint(g2, p4);
            
            g2.setColor(Color.blue);
            Point[] quadraticCurvePoints = BezierCurves.getQuadraticCurve(p1, p2, p4, 30);
            //connect(g2, quadraticCurvePoints);
            paintPoints(g, quadraticCurvePoints);
            
            g2.setColor(Color.magenta);
            Point[] cubicCurvePoints1 = BezierCurves.getCubicCurve(p1, p2, p3, p4, 30);
            //connect(g2, cubicCurvePoints);
            paintPoints(g, cubicCurvePoints1);
            
            g2.setColor(Color.green);
            Point[] cubicCurvePoints2 = BezierCurves.getCubicCurve(p1, p2, p4, p3, 30);
            //connect(g2, cubicCurvePoints);
            paintPoints(g, cubicCurvePoints2);
            
        }
    }
    
    
    private void paintPoint(Graphics g, Point point) {
        g.fillOval(point.x - 5, point.y - 5, 10, 10);
    }
    
    private void paintPoints(Graphics g, Point[] points) {
        for (Point point : points) {
            paintPoint(g, point);
        }
    }
    
    private void connect(Graphics g, Point[] points) {
        for(int i = 1; i < points.length; i++) {
            drawLine(g, points[i-1], points[i]);
        }
    }
    
    private void drawLine(Graphics g, Point d1, Point d2) {
        g.drawLine(d1.x, d1.y, d2.x, d2.y);
    }
}
