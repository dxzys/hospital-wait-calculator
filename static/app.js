const map = L.map('map').setView([45.273, -66.063], 13);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '© OpenStreetMap contributors'
        }).addTo(map);

hospitalMarker = L.marker([45.305, -66.084]).addTo(map);
        hospitalMarker.bindPopup("<b>Saint John Regional Hconstospital</b><br>Loading....").openPopup();        