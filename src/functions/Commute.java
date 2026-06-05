package functions;

public class Commute {
    public Commute() {}
    //GET THE DISITANCE
    public static double getDistance(double lantitude1, double longitude1, double lantitude2, double longitude2) {
            double R = 6371; 
            double dLat = Math.toRadians(lantitude2 - lantitude1);
            double dLon = Math.toRadians(longitude2 - longitude1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(Math.toRadians(lantitude1)) * Math.cos(Math.toRadians(lantitude2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = R * c; 
            return distance;
    }
}
