# UniSync fonts

The Login page uses three Google Fonts (extracted from Figma `8:274`):

| Resource | Family | Weight / Style |
| --- | --- | --- |
| `@font/jomolhari` | Jomolhari | Regular 400 |
| `@font/joti_one` | Joti One | Regular 400 |
| `@font/josefin_slab_bold` | Josefin Slab | Bold 700 |
| `@font/josefin_slab_semibold_italic` | Josefin Slab | SemiBold Italic 600 |

The XML files in `app/src/main/res/font/` reference the Google Play Services
downloadable-font provider via `@array/com_google_android_gms_fonts_certs`.
**That cert array is not yet present in `res/values/`** — adding the certs by
hand is error-prone, so:

## Option A — let Android Studio generate the cert array (recommended)

1. In Android Studio, open any of the font XML files in `app/src/main/res/font/`.
2. Click *Add font* / use the gutter prompt, or `File → New → Font` and pick any
   Google Font (e.g. Jomolhari).
3. Android Studio writes a correct `res/values/font_certs.xml` and
   `res/values/preloaded_fonts.xml` for you. The XMLs already in the font
   directory will then resolve correctly.
4. Optionally add this inside `<application>` in `AndroidManifest.xml` to
   preload at app start:
   ```xml
   <meta-data android:name="preloaded_fonts" android:resource="@array/preloaded_fonts" />
   ```

## Option B — ship the .ttf files

1. Download the TTFs from <https://fonts.google.com>:
   - Jomolhari (Regular)
   - Joti One (Regular)
   - Josefin Slab (Bold + SemiBold Italic)
2. Drop them in `app/src/main/res/font/` as `jomolhari.ttf`, `joti_one.ttf`,
   `josefin_slab_bold.ttf`, `josefin_slab_semibold_italic.ttf` and delete the
   matching `.xml` shims so the resource names resolve to the TTFs directly.

Either option keeps the layout code (`android:fontFamily="@font/jomolhari"`)
unchanged.

## Why the README isn't inside res/font/

The Android resource merger only accepts `.xml`, `.ttf`, `.ttc`, or `.otf`
files in `res/font/`. Markdown lives outside the resources tree.
