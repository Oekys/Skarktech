# Skarktech

This project consist of creating a universal dematerialized receipt system.

# Setup

Clone project and build the application in AndroidStudio. Then it's ready to run. To use any other app than the one used to simulate a POS, you must change the GUID to the one used by your device's Bluetooth.

The NFC tag must contain a MIME type 'pos/mac' with the Bluetooth MAC address as value (String).

# Simulating the POS

Clone the https://github.com/tewarid/dotnet-tools .NET tools and build them in Visual Studio (check instructions in the tool's Git) and run the BluetoothSerialServerTool app. This app allow to read and write in the input/output Bluetooth streams.
