# 🎵 Music Visualizer with Equalizer

端末内の音楽ファイルを再生し、音データに連動したビジュアライザーとグラフィックイコライザー機能を備えたAndroidアプリです。

![Platform](https://img.shields.io/badge/Platform-Android-green.svg)
![Language](https://img.shields.io/badge/Language-Kotlin-blue.svg)
![UI](https://img.shields.io/badge/UI-Jetpack%20Compose-orange.svg)
![Min SDK](https://img.shields.io/badge/Min%20SDK-29-yellow.svg)

## ✨ 主な機能

### 🎶 音楽再生機能
- **端末内の音楽ファイルを再生**: MP3、AAC、WAV、FLAC等、Androidが対応する全ての音声フォーマットに対応
- **ファイルピッカー**: 簡単に音楽ファイルを選択
- **MediaPlayer**: 高品質な音楽再生

### 🎨 4種類のビジュアライザー
1. **バービジュアライザー**: 音の振幅を縦棒グラフで表示（64本のバー）
2. **波形ビジュアライザー**: オシロスコープのような波形表示（2D波形）
3. **円形ビジュアライザー**: 360度の円形配置、レインボーカラー
4. **スペクトラムビジュアライザー**: 周波数スペクトラムを5色のグラデーションで表示

### 🎚️ グラフィックイコライザー機能（NEW!）
- **10バンドイコライザー**: 31Hz～16kHzの周波数帯域を個別に調整可能
- **7種類のプリセット**:
  - **Normal**: フラット設定
  - **Pop**: ボーカルと中高域を強調
  - **Rock**: 低域と高域を強調
  - **Jazz**: 中域を抑えて低域と高域を強調
  - **Classical**: クラシック音楽に最適化
  - **Dance**: 低域を強調したダンスミュージック向け
  - **Custom**: ユーザーカスタム設定
- **設定の自動保存**: 次回起動時も設定を復元
- **ON/OFF切り替え**: イコライザーの有効/無効を簡単に切り替え
- **展開/折りたたみ**: 画面を広く使えるコンパクトなデザイン

### 🎮 完全な再生コントロール
- ✅ **再生・一時停止**: 中央の大きなボタンで操作
- ✅ **停止**: 停止ボタンで完全に停止
- ✅ **シークバー**: 任意の位置に移動可能
- ✅ **再生時間表示**: 現在位置/総時間を表示
- ✅ **音量調整スライダー**: 0%～100%の範囲で調整

## 📱 動作環境

- **最小SDKバージョン**: Android 10.0 (API 29)
- **対象SDKバージョン**: Android 14 (API 34)
- **開発言語**: Kotlin
- **UIフレームワーク**: Jetpack Compose

## 🛠️ 使用技術

### コアライブラリ
- **Kotlin 1.9.20**: モダンなAndroid開発言語
- **Jetpack Compose 1.5.4**: 宣言的UIフレームワーク
- **Material Design 3**: 最新のマテリアルデザイン

### オーディオAPI
- **MediaPlayer**: 音楽ファイルの再生
- **Visualizer API**: 音声データの可視化
- **Equalizer API**: 10バンドのグラフィックイコライザー
- **AudioEffect API**: 音声エフェクト処理

### アーキテクチャ
- **MVVM Architecture**: Model-View-ViewModel パターン
- **ViewModel**: 状態管理とビジネスロジック
- **SharedPreferences**: イコライザー設定の永続化

## 🚀 セットアップ方法

### 前提条件
- Android Studio Hedgehog (2023.1.1) 以降
- JDK 17
- Android SDK 34

### ビルド手順

1. **プロジェクトを開く**
   ```
   File → Open → MusicVisualizerApp フォルダを選択
   ```

2. **Gradleの同期**
   ```
   File → Sync Project with Gradle Files
   ```

3. **ビルド**
   ```
   Build → Rebuild Project
   ```

4. **実行**
   ```
   Run → Run 'app'
   ```

### コマンドラインからのビルド

```bash
cd MusicVisualizerApp

# クリーンビルド
./gradlew clean

# デバッグAPKのビルド
./gradlew assembleDebug

# インストール
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 📖 使い方

### 初回起動

1. アプリを起動すると、権限リクエスト画面が表示されます
2. 「権限を許可」ボタンをタップ
3. **録音権限**を許可（音声データの可視化に必要）
4. **ストレージ権限**を許可（音楽ファイルの読み込みに必要）

### 音楽の再生

1. 左下の🎵アイコンをタップ
2. 端末内の音楽ファイルを選択
3. 画面上部でビジュアライザーモードを選択
4. 中央の再生ボタンをタップ

### ビジュアライザーの切り替え

画面上部の4つのチップから選択：
- **バー**: 縦棒グラフ表示
- **波形**: 波形表示
- **円形**: 円形配置
- **スペクトラム**: 周波数スペクトラム

### イコライザーの使用

1. 音楽を再生すると、ビジュアライザーの下にイコライザーパネルが表示されます
2. **ON/OFFスイッチ**でイコライザーを有効化
3. **展開ボタン**（▼）をタップしてイコライザーを展開
4. **プリセット**ドロップダウンから好みのプリセットを選択
5. または、各周波数帯域のスライダーを調整してカスタム設定を作成

#### 周波数帯域

| バンド | 周波数 | 説明 |
|--------|--------|------|
| 1 | 31Hz | 超低域（サブベース） |
| 2 | 62Hz | 低域（ベース） |
| 3 | 125Hz | 低域（キック） |
| 4 | 250Hz | 低中域 |
| 5 | 500Hz | 中域 |
| 6 | 1kHz | 中域（ボーカル） |
| 7 | 2kHz | 中高域 |
| 8 | 4kHz | 高域（明瞭度） |
| 9 | 8kHz | 高域（空気感） |
| 10 | 16kHz | 超高域 |

### 再生コントロール

- **シークバー**: ドラッグして任意の位置に移動
- **音量スライダー**: ドラッグして音量を調整
- **停止ボタン**: 再生を停止して先頭に戻る
- **再生/一時停止ボタン**: 中央の大きなボタン

## 🎯 イコライザープリセット詳細

### Normal（ノーマル）
- すべての周波数帯域がフラット（0dB）
- 原音を忠実に再生

### Pop（ポップ）
- 中高域を強調
- ボーカルが際立つ設定
- ポップミュージックに最適

### Rock（ロック）
- 低域と高域を強調
- パワフルなサウンド
- ロック、メタルに最適

### Jazz（ジャズ）
- 中域を抑えて低域と高域を強調
- 楽器の分離が良好
- ジャズ、ブルースに最適

### Classical（クラシック）
- 低域と高域を強調
- 繊細な表現
- クラシック音楽に最適

### Dance（ダンス）
- 低域を大幅に強調
- パンチのあるベース
- EDM、ダンスミュージックに最適

### Custom（カスタム）
- ユーザーが自由に調整
- 設定は自動的に保存される

## 🔧 技術的な詳細

### プロジェクト構造

```
MusicVisualizerApp/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/musicvisualizer/
│   │       │   ├── MainActivity.kt              # メインアクティビティ
│   │       │   ├── MusicPlayer.kt               # 音楽再生管理
│   │       │   ├── MusicPlayerViewModel.kt      # ViewModel
│   │       │   ├── VisualizerManager.kt         # ビジュアライザー管理
│   │       │   ├── VisualizerComponents.kt      # ビジュアライザーUI
│   │       │   ├── EqualizerManager.kt          # イコライザー管理（NEW）
│   │       │   ├── EqualizerPreferences.kt      # 設定の保存（NEW）
│   │       │   └── EqualizerUI.kt               # イコライザーUI（NEW）
│   │       ├── res/
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

### 主要クラス

#### EqualizerManager
- Android Equalizer APIのラッパークラス
- 10バンドのイコライザーを管理
- プリセットの適用
- ゲインの設定・取得

#### EqualizerPreferences
- SharedPreferencesを使用した設定の永続化
- イコライザーの有効/無効状態
- 選択されているプリセット
- カスタムゲイン設定

#### EqualizerUI
- Jetpack Composeによるイコライザーの UI
- プリセット選択ドロップダウン
- 10個の周波数帯域スライダー
- 展開/折りたたみ機能

### 使用ライブラリ

| ライブラリ | バージョン | 用途 |
|-----------|-----------|------|
| androidx.core:core-ktx | 1.12.0 | Kotlin拡張 |
| androidx.lifecycle:lifecycle-runtime-ktx | 2.6.2 | ライフサイクル管理 |
| androidx.activity:activity-compose | 1.8.1 | Compose統合 |
| androidx.compose.ui:ui | 2023.10.01 | Compose UI |
| androidx.compose.material3:material3 | 2023.10.01 | Material Design 3 |
| androidx.lifecycle:lifecycle-viewmodel-compose | 2.6.2 | ViewModel統合 |
| com.google.accompanist:accompanist-permissions | 0.32.0 | 権限管理 |

## 📝 必要な権限

### RECORD_AUDIO
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```
- 音声データの可視化に必要
- ビジュアライザーとイコライザーの動作に必須

### READ_MEDIA_AUDIO / READ_EXTERNAL_STORAGE
```xml
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
```
- 端末内の音楽ファイルの読み込みに必要

## 🎨 カスタマイズ

### イコライザープリセットの追加

`EqualizerManager.kt`の`EqualizerPreset` enumに新しいプリセットを追加：

```kotlin
enum class EqualizerPreset(val displayName: String, val gains: List<Int>) {
    // 既存のプリセット...
    YOUR_PRESET("Your Preset", listOf(0, 2, 4, 2, 0, -2, -4, -2, 0, 2))
}
```

### ビジュアライザーの追加

`VisualizerComponents.kt`に新しいComposable関数を追加し、`MainActivity.kt`の`when`文に追加：

```kotlin
@Composable
fun YourVisualizer(modifier: Modifier, data: IntArray) {
    // カスタムビジュアライザーの実装
}
```

## 🐛 トラブルシューティング

### イコライザーが動作しない

1. **権限を確認**: RECORD_AUDIO権限が許可されているか確認
2. **音楽を再生**: イコライザーは音楽再生中のみ有効
3. **ON/OFFスイッチ**: イコライザーが有効になっているか確認

### 設定が保存されない

1. **アプリを正常終了**: タスクキルではなく、バックボタンで終了
2. **ストレージ権限**: アプリのストレージ権限を確認
3. **キャッシュをクリア**: 設定 → アプリ → Music Visualizer → ストレージ → キャッシュをクリア

### 音が歪む

1. **ゲインを下げる**: すべてのバンドのゲインを下げる
2. **音量を調整**: アプリ内の音量スライダーを下げる
3. **Normalプリセット**: 一度Normalプリセットに戻す

## 🔒 セキュリティとプライバシー

### データの取り扱い

- ✅ 音楽ファイルは端末内でのみ再生
- ✅ 音声データは録音・保存されません
- ✅ データはメモリ上でのみ処理
- ✅ 外部への送信なし
- ✅ 完全にローカルで動作

## 📄 ライセンス

このプロジェクトはサンプルアプリケーションです。

## 🙏 謝辞

- Android Visualizer API
- Android Equalizer API
- Jetpack Compose
- Material Design 3

## 📞 サポート

問題や質問がある場合は、GitHubのIssuesセクションで報告してください。

## 🔄 更新履歴

### Version 2.0.0 (2025-11-12)

- **NEW**: 10バンドのグラフィックイコライザー機能
- **NEW**: 7種類のプリセット（Normal, Pop, Rock, Jazz, Classical, Dance, Custom）
- **NEW**: イコライザー設定の自動保存・復元機能
- **IMPROVED**: UIのスクロール対応
- **IMPROVED**: ドキュメントの充実

### Version 1.0.0 (2025-11-12)

- 初回リリース
- 端末内の音楽ファイル再生機能
- 4種類のビジュアライザー
- シークバーと音量調整
- Android 10以降に対応

---

**Enjoy the music visualization and equalization! 🎵✨**
