# 🚀 Music Visualizer - セットアップガイド

このガイドでは、Music Visualizerアプリを開発環境にセットアップし、ビルドして実行するまでの手順を詳しく説明します。

## 📋 目次

1. [必要な環境](#必要な環境)
2. [環境のセットアップ](#環境のセットアップ)
3. [プロジェクトのインポート](#プロジェクトのインポート)
4. [ビルドと実行](#ビルドと実行)
5. [トラブルシューティング](#トラブルシューティング)

## 必要な環境

### ソフトウェア要件

- **Android Studio**: Hedgehog (2023.1.1) 以降
- **JDK**: Java 11 以降（Android Studioに同梱）
- **Android SDK**:
  - Compile SDK: 34
  - Min SDK: 29 (Android 10.0)
  - Target SDK: 34
- **Gradle**: 8.0 以降（Android Studioに同梱）
- **Kotlin**: 1.9.20 以降

### ハードウェア要件

- **RAM**: 8GB以上推奨（最小4GB）
- **ストレージ**: 10GB以上の空き容量
- **CPU**: Intel i5 / AMD Ryzen 5 以上推奨

### テスト環境

- **エミュレーター**: Android 10.0 (API 29) 以降
- **実機**: Android 10.0以降を搭載したデバイス

## 環境のセットアップ

### 1. Android Studioのインストール

#### Windows / macOS / Linux

1. [Android Studio公式サイト](https://developer.android.com/studio)からダウンロード
2. インストーラーを実行
3. セットアップウィザードで「Standard」を選択
4. Android SDKのダウンロードを待つ

### 2. Android SDKの設定

1. Android Studioを起動
2. **Tools → SDK Manager**を開く

3. **SDK Platforms**タブで以下をインストール:
   - ✅ Android 14.0 (API 34)
   - ✅ Android 10.0 (API 29)

4. **SDK Tools**タブで以下をインストール:
   - ✅ Android SDK Build-Tools 34
   - ✅ Android SDK Platform-Tools
   - ✅ Android Emulator
   - ✅ Intel x86 Emulator Accelerator (HAXM) ※Intel CPUの場合

### 3. エミュレーターの作成（オプション）

実機がない場合、エミュレーターを作成します：

1. **Tools → Device Manager**を開く
2. **Create Device**をクリック
3. デバイスを選択（例: Pixel 6）
4. システムイメージを選択:
   - **推奨**: API 34 (Android 14.0)
   - **最小**: API 29 (Android 10.0)
5. **Finish**をクリック

## プロジェクトのインポート

### 方法1: Android Studioから開く

1. Android Studioを起動
2. **File → Open**を選択
3. `MusicVisualizerApp`フォルダを選択
4. **OK**をクリック
5. Gradleの同期が自動的に開始されます

### 方法2: コマンドラインから

```bash
# プロジェクトディレクトリに移動
cd /path/to/MusicVisualizerApp

# Android Studioで開く（macOS）
open -a "Android Studio" .

# Android Studioで開く（Linux）
studio .
```

### Gradle同期

プロジェクトを開くと、Android StudioがGradleの同期を開始します：

1. 画面下部の「Build」タブで進行状況を確認
2. 依存関係のダウンロードには数分かかる場合があります
3. 「BUILD SUCCESSFUL」と表示されれば成功

**同期に失敗した場合**:
```
File → Sync Project with Gradle Files
```

## ビルドと実行

### デバッグビルドの実行

#### エミュレーターで実行

1. ツールバーのデバイス選択ドロップダウンからエミュレーターを選択
2. 緑色の「Run」ボタン（▶）をクリック
3. エミュレーターが起動し、アプリがインストールされます

#### 実機で実行

1. **開発者オプションの有効化**:
   - 設定 → デバイス情報 → ビルド番号を7回タップ

2. **USBデバッグの有効化**:
   - 設定 → システム → 開発者向けオプション → USBデバッグをON

3. **デバイスの接続**:
   - USBケーブルでPCとデバイスを接続
   - デバイスに表示される「USBデバッグを許可しますか？」で「許可」をタップ

4. **実行**:
   - ツールバーのデバイス選択ドロップダウンから接続したデバイスを選択
   - 緑色の「Run」ボタン（▶）をクリック

### コマンドラインからのビルド

#### デバッグAPKのビルド

```bash
# プロジェクトディレクトリに移動
cd MusicVisualizerApp

# Gradleラッパーに実行権限を付与（初回のみ、Linux/macOS）
chmod +x gradlew

# デバッグAPKをビルド
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

ビルドされたAPKは以下に生成されます：
```
app/build/outputs/apk/debug/app-debug.apk
```

#### APKのインストール

```bash
# adbコマンドでインストール
adb install app/build/outputs/apk/debug/app-debug.apk

# 既にインストールされている場合は上書き
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## アプリの使い方

### 初回起動

1. アプリを起動
2. 権限リクエスト画面が表示される
3. 「権限を許可」ボタンをタップ
4. 録音権限を許可
5. ストレージ権限を許可

### 音楽の再生

1. **音楽ファイルの選択**:
   - 左下の🎵アイコンをタップ
   - ファイルピッカーが開く
   - 音楽ファイルを選択

2. **ビジュアライザーモードの選択**:
   - 画面上部のチップから選択
   - バー / 波形 / 円形 / スペクトラム

3. **再生**:
   - 中央の大きな再生ボタンをタップ
   - ビジュアライザーが動き始める

4. **コントロール**:
   - シークバーで位置を移動
   - 音量スライダーで音量調整
   - 停止ボタンで停止

## トラブルシューティング

### Gradle同期エラー

**エラー**: "Failed to sync Gradle project"

**解決方法**:

1. インターネット接続を確認

2. Gradleキャッシュをクリア:
   ```bash
   ./gradlew clean
   rm -rf .gradle
   ```

3. Android Studioのキャッシュをクリア:
   ```
   File → Invalidate Caches / Restart → Invalidate and Restart
   ```

### ビルドエラー

**エラー**: "Compilation failed"

**解決方法**:

1. **Build → Clean Project**を実行
2. **Build → Rebuild Project**を実行
3. Kotlin Compilerのバージョンを確認

### 依存関係の問題

**エラー**: "Could not resolve dependencies"

**解決方法**:

1. `build.gradle`のリポジトリ設定を確認:
   ```gradle
   repositories {
       google()
       mavenCentral()
   }
   ```

2. 依存関係を更新:
   ```bash
   ./gradlew build --refresh-dependencies
   ```

### エミュレーターが起動しない

**問題**: エミュレーターが起動しない、または非常に遅い

**解決方法**:

1. **HAXM（Intel）またはHypervisor（AMD）が有効か確認**:
   - BIOSで仮想化支援機能を有効化

2. **エミュレーターの設定を変更**:
   - Device Manager → デバイスの編集 → Show Advanced Settings
   - RAM: 2048MB以上
   - Graphics: Hardware - GLES 2.0

3. **別のシステムイメージを試す**:
   - x86_64イメージを使用（ARMより高速）

### 実機で認識されない

**問題**: デバイスが「adb devices」に表示されない

**解決方法**:

1. **USBドライバーのインストール**（Windows）:
   - デバイスメーカーの公式サイトからダウンロード

2. **USBデバッグの再有効化**:
   - デバイスの開発者向けオプションでUSBデバッグをOFF/ON

3. **adbサーバーの再起動**:
   ```bash
   adb kill-server
   adb start-server
   adb devices
   ```

### 権限エラー

**エラー**: "Permission denied" when running gradlew

**解決方法**:
```bash
chmod +x gradlew
```

### メモリ不足エラー

**エラー**: "Out of memory" during build

**解決方法**:

`gradle.properties`ファイルを編集:
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

## ビルド時間の最適化

### Gradle Daemonの有効化

`gradle.properties`に追加:
```properties
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.configureondemand=true
```

### ビルドキャッシュの有効化

`gradle.properties`に追加:
```properties
org.gradle.caching=true
```

## 検証とテスト

### ビルドの検証

```bash
# プロジェクトのクリーンビルド
./gradlew clean build

# Lintチェック
./gradlew lint

# テストの実行
./gradlew test
```

### APKの検証

```bash
# APKの内容を確認
unzip -l app/build/outputs/apk/debug/app-debug.apk

# APKのサイズを確認
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

## 開発のヒント

### ログの確認

Android Studioの「Logcat」タブでログを確認できます：

```kotlin
// コード内でログを出力
import android.util.Log

Log.d("MusicVisualizer", "Debug message")
Log.e("MusicVisualizer", "Error message")
```

### デバッグ

1. ブレークポイントを設定:
   - コードの行番号の左側をクリック

2. デバッグモードで実行:
   - Run → Debug 'app'

3. 変数の値を確認:
   - デバッガーウィンドウで確認

### ホットリロード

Jetpack Composeは、UIの変更を即座に反映できます：

1. コードを変更
2. **Ctrl+S**（Windows/Linux）または**Cmd+S**（macOS）で保存
3. 変更が自動的に反映される

## 参考リンク

- [Android Studio公式ドキュメント](https://developer.android.com/studio/intro)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Android Visualizer API](https://developer.android.com/reference/android/media/audiofx/Visualizer)
- [MediaPlayer API](https://developer.android.com/reference/android/media/MediaPlayer)

---

**セットアップが完了したら、アプリを楽しんでください！ 🎉**
