import requests

GEOCODER_URL = "https://geocoder.ca/"

def get_postal_coords(postal_code):
    postal_code = postal_code.strip().replace(" ", "").upper()
    params = {"postal": postal_code, "json": 1}
    response = requests.get(GEOCODER_URL, params=params, timeout=10)
    response.raise_for_status()
    data = response.json()
    return float(data["latt"]), float(data["longt"])
