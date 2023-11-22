package beziercurves;

import java.awt.*;

public class BezierCurves {
    
    public static Point[] getQuadraticCurve(Point p1, Point pRef, Point p2, int pointCount) {
        Point[] curvePoints = new Point[pointCount];
        for (int i = 0; i < pointCount; i++) {
            double f = ((double) i) / (pointCount-1);
            Point point1 = getLinearPointBetween(p1, pRef, f);
            Point point2 = getLinearPointBetween(pRef, p2, f);
            curvePoints[i] = getLinearPointBetween(point1, point2, f);
        }
        return curvePoints;
    }
    
    public static Point[] getCubicCurve(Point p1, Point pRef1, Point pRef2, Point p2, int pointCount) {
        Point[] quadraticPoints1 = getQuadraticCurve(p1, pRef1, pRef2, pointCount);
        Point[] quadraticPoints2 = getQuadraticCurve(pRef1, pRef2, p2, pointCount);
        
        Point[] curvePoints = new Point[pointCount];
        
        for (int i = 0; i < pointCount; i++) {
            double f = ((double) i) / (pointCount-1);
            curvePoints[i] = getLinearPointBetween(quadraticPoints1[i], quadraticPoints2[i], f);
        }
        return curvePoints;
    }
    
    public static Point getLinearPointBetween(Point p1, Point p2, double fraction) {
        int x = p1.x - (int) ((p1.x - p2.x) * fraction);
        int y = p1.y - (int) ((p1.y - p2.y) * fraction);
        
        return new Point(x, y);
    }
    
    
    
    
}
