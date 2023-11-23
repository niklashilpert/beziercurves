package beziercurves;

public class DPoint {
    
    public double x, y;
    
    public DPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public double distance(DPoint other) {
        return Math.sqrt(Math.pow(x-other.x, 2) + Math.pow(y-other.y, 2));
    }
}
