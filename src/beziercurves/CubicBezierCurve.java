package beziercurves;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CubicBezierCurve {
    
    public static final int LENGTH_CALCULATION_POINT_COUNT = 10;
    public static final double LENGTH_TO_POINT_RELATION = 2.5;
    public static final double START_REF_POSITION = .2;
    
    private DPoint pointA, refA, refB, pointB;
    
    private DPoint[] pointsOnCurve;
    
    public static CubicBezierCurve createBetween(DPoint pA, DPoint pB) {
        CubicBezierCurve curve = new CubicBezierCurve(pA, pA, pB, pB);
        curve.resetRefs();
        curve.recalculate();
        return curve;
    }
    
    public CubicBezierCurve(DPoint pA, DPoint rA, DPoint rB, DPoint pB) {
        this.pointA = pA;
        this.refA = rA;
        this.refB = rB;
        this.pointB = pB;
        
        recalculate();
    }
    
    public void resetRefs() {
        refA = BezierCurves.getLinearPointBetween(pointA, pointB, START_REF_POSITION);
        refB = BezierCurves.getLinearPointBetween(pointB, pointA, START_REF_POSITION);
    }
    
    public void recalculate(int pointCount) {
        List<DPoint> refPoints = new ArrayList<>();
        Collections.addAll(refPoints, pointA, refA, refB, pointB);
        pointsOnCurve = BezierCurves.getCurve(pointCount, refPoints);
    }
    
    public void recalculate() {
        recalculate(guessAppropriatePointCount());
    }
    
    public int guessAppropriatePointCount() {
        return (int) (getApproxLength(LENGTH_CALCULATION_POINT_COUNT) * LENGTH_TO_POINT_RELATION);
    }
    
    // Returns an approximation of the curve's length
    public double getApproxLength(int pointCount) {
        List<DPoint> refPoints = new ArrayList<>();
        Collections.addAll(refPoints, pointA, refA, refB, pointB);
        
        DPoint[] samples = BezierCurves.getCurve(pointCount, refPoints);
        
        double length = 0.0;
        
        for (int i = 1; i < pointCount; i++) {
            length += samples[i-1].distance(samples[i]);
        }
        System.out.println(length);
        return length;
    }
    
    
    /* ### Getters and Setters ### */
    
    public DPoint[] getPointsOnCurve() {
        return pointsOnCurve;
    }
    
    public DPoint getPointA() {
        return pointA;
    }
    
    public void setPointA(DPoint pointA) {
        this.pointA = pointA;
    }
    
    public DPoint getRefA() {
        return refA;
    }
    
    public void setRefA(DPoint refA) {
        this.refA = refA;
    }
    
    public DPoint getRefB() {
        return refB;
    }
    
    public void setRefB(DPoint refB) {
        this.refB = refB;
    }
    
    public DPoint getPointB() {
        return pointB;
    }
    
    public void setPointB(DPoint pointB) {
        this.pointB = pointB;
    }
}
