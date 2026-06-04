import requests # we will need this to send HTTP requests to the APIs (retreive the data)

# set constants for geocoder and NB hospital data API urls
#   https://geocoder.ca/
#     parameters: ?postal={postal_code}&geoit=XML&json=1
#   https://services7.arcgis.com/BuriUAtfp8cZMlDt/arcgis/rest/services/dev_waittimes_exb_hfl_6da46_c5905_9256e/FeatureServer/0/query
#     parameters: ?f=json&where=1%3D1&outFields=*&resultRecordCount=100&returnGeometry=true

# function to calculate distance between two coordinates in kilometres (Haversine formula)

# function to get coordinates from postal code

# function to get hospital data

# main function
def main():
  print("NB Hospital Distance Calculator\n")
  
  # prompt the user to input postal code and store it

  # call function to get coordinates from postal code
  # and store them as latitude and longitude

  # call function to get hospital data

  # loop through hospitals and calculate the distance 
  # from the user's coordinates to each hospital (haversine function)

  # sort hospitals by distance

  # print the closest hospital(s)

if __name__ == "__main__":
    main()
