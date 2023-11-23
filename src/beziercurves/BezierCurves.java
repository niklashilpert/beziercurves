package beziercurves;

import java.awt.*;
import java.util.List;

public class BezierCurves {
    
    public static DPoint[] getCurve(int pointCount, List<DPoint> points) {
        
        DPoint[] curvePoints = new DPoint[pointCount];
        
        if (points.size() < 2) return new DPoint[0];
        
        for (int i = 0; i < pointCount; i++) {
            curvePoints[i] = getPoint(points, ((double) i) / (pointCount-1));
        }
        
        return curvePoints;
    }
    
    private static DPoint getPoint(List<DPoint> points, double fraction) {
        if (points.size() == 2) {
            return getLinearPointBetween(points.get(0), points.get(1), fraction);
        } else {
            DPoint point1 = getPoint(points.subList(0, points.size()-1), fraction);
            DPoint point2 = getPoint(points.subList(1, points.size()), fraction);
            return getLinearPointBetween(point1, point2, fraction);
        }
    }
    
    
    
    public static DPoint[] getQuadraticCurve(DPoint p1, DPoint pRef, DPoint p2, int pointCount) {
        DPoint[] curvePoints = new DPoint[pointCount];
        for (int i = 0; i < pointCount; i++) {
            double f = ((double) i) / (pointCount-1);
            DPoint point1 = getLinearPointBetween(p1, pRef, f);
            DPoint point2 = getLinearPointBetween(pRef, p2, f);
            curvePoints[i] = getLinearPointBetween(point1, point2, f);
        }
        return curvePoints;
    }
    
    public static DPoint[] getCubicCurve(DPoint p1, DPoint pRef1, DPoint pRef2, DPoint p2, int pointCount) {
        DPoint[] quadraticPoints1 = getQuadraticCurve(p1, pRef1, pRef2, pointCount);
        DPoint[] quadraticPoints2 = getQuadraticCurve(pRef1, pRef2, p2, pointCount);
        
        DPoint[] curvePoints = new DPoint[pointCount];
        
        for (int i = 0; i < pointCount; i++) {
            double f = ((double) i) / (pointCount-1);
            curvePoints[i] = getLinearPointBetween(quadraticPoints1[i], quadraticPoints2[i], f);
        }
        return curvePoints;
    }
    
    public static DPoint getLinearPointBetween(DPoint p1, DPoint p2, double fraction) {
        double x = p1.x - ((p1.x - p2.x) * fraction);
        double y = p1.y - ((p1.y - p2.y) * fraction);
        
        return new DPoint(x, y);
    }
    
    
    
    
}
