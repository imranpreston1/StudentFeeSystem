# Student Fee System (Android App)

Ye ek native Android app hai (Kotlin + Room database), **Google Material Design** pattern par bani hui, **bottom navigation** ke sath (Dashboard / Students / Settings tabs) — bilkul modern apps jaisa flow. Ismein:
- **Dashboard tab**: colorful stats (Students/Collection/Pending) + Quick Actions (Add Student, Search, Backup)
- **Students tab**: inline search bar + student list
- **Settings tab**: School Settings, Backup Data, Restore Data, App Info
- Students add/edit/delete kar sakte hain (naam, roll number, class, **group**, contact, **father/guardian name**, **address**, admission date, **date of birth**, **gender**, **photo**, **monthly fee**)
- Student detail screen par **Monthly Fee / Paid / Balance** summary cards
- Har student ke fees records add kar sakte hain (month, amount, paid/pending)
- Fee record par tap karne se paid/pending status toggle ho jata hai
- Har fee record ke sath **🖨 Print** button hai jo Bluetooth thermal printer par receipt print karta hai
- **Dashboard** — Main screen ke top par total students, total collected aur total pending amount ek nazar mein
- **Search bar** — students ko naam ya roll number se turant dhoondh sakte hain
- **Backup / Restore** — Main screen ke top-right menu → **Backup / Restore Data** se pura data ek file mein save kar ke **seedha Google Drive (ya kisi bhi cloud app) mein** save kar sakte hain; usi file se kisi bhi doosre phone par data wapas restore bhi ho sakta hai
- Sara data phone ke andar local database (SQLite via Room) mein save hota hai — internet ki zaroorat nahi
- **First run par** app school ka naam poochti hai (ye naam har printed receipt ke top par aata hai); baad mein Main screen ke top-right menu → **School Settings** se change kar sakte hain

## Backup / Restore Kaise Use Karein

**Backup lene ke liye:**
1. Main screen ke top-right menu (⋮) → **Backup / Restore** → **Backup Now**
2. Ek file save karne ki screen khulegi — yahan **Google Drive** (ya jo bhi cloud app phone mein installed ho) select kar ke seedha wahan save kar dein
3. File ka naam khud-ba-khud tareekh ke sath ban jata hai (jaise `StudentFeeBackup_2026-09-20_1430.db`)

**Data restore karne ke liye** (isi phone par ya kisi naye phone par app install kar ke):
1. Menu → **Backup / Restore** → **Restore from a Backup File**
2. Jahan backup file save ki thi wahan se select karein (Google Drive, phone storage, waghera)
3. Confirm karein — pura purana data usi backup se wapas aa jayega

⚠️ Restore karne se is waqt phone mein maujood **sara data replace** ho jata hai backup wale data se — is liye restore se pehle sochi samjhi confirmation di gayi hai app mein.

## APK Kaise Banayein

1. Android Studio mein project khol kar sync complete hone dein
2. Top menu se: **Build → Build Bundle(s) / APK(s) → Build APK(s)**
3. Build complete hone par neeche corner mein "locate" link aayega — usi par click karke APK file mil jayegi (`app/build/outputs/apk/debug/app-debug.apk`)
4. Ye APK file kisi bhi Android phone par bhej kar install kar sakte hain (phone settings mein "Install from unknown sources" allow karna par sakta hai)

Agar Play Store ke liye ya kisi ko baant-ne ke liye **signed release APK** chahiye ho to: **Build → Generate Signed Bundle / APK** use karein aur wahan ek naya keystore bana lein (pehli dafa).

⚠️ **Zaroori Note:** Is update mein database mein naye fields (Father Name, Address) add hue hain. Agar phone mein pehle se purani APK install hai aur usme students/fees ka data maujood hai, to is nayi APK ko install karte waqt **pehle purani app ko uninstall** kar dein, phir nayi APK install karein — warna app crash ho sakti hai. (Naye users ke liye ye masla nahi.)

## APK Online Build Karna (Bina Android Studio Ke)

Is project mein `.github/workflows/build.yml` file already maujood hai jo **GitHub Actions** use karke khud-ba-khud APK bana degi — koi Android Studio install karne ki zaroorat nahi.

**Steps:**

1. https://github.com par account banayein (agar nahi hai)
2. Naya **repository** banayein (Public ya Private, koi farq nahi padta) — koi README/gitignore add na karein, khali rakhein
3. Is `StudentFeeSystem` folder ka pura content us repository mein upload/push kar dein:
   - Website se: repository page par **"uploading an existing file"** link se pura folder drag-drop kar dein, ya
   - Terminal/Git se:
     ```
     cd StudentFeeSystem
     git init
     git add .
     git commit -m "Initial commit"
     git branch -M main
     git remote add origin <apki-repo-ka-link>
     git push -u origin main
     ```
4. Push hote hi repository ke **Actions** tab mein "Build APK" workflow khud chalna shuru ho jayega (2-4 minute lagte hain)
5. Workflow complete hone ke baad usi run ke page par neeche **Artifacts** section mein `app-debug-apk` milega — download kar lein
6. Ye zip khol kar andar `app-debug.apk` milegi — ye phone mein bhej kar install karein

Is tarah bina apne computer par kuch install kiye, sirf GitHub se hi APK ban jayegi.

## Thermal Printer Setup

Ye kisi bhi common **Bluetooth ESC/POS thermal printer** (58mm ya 80mm) ke sath kaam karta hai:

1. Printer ko on karein aur phone ki **Bluetooth Settings** mein pair karein (ek dafa)
2. App mein kisi student ke fee record ke sath **🖨 Print** button dabayein
3. Pehli dafa Bluetooth permission mangega — Allow karein
4. Paired printers ki list mein se apna printer select karein
5. Receipt print ho jayega

Agar printer list mein nazar na aaye, dobara phone Bluetooth settings mein pairing check karein.

**School ka naam** receipt ke top par print hota hai — isko change karne ke liye `app/src/main/res/values/strings.xml` mein `school_name` value edit kar dein.

## Kaise Open Karein (Android Studio mein)

1. Android Studio install karein (agar pehle se nahi hai): https://developer.android.com/studio
2. Android Studio kholein → **Open** → is `StudentFeeSystem` folder ko select karein
3. Android Studio khud gradle wrapper generate kar lega aur "Gradle Sync" shuru ho jayega (internet chahiye hoga pehli dafa dependencies download karne ke liye)
4. Sync complete hone ke baad, top mein green **Run ▶** button dabayein
5. Ek emulator select karein ya apna phone USB Debugging ke sath connect karein
6. App phone/emulator par install ho kar chal jayegi

## App Structure

- `data/` — Room entities (`Student`, `Fee`) aur DAOs
- `viewmodel/` — ViewModels jo database se data fetch/save karte hain
- `ui/` — Activities aur Adapters (MainActivity, AddEditStudentActivity, StudentDetailActivity, AddFeeActivity)

## Aage Barhane Ke Ideas (Optional)

- Fee receipt PDF generate karna
- Monthly total collection ka dashboard/chart
- Search bar students list mein
- Login/PIN lock for security
- Cloud backup (Firebase) agar multiple devices par data chahiye ho

Koi feature add/change karwana ho to bata dein, isi project mein update kar dunga.
