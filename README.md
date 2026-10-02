# تطبيق الخلفيات (Wallpaper App)

تطبيق أندرويد (Kotlin + Jetpack Compose) يعرض خلفيات طبيعة/أنمي/عادية ويعيّنها مباشرة على الشاشة الرئيسية أو القفل أو الاثنين، بدون تحميل يدوي.

---

## 🚀 الطريقة الأسهل: خلّي GitHub يبني الـ APK (موصى فيها)

ما تحتاج تثبت Android SDK ولا تحمّل جيجات على جوالك — GitHub يبني لك الملف على سيرفراتهم وتحمّل APK جاهز مباشرة.

### الخطوات (كلها من المتصفح أو تطبيق GitHub على جوالك)

1. سوّي حساب مجاني بـ https://github.com (لو ما عندك)
2. أنشئ مستودع (Repository) جديد — اسمه مثلاً `wallpaper-app`، خليه **Public** أو **Private** (الاثنين يشتغلون)
3. ارفع كل ملفات المشروع (كل محتويات مجلد `wallpaper-app` اللي بالزيب) داخل المستودع:
   - من المتصفح: افتح المستودع → **Add file → Upload files** → اسحب كل الملفات والمجلدات
4. قبل الرفع، افتح ملف `app/src/main/java/com/example/wallpaperapp/repository/WallpaperRepository.kt` وحط مفتاح Unsplash (خطوة 1 تحت)
5. بعد الرفع، روح لتبويب **Actions** بأعلى المستودع — راح تلاقي عملية بناء (`Build APK`) بدأت تلقائيًا
6. خليها تخلص (تاخذ 2-5 دقائق)، بعدها افتح العملية المكتملة وبالأسفل بقسم **Artifacts** راح تلاقي ملف `app-debug` — حمّله، هذا هو الـ APK (بصيغة zip صغير يحتوي الملف، فكه وبياخذ الـ apk).
7. انقل الملف لجوالك وثبّته (فعّل "السماح من مصادر غير معروفة" أول مرة)

لو تبي تبني نسخة جديدة بعد أي تعديل، بس ارفع التعديل (Commit changes) وراح يبني تلقائيًا من جديد.

---

## أو: ابنيه بنفسك من Termux (أصعب، بدون نت خارجي عند البناء)

⚠️ هذي الطريقة تحتاج:
- مساحة فاضية **5 جيجا على الأقل**
- جهاز بذاكرة RAM **4 جيجا فأكثر** (يفضل أكثر)
- صبر — التحميل والبناء ياخذ وقت وممكن يطلع أخطاء نحتاج نصلحها خطوة خطوة

## 1) احصل على مفتاح Unsplash (مجاني)
1. سجّل بـ https://unsplash.com/developers
2. أنشئ تطبيق جديد (Demo App كافي للتجربة)
3. انسخ الـ **Access Key**
4. افتح الملف:
   `app/src/main/java/com/example/wallpaperapp/repository/WallpaperRepository.kt`
   وحط المفتاح بدل `ضع_مفتاح_Unsplash_هنا`

## 2) تثبيت الأدوات على Termux

```bash
pkg update && pkg upgrade -y
pkg install openjdk-17 gradle wget unzip -y
```

## 3) تحميل Android SDK (command line tools)

```bash
mkdir -p ~/android-sdk/cmdline-tools
cd ~/android-sdk/cmdline-tools
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O cmdtools.zip
unzip cmdtools.zip
mv cmdline-tools latest
rm cmdtools.zip
```

## 4) ضبط متغيرات البيئة

```bash
echo 'export ANDROID_HOME=$HOME/android-sdk' >> ~/.bashrc
echo 'export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools' >> ~/.bashrc
source ~/.bashrc
```

## 5) تثبيت مكونات SDK المطلوبة

```bash
yes | sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

(هذه الخطوة تحمّل حوالي 1-2 جيجا، خليها تخلص كامل)

## 6) فك ضغط المشروع وضبط مسار SDK

```bash
cd ~
unzip /sdcard/Download/wallpaper-app.zip -d ~/wallpaper-app-extracted
cd ~/wallpaper-app-extracted/wallpaper-app
echo "sdk.dir=$HOME/android-sdk" > local.properties
```

## 7) بناء APK

```bash
gradle assembleDebug
```

لو كل شي تمام، راح تلاقي الملف هنا:
```
app/build/outputs/apk/debug/app-debug.apk
```

## 8) تثبيت APK على جهازك

انقله بأي طريقة لمجلد يفتحه مدير الملفات، أو:
```bash
termux-setup-storage
cp app/build/outputs/apk/debug/app-debug.apk /sdcard/Download/
```
بعدها افتحه من "الملفات" بجوالك وثبّته (لازم تفعّل "السماح من مصادر غير معروفة").

## ملاحظات
- قسم الأنمي يستخدم مصدر مجاني بدون مفتاح (nekos.best)، الصور فان آرت وليست بالضرورة 8K مضمونة.
- دقة التحميل من Unsplash مضبوطة على 4K (`w=3840`)، لو تبي 8K غيّرها بملف `WallpaperRepository.kt` إلى `w=7680` (حجم الملفات راح يكبر).
- لو واجهت خطأ بالتوافق بين Gradle و Android Gradle Plugin، قلي نص الخطأ وأعدّل الإصدارات.
