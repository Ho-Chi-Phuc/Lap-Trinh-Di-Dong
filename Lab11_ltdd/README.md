# Water Me! App – Practice: WorkManager (Android Basics with Compose – Unit 7)

Ứng dụng **Water Me!** là bài thực hành thuộc khóa học **Android Basics with Compose – Unit 7: WorkManager** của Google, giúp người dùng xem danh sách các loại cây và đặt lịch nhắc nhở tưới cây chạy ngầm bằng **Android Jetpack WorkManager**.

---

## 1. Tính năng chính

- Hiển thị danh sách các loại cây (`Lithop`, `Carrot`, `Peony`, `Pothos`, `Fiddle Leaf Fig`, `Strawberry`) cùng loại cây, mô tả và tần suất tưới bằng **Jetpack Compose** (`LazyColumn`).
- Cho phép nhấn vào từng loại cây để mở hộp thoại (`AlertDialog`) chọn khoảng thời gian nhắc tưới:
  - **5 seconds** (5 giây)
  - **1 minute** (1 phút – *Optional Challenge*)
  - **2 minutes** (2 phút – *Optional Challenge*)
  - **3 minutes** (3 phút – *Optional Challenge*)
  - **1 day** (1 ngày)
  - **1 week** (1 tuần)
  - **1 month** (1 tháng)
- Lên lịch công việc chạy nền (`OneTimeWorkRequest`) thông qua `WorkManagerWaterRepository` và `WaterReminderWorker` (`CoroutineWorker`).
- Gửi thông báo nhắc tưới cây (*"It's time to water your [Plant Name]."*), hoạt động ngay cả khi ứng dụng ở chế độ nền hoặc đã đóng.
- Quản lý công việc duy nhất (`enqueueUniqueWork`) với chính sách `ExistingWorkPolicy.REPLACE` theo khóa `plantName + duration`.

---

## 2. Cấu trúc mã nguồn chính

- [`MainActivity.kt`](app/src/main/java/com/example/waterme/MainActivity.kt): Activity chính khởi tạo giao diện `WaterMeApp()`.
- [`WaterMeApplication.kt`](app/src/main/java/com/example/waterme/WaterMeApplication.kt): Lớp `Application` khởi tạo `DefaultAppContainer` phục vụ Dependency Injection.
- [`Constants.kt`](app/src/main/java/com/example/waterme/Constants.kt): Các hằng số cấu hình Notification Channel và thời gian nhắc nhở (`FIVE_SECONDS`, `ONE_MINUTE`, `TWO_MINUTES`, `THREE_MINUTES`, `ONE_DAY`, `SEVEN_DAYS`, `THIRTY_DAYS`).
- [`data/WorkManagerWaterRepository.kt`](app/src/main/java/com/example/waterme/data/WorkManagerWaterRepository.kt): Triển khai `scheduleReminder()` để đóng gói `Data`, tạo `OneTimeWorkRequest` cho `WaterReminderWorker` với độ trễ `setInitialDelay(duration, unit)` và đưa vào hàng đợi `workManager.enqueueUniqueWork(plantName + duration, ExistingWorkPolicy.REPLACE, workRequestBuilder)`.
- [`worker/WaterReminderWorker.kt`](app/src/main/java/com/example/waterme/worker/WaterReminderWorker.kt): Lớp `CoroutineWorker` đọc tên cây từ `inputData.getString(nameKey)` và hiển thị thông báo qua `makePlantReminderNotification()`.
- [`worker/WorkerUtils.kt`](app/src/main/java/com/example/waterme/worker/WorkerUtils.kt): Hàm tiện ích khởi tạo `NotificationChannel` (API 26+) và phát thông báo hệ thống.
- [`ui/WaterMeApp.kt`](app/src/main/java/com/example/waterme/ui/WaterMeApp.kt) & [`ui/WaterViewModel.kt`](app/src/main/java/com/example/waterme/ui/WaterViewModel.kt): Giao diện Compose hiển thị danh sách cây, hộp thoại chọn thời gian và `ViewModel` kết nối UI với `WaterRepository`.

---

## 3. Hướng dẫn Build và Chạy ứng dụng

### Cách 1: Chạy trực tiếp trên Android Studio
1. Mở **Android Studio** -> chọn **Open** -> trỏ tới thư mục dự án `D:\Lab11_ltdd`.
2. Đợi quá trình **Gradle Sync** hoàn tất.
3. Khởi động máy ảo (**Android Emulator**) hoặc kết nối thiết bị Android thật qua USB Debugging.
4. Nhấn nút **Run 'app'** (`Shift + F10`) để biên dịch và cài đặt ứng dụng lên thiết bị.

### Cách 2: Build bằng dòng lệnh (Command Line / Terminal)
Chạy lệnh sau tại thư mục gốc của project (`D:\Lab11_ltdd`):

```powershell
.\gradlew.bat assembleDebug
```

File APK sau khi biên dịch thành công sẽ nằm tại:
`app\build\outputs\apk\debug\app-debug.apk`

Để cài đặt trực tiếp lên thiết bị/máy ảo đang kết nối:
```powershell
.\gradlew.bat installDebug
```

---

## 4. Hướng dẫn Kiểm tra chức năng nhắc tưới cây

### Bước 1: Cấp quyền Thông báo (Android 13 / API 33 trở lên)
Ứng dụng khai báo quyền `<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />` nhưng chưa tự mở hộp thoại xin quyền runtime trên Android 13+. Trước khi kiểm thử trên thiết bị chạy **Android 13 (API 33) trở lên**, hãy bật quyền thông báo cho ứng dụng:
1. Mở **Settings (Cài đặt)** trên thiết bị/máy ảo -> **Apps (Ứng dụng)** -> **Water Me**.
2. Chọn **Notifications (Thông báo)** -> Bật **All Water Me notifications (Cho phép thông báo)**.
   *(Hoặc dùng lệnh ADB: `adb shell pm grant com.example.waterme android.permission.POST_NOTIFICATIONS`)*

### Bước 2: Kiểm tra các kịch bản hoạt động
1. **Đặt nhắc nhở cơ bản (5 giây):**
   - Mở ứng dụng **Water Me**, nhấn vào cây **Carrot** (hoặc bất kỳ cây nào).
   - Trong hộp thoại *"Remind me to water Carrot in…"*, chọn **5 seconds**.
   - Đợi khoảng 5 giây -> kiểm tra thanh trạng thái xuất hiện thông báo **"Water me! - It's time to water your Carrot."**
2. **Kiểm tra khi đóng hoặc ẩn ứng dụng xuống nền:**
   - Nhấn vào cây **Peony**, chọn **5 seconds** hoặc **1 minute**.
   - Nhấn nút Home hoặc vuốt đóng hoàn toàn ứng dụng.
   - Đợi hết thời gian đã chọn -> WorkManager vẫn thực thi `WaterReminderWorker` ở nền và hiển thị thông báo.
3. **Kiểm tra đặt nhiều lịch khác nhau:**
   - Chọn **Lithop** -> đặt **5 seconds**, sau đó chọn **Pothos** -> đặt **1 minute**.
   - Cả hai công việc có tên duy nhất khác nhau (`Lithop5` và `Pothos1`) nên đều được lên lịch độc lập, không ghi đè nhau.
4. **Kiểm tra ghi đè lịch trùng (`ExistingWorkPolicy.REPLACE`):**
   - Chọn **Strawberry** -> đặt **1 minute**, sau đó trước khi hết 1 phút lại chọn **Strawberry** -> đặt **1 minute**.
   - Công việc cũ (`Strawberry1`) sẽ bị thay thế và bộ đếm thời gian được đặt lại từ đầu.

### Bước 3: Kiểm tra trạng thái WorkManager (Tùy chọn)
Bạn có thể mở công cụ **App Inspection -> Background Task Inspector** trong Android Studio (hoặc chạy `adb shell dumpsys jobscheduler`) để quan sát trạng thái các `WaterReminderWorker` (`ENQUEUED`, `RUNNING`, `SUCCEEDED`).
