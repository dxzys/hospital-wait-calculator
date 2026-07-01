import requests

HOSPITALS_URL = "https://services7.arcgis.com/BuriUAtfp8cZMlDt/arcgis/rest/services/dev_waittimes_exb_hfl_6da46_c5905_9256e/FeatureServer/0/query"

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
            "wait_low": attributes.get("wait_low", 0) or 0,
            "wait_high": attributes.get("wait_high", 0) or 0,
            "ED_Volume": attributes.get("ED_Volume", 0) or 0,
            "opening_hours": attributes.get("opening_hours", ""),
            "zone": attributes.get("zone", ""),
        })

    return hospitals