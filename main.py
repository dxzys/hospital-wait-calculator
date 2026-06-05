import requests
import math

GEOCODER_URL = "https://geocoder.ca/"
HOSPITALS_URL = "https://services7.arcgis.com/BuriUAtfp8cZMlDt/arcgis/rest/services/dev_waittimes_exb_hfl_6da46_c5905_9256e/FeatureServer/0/query"

# function to calculate distance between two coordinates in kilometres (Haversine formula)
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

# function to get coordinates from postal code
def get_postal_coords(postal_code):
  params = {
    "postal": postal_code,
    "geoit": "XML",
    "json": 1
  }
  
  response = requests.get(GEOCODER_URL, params=params)
  response.raise_for_status()
  
  data = response.json()
  
  return float(data["latt"]), float(data["longt"])

# function to get hospital data
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
      "wait_range": attributes["ed_wait_range"]
    })

  return hospitals

# main function
def main():
  print("NB Hospital Distance Calculator\n")
  
  postal_code = input("Enter your postal code (format: A1A1A1): ")

  user_lat, user_lon = get_postal_coords(postal_code)

  hospitals = get_hospitals()

  results = []
  
  for hospital in hospitals:
    distance = haversine(user_lat, user_lon, hospital["latitude"], hospital["longitude"])
    hospital["distance"] = distance
    results.append(hospital)

  # sort hospitals by distance
  results.sort(key=lambda x: x["distance"])

  # print the closest hospital(s)

if __name__ == "__main__":
    main()
