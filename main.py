import requests # we will need this to send HTTP requests to the APIs (retreive the data)

# set constants for geocoder and NB hospital data API urls
GEOCODER_URL = "https://geocoder.ca/"
#     parameters: ?postal={postal_code}&geoit=XML&json=1
#   https://services7.arcgis.com/BuriUAtfp8cZMlDt/arcgis/rest/services/dev_waittimes_exb_hfl_6da46_c5905_9256e/FeatureServer/0/query
#     parameters: ?f=json&where=1%3D1&outFields=*&resultRecordCount=100&returnGeometry=true
#   we will need this to parse the XML response from the geocoder API

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

# main function
def main():
  print("NB Hospital Distance Calculator\n")
  
  # prompt the user to input postal code and store it
  postal_code = input("Enter your postal code (format: A1A1A1): ")

  # call function to get coordinates from postal code
  # and store them as latitude and longitude
  user_lat, user_lon = get_postal_coords(postal_code)

  # call function to get hospital data

  # loop through hospitals and calculate the distance 
  # from the user's coordinates to each hospital (haversine function)

  # sort hospitals by distance

  # print the closest hospital(s)

if __name__ == "__main__":
    main()
