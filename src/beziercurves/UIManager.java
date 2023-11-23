package beziercurves;

import java.awt.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class UIManager {
    
    public static final int mainPointRadius = 15;
    public static final int sampleSize = 300;
    
    private final ArrayList<DPoint> mainPoints;
    private DPoint[] curvePoints;
    private DPoint currentPoint;
    
    private boolean showPoints = false;
    private double x, y;
    
    public UIManager() {
        mainPoints = new ArrayList<>();
        curvePoints = new DPoint[0];
        
        JFrame frame = new JFrame("Les curves de Bezier");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new BezierPanel();
        panel.setPreferredSize(new Dimension(900, 900));
        panel.setLayout(null);
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                
                DPoint current = new DPoint(x, y);
                
                if (e.getKeyCode() == KeyEvent.VK_T) {
                    showPoints = !showPoints;
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    for (int i = 0; i < mainPoints.size(); i++) {
                        if (mainPoints.get(i).distance(current) <= mainPointRadius) {
                            DPoint selected = mainPoints.get(i);
                            mainPoints.remove(selected);
                            curvePoints = BezierCurves.getCurve(sampleSize, mainPoints);
                        }
                    }
                }
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                x = e.getX();
                y = e.getY();
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                x = e.getX();
                y = e.getY();
                
                currentPoint.x += e.getX() - currentPoint.x;
                currentPoint.y += e.getY() - currentPoint.y;
                curvePoints = BezierCurves.getCurve(sampleSize, mainPoints);
            }
        });
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                DPoint clicked = new DPoint(e.getX(), e.getY());
                
                for (DPoint p : mainPoints) {
                    if (p.distance(clicked) <= mainPointRadius) {
                        currentPoint = p;
                        return;
                    }
                }
                
                mainPoints.add(clicked);
                currentPoint = clicked;
                curvePoints = BezierCurves.getCurve(sampleSize, mainPoints);
            }
        });
        
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
            
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2));
            
            g.setColor(Color.blue);
            
            if (showPoints) {
                paintPoints(g2, curvePoints);
            } else {
                connect(g2, curvePoints);
            }
            g.setColor(Color.red);
            paintPoints(g2, mainPoints);
            
            repaint();
            
        }
    }
    
    
    private void paintPoint(Graphics g, DPoint point) {
        g.fillOval((int) point.x - mainPointRadius/2, (int) point.y - mainPointRadius/2, mainPointRadius, mainPointRadius);
    }
    
    private void paintPoints(Graphics g, DPoint[] points) {
        for (DPoint point : points) {
            paintPoint(g, point);
        }
    }
    private void paintPoints(Graphics g, List<DPoint> points) {
        for (DPoint point : points) {
            paintPoint(g, point);
        }
    }
    
    
    private void connect(Graphics g, DPoint[] points) {
        for(int i = 1; i < points.length; i++) {
            drawLine(g, points[i-1], points[i]);
        }
    }
    
    private void drawLine(Graphics g, DPoint d1, DPoint d2) {
        g.drawLine((int) Math.round(d1.x), (int) Math.round(d1.y), (int) Math.round(d2.x), (int) Math.round(d2.y));
    }
}
