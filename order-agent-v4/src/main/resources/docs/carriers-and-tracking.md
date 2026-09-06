# Carriers & Tracking

## Supported Carriers
The platform ships with the following carrier partners: FedEx, UPS, DHL, USPS, and local
regional couriers. The `carrier` field on an order records which one is handling delivery.

## Tracking Numbers
Every order is assigned a `trackingNumber` as soon as its status becomes SHIPPED. Before
that point the trackingNumber field will be empty/null, since the package has not left the
warehouse yet. Customers can be told that tracking becomes available once the order ships.

## Current Location
The `currentLocation` field is updated by the carrier's tracking webhook and reflects the
last known scan location of the package (e.g. a sorting facility, a city, or "Out for
delivery near <city>"). It is only meaningful once the order has shipped.
