# DIY Brick — Android NFC Card Trigger App

Turn any compatible NFC tag (or reprogrammed keycard) into an action trigger for your Android phone.

## What It Does

1. **Tap a card** → app reads the UID
2. **Unknown card** → prompts you to assign an action
3. **Known card** → executes the bound action instantly

Actions: toggle flashlight, camera, WiFi, Bluetooth, DND, launch maps/music, share contact, custom toast.

## Requirements

- Android phone with NFC (API 24+, Android 7+)
- Gradle / Android Studio (or command-line build)
- [Flipper Zero](https://flipperzero.one/) (strongly recommended for recon)

## Quick Build

```bash
git clone https://github.com/johnohhh1/diy-brick-android
cd diy-brick-android
# Option 1: Android Studio (recommended)
#   File → Open → select this directory → Build → Run

# Option 2: Command line
./gradlew assembleDebug
# APK appears in app/build/outputs/apk/debug/app-debug.apk
```

## Using With Your Spare Cards

### 1. Recon Your Cards With Flipper Zero

See `FLIPPER_RECON.md` for the step-by-step checklist.

**Summary:**
- If card is **13.56 MHz NFC** (MIFARE Classic, NTAG, etc.) → phone reads natively → **use directly**
- If card is **125 kHz RFID** (HID Prox, EM4100) → phone cannot read → **Flipper acts as bridge** (read or emulate)

### 2. If 13.56 MHz And Unlocked

Just tap the card to the phone with the app open. The UID will be captured and you can assign an action.

### 3. If 125 kHz Only

Options:
- **Best:** Use Flipper to read the 125 kHz UID, then write that UID to a cheap NTAG215 sticker. Tap sticker to phone.
- **Hacky:** Keep Flipper in emulate mode; trigger it somehow → not practical for pocket use.

### 4. If Hotel Card Is 13.56 MHz But Encrypted

Hotel MIFARE cards often lock sectors with custom keys. Android can *still* read the **4-byte UID** in most cases (for card identification, not data cloning). The app uses UID only, so you don't need to decrypt the hotel data.

## Action Types

| Action | Description |
|---|---|
| Open Camera | Launches stock camera |
| Toggle Flashlight | On/off via CameraManager |
| Toggle Bluetooth | Enables/disables BT radio |
| Toggle WiFi | Enables/disables WiFi |
| Toggle Hotspot | Opens Wireless Settings |
| Toggle DND | Requires DND access grant once |
| Launch Maps | Opens maps app |
| Launch Music | Opens default music app |
| Share Contact | Shares a text contact stub |
| Custom Text | Shows a toast confirmation |

## Extending

Add new actions in `ActionType.kt` and handle them in `ActionExecutor.kt`. No NFC code changes needed.

## Contributing

Issues and pull requests are welcome, especially new action types and reports of which card types work on which phones.

## License

MIT, see [LICENSE](LICENSE). It's a lab hack, so there's no warranty.
