package beziercurves;

import java.awt.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class UIManager {
    
    public static final int POINT_RADIUS = 2;
    
    public static final int COORD_SCALE = 10;
    
    private final ArrayList<CubicBezierCurve> curves;
   
    private final JFrame frame;
    private final JPanel panel;
    private boolean showPoints = false;
    
    
    public UIManager() {
        UIListeners listeners = new UIListeners();
        curves = new ArrayList<>();
        
        frame = new JFrame("Les curves de Bezier (?)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addKeyListener(listeners.KEY_LISTENER);
        
        panel = new BezierPanel();
        panel.setPreferredSize(new Dimension(900, 900));
        panel.setLayout(null);
        panel.addMouseMotionListener(listeners.MOUSE_LISTENER);
        panel.addMouseListener(listeners.MOUSE_LISTENER);
        
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private DPoint[] getAllPoints() {
        DPoint[] points = new DPoint[curves.size()*4];
        for (int i = 0; i < curves.size(); i++) {
            points[i*4  ] = curves.get(i).getPointA();
            points[i*4+1] = curves.get(i).getRefA();
            points[i*4+2] = curves.get(i).getRefB();
            points[i*4+3] = curves.get(i).getPointB();
        }
        return points;
    }
    
    
    private Dimension getPanelSize() {
        return frame.getContentPane().getSize();
    }
    private double toCoordX(double x) {
        return (x - getPanelSize().width/2.0) / COORD_SCALE;
    }
    private double toCoordY(double y) {
        return (y - getPanelSize().height/2.0) / COORD_SCALE;
    }
    
    private double toActualX(double x) {
        return getPanelSize().width/2.0 + x*COORD_SCALE;
    }
    private double toActualY(double y) {
        return getPanelSize().height/2.0 + y*COORD_SCALE;
    }
    
    
    private class UIListeners {
        
        private double x, y;
        private double distanceFromCenterX, distanceFromCenterY;
        private boolean preventChangingKeyReads = false;
        
        private boolean isMouseOnPoint(DPoint position, DPoint point) {
            return point.distance(position) <= POINT_RADIUS;
        }
        
        public final MouseAdapter MOUSE_LISTENER = new MouseAdapter() {
            
            private CubicBezierCurve currentCurve;
            private DPoint currentPoint;
            private boolean resetRefs = false;
            
            private boolean detectClick(DPoint clicked, DPoint point) {
                boolean result = isMouseOnPoint(clicked, point);
                if (result) {
                    currentPoint = point;
                    distanceFromCenterX = point.x - clicked.x;
                    distanceFromCenterY = point.y - clicked.y;
                }
                return result;
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                
                preventChangingKeyReads = true;
                
                DPoint clicked = new DPoint(toCoordX(e.getX()), toCoordY(e.getY()));
                
                for (CubicBezierCurve curve : curves) {
                    boolean onePointClicked = false;
                    
                    onePointClicked |= detectClick(clicked, curve.getPointA());
                    onePointClicked |= detectClick(clicked, curve.getRefA());
                    onePointClicked |= detectClick(clicked, curve.getRefB());
                    onePointClicked |= detectClick(clicked, curve.getPointB());
                    
                    // If one of the points of the current curve was clicked ...
                    if (onePointClicked) {
                        System.out.println("Clicked");
                        currentCurve = curve;
                        resetRefs = false;
                        return;
                    }
                }
                
                // Handles the creation of a new Bézier curve
                CubicBezierCurve curve = CubicBezierCurve.createBetween(clicked.clone(), clicked);
                currentCurve = curve;
                currentPoint = clicked;
                resetRefs = true;
                
                panel.repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                preventChangingKeyReads = false;
                currentCurve = null;
                currentPoint = null;
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                x = toCoordX(e.getX());
                y = toCoordY(e.getY());
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!curves.contains(currentCurve)) curves.add(currentCurve);
                
                x = toCoordX(e.getX());
                y = toCoordY(e.getY());
                
                
                currentPoint.x += x - currentPoint.x + distanceFromCenterX;
                currentPoint.y += y - currentPoint.y + distanceFromCenterY;
                
                //currentCurve.setPointB(currentPoint);
                if (resetRefs) currentCurve.resetRefs();
                currentCurve.recalculate();
                panel.repaint();
            }
        };
        
        public final KeyListener KEY_LISTENER = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                DPoint current = new DPoint(x, y);
                
                if (e.getKeyCode() == KeyEvent.VK_T) {
                    showPoints = !showPoints;
                    panel.repaint();
                } else if (!preventChangingKeyReads && e.getKeyCode() == KeyEvent.VK_D) {
                    
                    for (int i = 0; i < curves.size(); i++) {
                        boolean onePointClicked = false;
                        onePointClicked |= isMouseOnPoint(current, curves.get(i).getPointA());
                        onePointClicked |= isMouseOnPoint(current, curves.get(i).getRefA());
                        onePointClicked |= isMouseOnPoint(current, curves.get(i).getRefB());
                        onePointClicked |= isMouseOnPoint(current, curves.get(i).getPointB());
                        
                        // If one of the points of the current curve was clicked ...
                        if (onePointClicked) {
                            curves.remove(i);
                            panel.repaint();
                            return;
                        }
                    }
                }
            }
        };
    }
    private class BezierPanel extends JPanel {
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            for (CubicBezierCurve curve : curves) {
                
                if (showPoints) {
                    paintPoints(g2, curve.getPointsOnCurve(), .2, 0, 0x0000FF, true);
                } else {
                    connect(g2, curve.getPointsOnCurve(), .6, 0x0000FF);
                }
                
                // Draw connection line between main points and their reference points
                drawLine(g2, curve.getPointA(), curve.getRefA(), .2, 0x000000, POINT_RADIUS/2.0+.3);
                drawLine(g2, curve.getPointB(), curve.getRefB(), .2, 0x000000, POINT_RADIUS/2.0+.3);
                
                //GraphicsFunctions.paintPoint(g2, curve.getPointA(), POINT_RADIUS, 0xFF0000, false);
                paintPoint(g2, curve.getRefA(), POINT_RADIUS, .25, 0xFF0000, false);
                paintPoint(g2, curve.getRefB(), POINT_RADIUS, .25, 0xFF0000, false);
                //GraphicsFunctions.paintPoint(g2, curve.getPointB(), POINT_RADIUS, 0xFF0000, false);
            }
            
            
            if (!curves.isEmpty()) {
                g.drawString("Points: " + curves.get(0).guessAppropriatePointCount(), 10, 10);
            }
        }
        
        public void paintPoint(Graphics2D g, DPoint point, double radius, double strokeWidth, int color, boolean fill) {
            Color oldColor = g.getColor();
            Stroke oldStroke = g.getStroke();
            g.setColor(new Color(color));
            g.setStroke(new BasicStroke((int) (strokeWidth * COORD_SCALE)));
            
            if (fill) g.fillOval(
                    (int) toActualX(point.x - radius/2.0),
                    (int) toActualY(point.y - radius/2.0),
                    (int) (radius * COORD_SCALE),
                    (int) (radius * COORD_SCALE)
            );
            else g.drawOval(
                    (int) toActualX(point.x - radius/2.0),
                    (int) toActualY(point.y - radius/2.0),
                    (int) (radius * COORD_SCALE),
                    (int) (radius * COORD_SCALE)
            );
            
            g.setColor(oldColor);
            g.setStroke(oldStroke);
        }
        
        public void paintPoints(Graphics2D g, DPoint[] points, double radius, double strokeWidth, int color, boolean fill) {
            for (DPoint point : points) {
                paintPoint(g, point, radius, strokeWidth, color, fill);
            }
        }
        public void paintPoints(Graphics2D g, List<DPoint> points, double radius, double strokeWidth, int color, boolean fill) {
            for (DPoint point : points) {
                paintPoint(g, point, radius, strokeWidth, color, fill);
            }
        }
        
        public void connect(Graphics2D g, DPoint[] points, double strokeWidth, int color) {
            for(int i = 1; i < points.length; i++) {
                drawLine(g, points[i-1], points[i], strokeWidth, color);
            }
        }
        
        public void drawLine(Graphics2D g, DPoint p1, DPoint p2, double strokeWidth, int color, double padding) {
            double distanceX = p2.x - p1.x;
            double distanceY = p2.y - p1.y;
            double distance = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
            
            if (distance != 0 && distance > padding) {
                // Strahlensatz / Intercept Theorem
                double paddingX = padding * distanceX / distance;
                double paddingY = padding * distanceY / distance;
                
                DPoint p2Padding = new DPoint(p2.x - paddingX, p2.y - paddingY);
                
                drawLine(g, p1, p2Padding, strokeWidth, color);
            }
        }
        
        public void drawLine(Graphics2D g, DPoint p1, DPoint p2, double strokeWidth, int color) {
            Color oldColor = g.getColor();
            Stroke oldStroke = g.getStroke();
            
            g.setColor(new Color(color));
            g.setStroke(new BasicStroke((int) (strokeWidth * COORD_SCALE)));
            g.drawLine(
                    (int) toActualX(p1.x),
                    (int) toActualY(p1.y),
                    (int) toActualX(p2.x),
                    (int) toActualY(p2.y));
            g.setColor(oldColor);
            g.setStroke(oldStroke);
        }
        
    }
    
}
