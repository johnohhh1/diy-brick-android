# Flipper Zero Card Recon Checklist

Use this to figure out what your spare keycards actually are and whether your Android phone can read them.

## Step 1: Physical Inspection

- [ ] Look for any printed numbers or brand names on the card
- [ ] Shine a bright flashlight through the card — any visible copper coil?
- [ ] Note whether card is thin flat card, thick fob, or keyring tag

## Step 2: 125 kHz RFID Test (Flipper)

1. On Flipper: `125 kHz RFID → Read`
2. Hold card flat against back of Flipper (antenna is centered)
3. **If it beeps and shows data:**
   - Write down: **Format** (HID Prox, EM4100, etc.)
   - Write down: **UID / Serial number**
   - **Phone cannot read this directly.** You need a 13.56 MHz NTAG sticker as proxy, or keep using Flipper.
4. **If nothing:** Card is probably 13.56 MHz or dead. Move to Step 3.

## Step 3: 13.56 MHz NFC Test (Flipper)

1. On Flipper: `NFC → Read`
2. Hold card against back of Flipper
3. **If it reads:**
   - Write down: **Tag type** (MIFARE Classic 1K, NTAG213, etc.)
   - Write down: **UID**
   - Write down: **SAK, ATQA** (helps identify cloneability)
   - Check **"Data"** tab — are sectors locked (key A/B required)?
   - **If UID is readable but data sectors encrypted:** No problem for this app, we only use UID.
4. **Save the NFC file** (`More → Save`) for later analysis

## Step 4: Android Phone Test (Optional)

1. Install **NFC Tools** (free on Play Store)
2. Tap card to phone
3. **If it reads:** Compare UID with Flipper reading. Should match.
4. **If it doesn't read but Flipper does at 13.56 MHz:** Phone NFC stack might be pickier about tag placement. Try different spots on the phone back.

## Step 5: Inventory Spreadsheet

| Card # | Source (Hotel/Keyfob/etc) | Frequency | Type | UID | Phone Reads? | Assigned Action |
|---|---|---|---|---|---|---|
| 1 | Hilton keycard | 13.56 MHz | MIFARE Classic | A1:B2:C3... | Yes | Toggle flashlight |
| 2 | Parking garage fob | 125 kHz | HID Prox | 12345 | No | Need NTAG proxy |

## Decision Matrix

| Card Type | Frequency | Phone Reads It? | Your Move |
|---|---|---|---|
| NTAG / MIFARE Ultralight | 13.56 MHz | ✅ Yes | Use directly |
| MIFARE Classic (encrypted) | 13.56 MHz | ⚠️ UID only | Use directly (UID is enough) |
| HID Prox / EM4100 | 125 kHz | ❌ No | Proxy with NTAG sticker OR use Flipper as bridge |
| Mag-stripe only | N/A | ❌ No | Not usable for NFC app |

## Step 6: Writing Proxy Tags With Flipper (if needed)

If your card is 125 kHz and you want the phone to read it:

1. Buy **NTAG215** stickers (~$0.25 each, Amazon/AliExpress)
2. On Flipper: `NFC → Saved` → pick your card file
3. `... → Write to Initial Card`
4. Hold blank NTAG215 to Flipper
5. Verify with phone

You now have a 13.56 MHz clone carrying the same UID. The app will treat it as the original.

## Safety / Legal Notes

- **DO NOT clone access credentials you don't own.** Hotels retain keycard ownership.
- This recon is for identifying and using your own spare cards for personal projects.
- The app does **not** decrypt hotel sector data; it only reads the public UID.
