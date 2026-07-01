import requests
import math
import re
from datetime import datetime
from geocode import get_postal_coords
from hospitals_data import get_hospitals

OSRM_URL = "https://router.project-osrm.org"

BUSYNESS_WEIGHT_HOURS = 1.5  # max hours penalty when ED is at peak patient volume (tunable)

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

def format_duration(seconds):
    hours = int(seconds // 3600)
    minutes = int((seconds % 3600) // 60)
    if hours > 0:
        return f"{hours}h {minutes}m"
    return f"{minutes}m"

def classify_hospital(hospital):
    wait_range = hospital.get("wait_range", "")
    if wait_range == "Closed":
        return "closed"
    if wait_range == "Data not available":
        return "no_data"
    return "open"

def parse_opening_hours(hours_str):
    if not hours_str or hours_str in ("24/7", "24 h"):
        return None, None, True

    match = re.search(r"(\d+)\s*am?\s*[\-\u2013]\s*(\d+)\s*pm?", hours_str.lower())
    if match:
        open_hr = int(match.group(1))
        close_hr = int(match.group(2)) + 12
        return open_hr, close_hr, False

    return None, None, False

def is_currently_open(opening_hours):
    open_hr, close_hr, always_open = parse_opening_hours(opening_hours)
    if always_open:
        return True
    if open_hr is None:
        return True
    now = datetime.now()
    return open_hr <= now.hour < close_hr

def compute_score(hospital, driving_duration_s, all_ed_volumes):
    # Estimated hours until discharge: drive + visit duration + busyness penalty (lower = better)
    drive_hours = driving_duration_s / 3600

    wait_low = hospital.get("wait_low", 0) or 0
    wait_high = hospital.get("wait_high", 0) or 0
    mid_wait = (wait_low + wait_high) / 2  # midpoint of API's registration-to-discharge range (hours)

    ed_volume = hospital.get("ED_Volume", 0) or 0
    max_volume = max(all_ed_volumes) if all_ed_volumes else 1
    # busier EDs get a time penalty proportional to their relative volume
    busyness_penalty = (ed_volume / max_volume) * BUSYNESS_WEIGHT_HOURS

    return drive_hours + mid_wait + busyness_penalty

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

    all_ed_volumes = [
        h["ED_Volume"]
        for h in hospitals
        if isinstance(h.get("ED_Volume"), (int, float)) and h["ED_Volume"] > 0
    ]

    results = []
    for i, hospital in enumerate(hospitals):
        road_distance_km = data["distances"][0][i + 1] / 1000
        driving_duration_s = int(data["durations"][0][i + 1])

        status = classify_hospital(hospital)
        currently_open = is_currently_open(hospital.get("opening_hours", ""))

        score = None
        if status == "open" and currently_open:
            score = compute_score(hospital, driving_duration_s, all_ed_volumes)

        wait_low = hospital.get("wait_low", 0) or 0
        wait_high = hospital.get("wait_high", 0) or 0
        mid_wait = (wait_low + wait_high) / 2

        results.append({
            **hospital,
            "road_distance_km": road_distance_km,
            "driving_duration_s": driving_duration_s,
            "driving_time": format_duration(driving_duration_s),
            "mid_wait": mid_wait,
            "score": score,
            "status": status,
            "currently_open": currently_open,
        })

    # ranking priority: open+now > open+closed hrs > no_data > closed (ED)
    def sort_key(h):
        if h["status"] == "open" and h["currently_open"]:
            return (0, h["score"] if h["score"] is not None else float("inf"))
        elif h["status"] == "open" and not h["currently_open"]:
            return (1, float("inf"))
        elif h["status"] == "no_data":
            return (2, float("inf"))
        else:
            return (3, float("inf"))

    results.sort(key=sort_key)
    return results

def main():
    print("NB Hospital Wait Calculator\n")

    postal_code = input("Enter your postal code (format: A1A1A1): ")

    user_lat, user_lon = get_postal_coords(postal_code)

    hospitals = get_hospitals()

    try:
        results = get_osrm_ranked(user_lat, user_lon, hospitals)
    except Exception:
        print("OSRM routing unavailable, falling back to straight-line distance.")
        results = []
        for hospital in hospitals:
            distance = haversine(user_lat, user_lon, hospital["latitude"], hospital["longitude"])
            status = classify_hospital(hospital)
            currently_open = is_currently_open(hospital.get("opening_hours", ""))

            wait_low = hospital.get("wait_low", 0) or 0
            wait_high = hospital.get("wait_high", 0) or 0
            mid_wait = (wait_low + wait_high) / 2

            results.append({
                **hospital,
                "road_distance_km": distance,
                "driving_duration_s": 0,
                "driving_time": "N/A",
                "mid_wait": mid_wait,
                "score": None,
                "status": status,
                "currently_open": currently_open,
            })

        results.sort(key=lambda x: (
            0 if x["status"] == "open" else (1 if x["status"] == "no_data" else 2),
            x["road_distance_km"],
        ))

    print(f"\n{'#':<3} {'Hospital':<35} {'Dist (km)':<10} {'Drive':<10} {'Wait (mid)':<12} {'ED Vol':<8} {'Score'}")
    print("-" * 100)

    for i, h in enumerate(results[:8], 1):
        if h["status"] == "open" and h["mid_wait"] > 0:
            mid_str = f"{h['mid_wait']:.1f}h"
        else:
            mid_str = h["wait_range"]

        vol_str = str(h["ED_Volume"]) if h["status"] == "open" else "\u2014"
        score_str = f"{h['score']:.1f}h" if h["score"] is not None else "\u2014"

        # flag hospitals that are unavailable or closed right now
        indicator = ""
        if h["status"] == "open" and not h["currently_open"]:
            indicator = " [closed now]"
        elif h["status"] == "closed":
            indicator = " [ED closed]"
        elif h["status"] == "no_data":
            indicator = " [no data]"

        print(
            f"{i:<3} {h['name'][:34]:<34}  {h['road_distance_km']:<10.2f} {h['driving_time']:<10} "
            f"{mid_str:<12} {vol_str:<8} {score_str:<10}{indicator}"
        )

if __name__ == "__main__":
    main()
