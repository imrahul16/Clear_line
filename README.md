📞 ClearLine – Smart Call Blocker

ClearLine is a privacy-focused Android application built in Kotlin that automatically blocks unknown callers to protect users from unwanted calls, scams, and potential harassment.

🚀 Features

- 🔒 **Call Blocking** – Blocks incoming calls from unknown or unsaved numbers using Android's CallScreeningService.
- 🔧 **Toggle Call Blocking** – Easily enable or disable call blocking with a single switch.
- 📋 **View Blocked Numbers** – See a list of all numbers that were automatically blocked.
- 💾 **Persistent Preferences** – App remembers your call blocking preference even after closing.
- 🔐 **Secure Permissions** – Handles runtime permissions for contacts, phone state, and call log securely.

📱 Tech Stack

- **Language:** Kotlin
- **Framework:** Android SDK
- **Min SDK:** 29 (Android 10)
- **Target SDK:** 33+
- **IDE:** Android Studio

🧠 Skills Demonstrated

- Android Services and Broadcast Receivers  
- Kotlin programming & UI design  
- Permission management and lifecycle awareness  
- Persistent data storage using SharedPreferences  
- App Manifest configuration for Android 12+

⚙️ Setup Instructions

1. Clone the repository:
   
   git clone https://github.com/yourusername/ClearLine.git


2. Open in Android Studio.
3. Allow required permissions when the app launches.
4. Run the app on a device (real or emulator running Android 10 or above).

## 📄 Permissions Used

* `READ_CONTACTS`
* `READ_CALL_LOG`
* `READ_PHONE_STATE`
* `ANSWER_PHONE_CALLS`
* `BIND_SCREENING_SERVICE`
* `RECEIVE_BOOT_COMPLETED`

> These permissions are needed to detect and block calls from unsaved numbers effectively.

## 🛠 Known Issues

* No scheduling functionality (was removed for stability).
* Only blocks **unknown numbers**, not specific blacklists (yet).

👨‍💻 Developer

**Rahul Dutta**
[LinkedIn](https://www.linkedin.com/in/imrahul16/) • [GitHub](https://github.com/imrahul16)



