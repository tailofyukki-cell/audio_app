# 🔧 「Default Activity not found」エラー修正ガイド

## 発生したエラー

```
'app' の実行中にエラーが発生しました
Default Activity not found
```

エミュレータにアプリがインストールされない問題。

---

## エラーの原因

このエラーは、以下のいずれかが原因で発生します：

1. **AndroidManifest.xmlの設定ミス**
   - LAUNCHER intent-filterが正しく設定されていない
   - アクティビティ名が間違っている

2. **ビルドキャッシュの問題**
   - 古いビルドキャッシュが残っている
   - Gradleの同期が完了していない

3. **リソースの問題**
   - アイコンリソースが存在しない
   - テーマが正しく定義されていない

---

## 修正内容

### 1. AndroidManifest.xmlの修正

**修正点:**
- アクティビティ名を完全修飾名に変更（`.MainActivity` → `com.example.musicvisualizer.MainActivity`）
- アイコンリソースの参照を削除（`tools:ignore="MissingApplicationIcon"`を追加）
- システムテーマを使用（`@android:style/Theme.Material.Light.NoActionBar`）

**修正後のAndroidManifest.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@android:style/Theme.Material.Light.NoActionBar"
        tools:targetApi="31"
        tools:ignore="MissingApplicationIcon">
        
        <activity
            android:name="com.example.musicvisualizer.MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@android:style/Theme.Material.Light.NoActionBar">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

---

## 解決手順

### 方法1: Android Studioで（推奨）

#### ステップ1: クリーンビルド

```
Build → Clean Project
```

数秒待ってから：

```
Build → Rebuild Project
```

#### ステップ2: Gradleの同期

```
File → Sync Project with Gradle Files
```

#### ステップ3: キャッシュのクリア（必要に応じて）

```
File → Invalidate Caches / Restart → Invalidate and Restart
```

#### ステップ4: 実行

```
Run → Run 'app'
```

---

### 方法2: コマンドラインで

```bash
cd MusicVisualizerApp

# クリーンビルド
./gradlew clean

# デバッグAPKのビルド
./gradlew assembleDebug

# インストール
adb install -r app/build/outputs/apk/debug/app-debug.apk

# アプリを起動
adb shell am start -n com.example.musicvisualizer/.MainActivity
```

---

## トラブルシューティング

### エラー: "Activity class does not exist"

**原因:** MainActivityのパッケージ名が間違っている

**解決方法:**

1. MainActivity.ktの先頭を確認：
   ```kotlin
   package com.example.musicvisualizer
   
   class MainActivity : ComponentActivity() {
       // ...
   }
   ```

2. AndroidManifest.xmlのアクティビティ名を確認：
   ```xml
   <activity android:name="com.example.musicvisualizer.MainActivity" ...>
   ```

---

### エラー: "Installation failed"

**原因:** 古いバージョンのアプリが残っている

**解決方法:**

```bash
# アプリをアンインストール
adb uninstall com.example.musicvisualizer

# 再インストール
./gradlew installDebug
```

---

### エラー: "Manifest merger failed"

**原因:** AndroidManifest.xmlに構文エラーがある

**解決方法:**

1. Build → Make Project を実行
2. エラーメッセージを確認
3. 該当箇所を修正

---

### エラー: "Resource not found"

**原因:** リソースファイルが存在しない

**解決方法:**

strings.xmlを確認：
```xml
<resources>
    <string name="app_name">Music Visualizer</string>
</resources>
```

---

## Android Studioの実行設定を確認

### ステップ1: 実行設定を開く

```
Run → Edit Configurations...
```

### ステップ2: 設定を確認

- **Module**: app
- **Launch**: Default Activity
- **Deploy**: Default APK

### ステップ3: Launch Optionsを確認

「Launch: Default Activity」が選択されていることを確認。

もし「Nothing」や「Specified Activity」になっている場合は、「Default Activity」に変更。

---

## エミュレーターの確認

### エミュレーターが正常に動作しているか確認

```bash
# 接続されているデバイスを確認
adb devices

# 出力例:
# List of devices attached
# emulator-5554   device
```

「device」と表示されていればOK。

---

### エミュレーターを再起動

1. Android Studioの「Device Manager」を開く
2. エミュレーターの「Stop」をクリック
3. 数秒待ってから「Start」をクリック

---

## ビルドログの確認

### Android Studioで

```
View → Tool Windows → Build
```

エラーメッセージを確認して、具体的な問題を特定。

---

### コマンドラインで

```bash
./gradlew assembleDebug --stacktrace --info
```

詳細なログが出力されます。

---

## 完全なクリーンビルド手順

すべてのキャッシュをクリアして、完全にクリーンな状態からビルド：

```bash
cd MusicVisualizerApp

# Gradleキャッシュをクリア
./gradlew clean --no-daemon

# ビルドディレクトリを削除
rm -rf app/build
rm -rf build
rm -rf .gradle

# Android Studioを閉じる

# Android Studioのキャッシュをクリア
# File → Invalidate Caches / Restart

# プロジェクトを再度開く

# Gradleの同期を待つ

# ビルド
Build → Rebuild Project

# 実行
Run → Run 'app'
```

---

## 検証方法

### アプリが正常にインストールされたか確認

```bash
# インストールされているパッケージを確認
adb shell pm list packages | grep musicvisualizer

# 出力:
# package:com.example.musicvisualizer
```

### アプリを手動で起動

```bash
# アプリを起動
adb shell am start -n com.example.musicvisualizer/.MainActivity
```

---

## よくある質問

### Q: "Default Activity not found" と "Activity not found" の違いは？

**A:** 
- **Default Activity not found**: AndroidManifest.xmlにLAUNCHER intent-filterが設定されていない
- **Activity not found**: 指定されたアクティビティクラスが存在しない

---

### Q: なぜアクティビティ名を完全修飾名にする必要があるのか？

**A:** 
`.MainActivity`は相対パス表記で、パッケージ名が正しく解決されない場合があります。
`com.example.musicvisualizer.MainActivity`のように完全修飾名を使用すると、確実に解決されます。

---

### Q: アイコンがなくてもアプリは動作するのか？

**A:** 
はい、動作します。`tools:ignore="MissingApplicationIcon"`を追加することで、アイコンがなくてもビルドできます。
ただし、ランチャーにはデフォルトのAndroidアイコンが表示されます。

---

## まとめ

### 修正のポイント

1. ✅ **アクティビティ名を完全修飾名に変更**
2. ✅ **システムテーマを使用**
3. ✅ **アイコンリソースの問題を回避**
4. ✅ **クリーンビルドを実行**

### 確認事項

- ✅ AndroidManifest.xmlが正しく設定されている
- ✅ MainActivity.ktが正しいパッケージにある
- ✅ Gradleの同期が完了している
- ✅ ビルドエラーがない

---

**この修正により、アプリは正常にインストールされ、起動できるようになります！**
