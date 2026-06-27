// INITIALIZE MAP
const map = L.map('map', {
  preferCanvas: true
}).setView([45.273, -66.063], 8);

// UPDATE MAP
L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
    maxZoom: 19,
    attribution: '© OpenStreetMap contributors, © CARTO'
}).addTo(map);

// HOSIPITAL DATA
const hospitals = [
              { name: "Dr. Georges-L.-Dumont University Hospital Centre", lat: 46.0988, lon: -64.7907 },
              { name: "The Moncton Hospital", lat: 46.0958, lon: -64.7972 },
              { name: "Sackville Memorial Hospital", lat: 45.8974, lon: -64.3677 },
              { name: "Saint John Regional Hospital", lat: 45.2833, lon: -66.0667 },
              { name: "St. Joseph's Hospital", lat: 45.2728, lon: -66.0730 },
              { name: "Charlotte County Hospital", lat: 45.1292, lon: -66.8201 },
              { name: "Grand Manan Hospital", lat: 44.6703, lon: -66.7553 },
              { name: "Dr. Everett Chalmers Regional Hospital", lat: 45.9454, lon: -66.6434 },
              { name: "Oromocto Public Hospital", lat: 45.8482, lon: -66.4763 },
              { name: "Upper River Valley Hospital", lat: 46.3333, lon: -67.5167 },
              { name: "Hôpital régional d'Edmundston", lat: 47.3735, lon: -68.3263 },
              { name: "Hôpital général de Grand-Sault", lat: 47.0455, lon: -67.7392 },
              { name: "Hôpital régional de Campbellton", lat: 48.0020, lon: -66.6760 },
              { name: "Hôpital de l'Enfant-Jésus", lat: 47.8200, lon: -65.0000 },
              { name: "Chaleur Regional Hospital", lat: 47.6281, lon: -65.6517 },
              { name: "Hôpital de Tracadie", lat: 47.5130, lon: -64.9176 },
              { name: "Hôpital et centre de santé communautaire de Lamèque", lat: 47.7921, lon: -64.6461 },
              { name: "Miramichi Regional Hospital", lat: 47.0098, lon: -65.5670 },
];

// COORDINATE SIGN
hospitals.forEach(h => {
  L.marker([h.lat, h.lon])
    .addTo(map)
    .bindPopup(h.name);
});

// INTERACTIVE
