package beziercurves;

import java.util.List;

public class BezierCurves {
    
    public static DPoint[] getCurve(int pointCount, List<DPoint> points) {
        
        DPoint[] curvePoints = new DPoint[pointCount];
        
        if (points.size() < 2) return new DPoint[0];
        
        for (int i = 0; i < pointCount; i++) {
            curvePoints[i] = getPointOnCurve(points, ((double) i) / (pointCount-1));
        }
        
        return curvePoints;
    }
    
    public static DPoint getPointOnCurve(List<DPoint> points, double fraction) {
        if (points.size() == 2) {
            return getLinearPointBetween(points.get(0), points.get(1), fraction);
        } else {
            DPoint point1 = getPointOnCurve(points.subList(0, points.size()-1), fraction);
            DPoint point2 = getPointOnCurve(points.subList(1, points.size()), fraction);
            return getLinearPointBetween(point1, point2, fraction);
        }
    }
    
    public static DPoint getLinearPointBetween(DPoint p1, DPoint p2, double fraction) {
        double x = p1.x - ((p1.x - p2.x) * fraction);
        double y = p1.y - ((p1.y - p2.y) * fraction);
        
        return new DPoint(x, y);
    }
}
