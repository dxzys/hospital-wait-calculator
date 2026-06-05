package functions;

public class Commute {
    public Commute() {}
    //GET THE DISITANCE
    public static double getDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
            double R = 6371; 
            double dLat = Math.toRadians(latitude2 - latitude1);
            double dLon = Math.toRadians(longitude2 - longitude1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = R * c; 
            return distance;
    }
}
