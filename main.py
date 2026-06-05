import requests

GEOCODER_URL = "https://geocoder.ca/"
HOSPITALS_URL = "https://services7.arcgis.com/BuriUAtfp8cZMlDt/arcgis/rest/services/dev_waittimes_exb_hfl_6da46_c5905_9256e/FeatureServer/0/query"

# function to calculate distance between two coordinates in kilometres (Haversine formula)

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
  
  return float(data["longt"]), float(data["latt"])

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
  # features -> attributes -> name, latitude, longitude, etc.

# main function
def main():
  print("NB Hospital Distance Calculator\n")
  
  # prompt the user to input postal code and store it
  postal_code = input("Enter your postal code (format: A1A1A1): ")

  # call function to get coordinates from postal code
  # and store them as latitude and longitude
  user_lat, user_lon = get_postal_coords(postal_code)

  # call function to get hospital data
  hospitals = get_hospitals()

  # loop through hospitals and calculate the distance 
  # from the user's coordinates to each hospital (haversine function)

  # sort hospitals by distance

  # print the closest hospital(s)

if __name__ == "__main__":
    main()
