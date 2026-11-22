# 智慧番茄鐘專注計時器 (Smart Pomodoro Focus Timer)

> **Android 課堂作業：CH12 (Service 服務) 與 CH13 (BroadcastReceiver 廣播接收器) 綜合應用與實作**

---

## 📌 專案簡介

在一般的倒數計時器 App 中，若將計時邏輯放在 `Activity` 內部，使用者切換 App、開啟其他程式或螢幕熄滅時，`Activity` 可能被系統暫停或銷毀，導致計時器中斷。

**智慧番茄鐘專注計時器** 將計時核心抽離至 **Service（服務）**，即使離開 App UI 畫面，計時器仍能在背景穩定倒數。同時搭配 **BroadcastReceiver（廣播接收器）**，實現背景服務對 UI 的動態傳輸，以及感應系統事件（螢幕開啟、耳機拔插、開機啟動）。

---

## 🛠️ CH12 與 CH13 核心概念對照

| 組成元件 | 涵蓋 Android 概念 | 專案中的實際角色 |
|----------|-------------------|------------------|
| **`TimerService`** | `startService()` / `stopService()`<br>`onStartCommand()`<br>`START_NOT_STICKY`<br>`Thread` / Foreground Service | 在背景執行倒數計時，即使 `MainActivity` 關閉仍持續進行。搭配 `START_NOT_STICKY` 確保系統殺死服務時不隨意自動重啟。建立 Foreground Notification 保持背景存活。 |
| **自訂動態廣播** | `sendBroadcast()`<br>`registerReceiver()` / `unregisterReceiver()` | `TimerService` 每秒向全 App 發送 `ACTION_TIMER_UPDATE` 廣播並帶入剩餘秒數與進度。`MainActivity` 接收廣播後即時更新倒數文字與進度條。 |
| **動態系統廣播** | `ACTION_SCREEN_ON`<br>`ACTION_HEADSET_PLUG` | 專注期間：<br>1. 開啟螢幕（`ACTION_SCREEN_ON`）時彈出 Toast 提示「保持專注！還有 X 分鐘」。<br>2. 耳機拔插（`ACTION_HEADSET_PLUG`）時自動暫停或恢復環境專注音效。 |
| **靜態廣播接收器** | `<receiver>` in `AndroidManifest.xml`<br>`BOOT_COMPLETED` | 監聽系統開機事件。當手機重開機時，自動發送 Notification 或 Toast 提醒使用者開始每日專注學習課程。 |
| **完成彈出視窗** | 背景啟動 Activity (`Intent.FLAG_ACTIVITY_NEW_TASK`) | 倒數歸零時，`TimerService` 從背景啟動 `FinishActivity` 顯示完成畫面，隨後呼叫 `stopSelf()` 自行銷毀。 |

---

## 🏗️ 系統架構圖

```text
 ┌────────────────────────┐        startService(duration)       ┌─────────────────────────────┐
 │                        │ ──────────────────────────────────▶ │                             │
 │      MainActivity      │                                     │        TimerService         │
 │                        │ ◀────────────────────────────────── │     (背景 Thread 倒數)        │
 │  (動態廣播接收器 Receiver) │     sendBroadcast("TIMER_UPDATE")   │                             │
 │   → 更新 UI & 進度條    │              (每秒一次)              │  動態系統廣播 Receiver：      │
 └────────────────────────┘                                     │  ・ACTION_SCREEN_ON         │
                                                                │  ・ACTION_HEADSET_PLUG      │
                                                                └──────────────┬──────────────┘
                                                                               │ 倒數歸零 (00:00)
                                                                               ▼
                                                                ┌─────────────────────────────┐
                                                                │       FinishActivity        │
                                                                │  (完成慶祝畫面 + stopSelf)   │
                                                                └─────────────────────────────┘

 ┌────────────────────────────────────────────────────────────┐
 │ 靜態廣播接收器 (AndroidManifest.xml 登記 BootReceiver)        │
 │ BOOT_COMPLETED ──▶ 裝置重開機時提醒使用者開始每日專注學習    │
 └────────────────────────────────────────────────────────────┘
```

---

## 🚀 亮點與新增功能細節

1. **多種預設專注時間 (Duration Presets)**
   - **25 mins (Pomodoro)**：標準 25 分鐘番茄鐘模式。
   - **5 mins (Break)**：5 分鐘短暫休息模式。
   - **10 secs (Test)**：**快速測試評分模式**（方便老師與助教快速驗證 00:00 倒數結束與 FinishActivity 彈出）。

2. **視覺化進度條 (Progress Bar)**
   - 倒數卡片內部加入平滑進度條，隨著背景廣播傳遞的剩餘秒數即時計算 0% 至 100% 完成進度。

3. **Foreground Service 常駐通知**
   - 解決 Android 8.0+ / 14+ 背景服務限制，開啟專注時可在 Android 通知欄即時查看倒數進度與點擊返回 App。

---

## 🧪 測試與驗證指南

### 1. 測試背景倒數與 FinishActivity 彈出
1. 開啟 App，選擇 **10 secs (Test)** 模式。
2. 點擊 **START FOCUS**。
3. 可按 Home 鍵切至背景或切換至其他 App。
4. 10 秒後，系統自動彈出 **`FinishActivity`** 顯示慶祝畫面（🎉 Focus Session Complete!）。

### 2. 模擬開機廣播 (BOOT_COMPLETED)
在終端機中使用 `adb` 模擬開機廣播：
```bash
adb shell am broadcast -a android.intent.action.BOOT_COMPLETED -p com.example.myapplication
```
畫面底部將跳出 Toast 提醒：「`裝置已重新開機，提醒您開始每日學習課程！`」。

### 3. 模擬螢幕開啟提示 (ACTION_SCREEN_ON)
專注期間按電源鍵關閉螢幕再開啟，或執行 `adb` 廣播：
```bash
adb shell am broadcast -a android.intent.action.SCREEN_ON -p com.example.myapplication
```
畫面將彈出 Toast 提示：「`Focus Reminder: X mins remaining!`」。

---

## 📁 檔案結構

```text
app/src/main/
├── java/com/example/myapplication/
│   ├── MainActivity.kt        # 主畫面 UI 與動態廣播接收器
│   ├── TimerService.kt        # 背景倒數服務、前台通知與動態系統廣播監聽
│   ├── FinishActivity.kt     # 計時完成頁面
│   └── BootReceiver.kt       # 靜態開機廣播接收器
└── res/
    ├── layout/
    │   ├── activity_main.xml  # 主介面（含番茄鐘卡片、進度條、模式選擇 ChipGroup）
    │   └── activity_finish.xml# 計時完成卡片介面
    └── values/
        ├── colors.xml         # 番茄紅、亮橘、綠色等主題顏色
        └── strings.xml        # 多國語言與文字資源
```
