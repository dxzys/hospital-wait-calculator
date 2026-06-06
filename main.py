import sys
import requests
import math

GEOCODER_URL = "https://geocoder.ca/"
HOSPITALS_URL = "https://services7.arcgis.com/BuriUAtfp8cZMlDt/arcgis/rest/services/dev_waittimes_exb_hfl_6da46_c5905_9256e/FeatureServer/0/query"
OSRM_URL = "https://router.project-osrm.org"

def haversine(lat1, lon1, lat2, lon2):
    R = 6371.0
    dlat = math.radians(lat2 - lat1)
    dlon = math.radians(lon2 - lon1)
    a = (
        math.sin(dlat / 2) ** 2
        + math.cos(math.radians(lat1)) * math.cos(math.radians(lat2)) * math.sin(dlon / 2) ** 2
    )
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    distance = R * c
    return distance


def get_postal_coords(postal_code):
    postal_code = postal_code.strip().replace(" ", "").upper()

    params = {
        "postal": postal_code,
        "json": 1
    }
    response = requests.get(GEOCODER_URL, params=params)
    response.raise_for_status()
    data = response.json()
    return float(data["latt"]), float(data["longt"])


def get_hospitals():
    params = {
        "f": "json",
        "where": "1=1",
        "outFields": "*",
        "returnGeometry": "true"
    }

    response = requests.get(HOSPITALS_URL, params=params)
    response.raise_for_status()

    data = response.json()

    hospitals = []
    for feature in data["features"]:
        attributes = feature["attributes"]

        hospitals.append({
            "name": attributes["hospital_name"],
            "latitude": attributes["latitude"],
            "longitude": attributes["longitude"],
            "wait_range": attributes["ed_wait_range"],
        })

    return hospitals

def format_duration(seconds):
    hours = int(seconds // 3600)
    minutes = int((seconds % 3600) // 60)
    if hours > 0:
        return f"{hours}h {minutes}m"
    return f"{minutes}m"


def get_osrm_ranked(user_lat, user_lon, hospitals):
    coords = f"{user_lon},{user_lat}"
    for h in hospitals:
        coords += f";{h['longitude']},{h['latitude']}"

    params = {
        "sources": "0",
        "annotations": "distance,duration",
    }

    url = f"{OSRM_URL}/table/v1/driving/{coords}"
    response = requests.get(url, params=params, timeout=30)
    response.raise_for_status()
    data = response.json()

    results = []
    for i, hospital in enumerate(hospitals):
        road_distance_km = data["distances"][0][i + 1] / 1000
        driving_duration_s = int(data["durations"][0][i + 1])
        results.append({
            **hospital,
            "road_distance_km": road_distance_km,
            "driving_duration_s": driving_duration_s,
            "driving_time": format_duration(driving_duration_s),
        })

    results.sort(key=lambda x: x["road_distance_km"])
    return results

def main():
    print("NB Hospital Distance Calculator\n")

    postal_code = sys.argv[1]

    user_lat, user_lon = get_postal_coords(postal_code)

    hospitals = get_hospitals()

    try:
        results = get_osrm_ranked(user_lat, user_lon, hospitals)
    except Exception:
        print("OSRM routing unavailable, falling back to straight-line distance.")
        results = []
        for hospital in hospitals:
            distance = haversine(user_lat, user_lon, hospital["latitude"], hospital["longitude"])
            hospital["road_distance_km"] = distance
            hospital["driving_time"] = "N/A"
            results.append(hospital)
        results.sort(key=lambda x: x["road_distance_km"])

    print(f"\n{'Hospital':<40} {'Distance (km)':<18} {'Driving Time':<15} {'Wait Range':<15}")
    print("-" * 100)

    for i, hospital in enumerate(results[:5], 1):
        print(f"{i}. {hospital['name']:<38} {hospital['road_distance_km']:<18.2f} {hospital['driving_time']:<15} {hospital['wait_range']:<15}")

if __name__ == "__main__":
    main()
