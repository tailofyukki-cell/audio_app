# 📚 使用ライブラリとAPI

このドキュメントでは、Music Visualizerアプリで使用しているライブラリとAPIについて詳しく説明します。

## 📦 使用ライブラリ一覧

### コアライブラリ

#### 1. AndroidX Core KTX
```gradle
implementation 'androidx.core:core-ktx:1.12.0'
```

**用途**: Android APIのKotlin拡張関数を提供

**主な機能**:
- Kotlinの拡張関数でAndroid APIを使いやすく
- より簡潔なコード記述が可能

**使用例**:
```kotlin
// 通常のAndroid API
val color = ContextCompat.getColor(context, R.color.primary)

// KTX拡張
val color = context.getColor(R.color.primary)
```

---

#### 2. Lifecycle Runtime KTX
```gradle
implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.6.2'
```

**用途**: ライフサイクル対応のコルーチンスコープを提供

**主な機能**:
- `lifecycleScope`でライフサイクルに応じた非同期処理
- メモリリークの防止

**使用例**:
```kotlin
lifecycleScope.launch {
    // ライフサイクルに応じて自動的にキャンセル
    delay(1000)
}
```

---

### Jetpack Compose

#### 3. Compose BOM (Bill of Materials)
```gradle
implementation platform('androidx.compose:compose-bom:2023.10.01')
```

**用途**: Composeライブラリのバージョン管理

**主な機能**:
- 互換性のあるComposeライブラリのバージョンを一括管理
- 個別にバージョンを指定する必要がない

---

#### 4. Compose UI
```gradle
implementation 'androidx.compose.ui:ui'
implementation 'androidx.compose.ui:ui-graphics'
implementation 'androidx.compose.ui:ui-tooling-preview'
```

**用途**: Jetpack Composeの基本UI機能

**主な機能**:
- 宣言的UIの構築
- Canvasによるカスタム描画
- プレビュー機能

**使用例**:
```kotlin
@Composable
fun MyScreen() {
    Column {
        Text("Hello, Compose!")
        Button(onClick = { }) {
            Text("Click me")
        }
    }
}
```

---

#### 5. Material 3
```gradle
implementation 'androidx.compose.material3:material3'
implementation 'androidx.compose.material:material'
implementation 'androidx.compose.material:material-icons-extended'
```

**用途**: Material Design 3のコンポーネント

**主な機能**:
- Button、Card、Sliderなどのマテリアルコンポーネント
- ダークテーマ対応
- アイコンセット

**使用例**:
```kotlin
Button(
    onClick = { },
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary
    )
) {
    Text("Material Button")
}
```

---

#### 6. Activity Compose
```gradle
implementation 'androidx.activity:activity-compose:1.8.1'
```

**用途**: ActivityとComposeの統合

**主な機能**:
- `setContent`でComposeを使用
- ActivityResultの統合

**使用例**:
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}
```

---

### ViewModel

#### 7. Lifecycle ViewModel Compose
```gradle
implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2'
```

**用途**: ComposeでのViewModel統合

**主な機能**:
- `viewModel()`関数でViewModelを取得
- ライフサイクルに応じた状態管理

**使用例**:
```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel = viewModel()) {
    val state by viewModel.state
    Text(text = state)
}
```

---

### 権限管理

#### 8. Accompanist Permissions
```gradle
implementation 'com.google.accompanist:accompanist-permissions:0.32.0'
```

**用途**: Composeでの実行時権限管理

**主な機能**:
- 宣言的な権限リクエスト
- 権限状態の監視

**使用例**:
```kotlin
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreen() {
    val permissionState = rememberPermissionState(
        Manifest.permission.RECORD_AUDIO
    )
    
    if (permissionState.status.isGranted) {
        Text("Permission granted")
    } else {
        Button(onClick = { permissionState.launchPermissionRequest() }) {
            Text("Request permission")
        }
    }
}
```

---

### Activity Result API

#### 9. Activity KTX
```gradle
implementation 'androidx.activity:activity-ktx:1.8.1'
```

**用途**: ActivityResult APIのKotlin拡張

**主な機能**:
- ファイルピッカーなどの結果を簡単に取得
- タイプセーフなAPI

**使用例**:
```kotlin
val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument()
) { uri: Uri? ->
    uri?.let { 
        // ファイルが選択された
    }
}

Button(onClick = { launcher.launch(arrayOf("audio/*")) }) {
    Text("Select Music")
}
```

---

## 🎵 Android API

### 1. MediaPlayer

**パッケージ**: `android.media.MediaPlayer`

**用途**: 音楽ファイルの再生

**主な機能**:
- 音楽ファイルの読み込みと再生
- 再生・一時停止・停止
- シーク操作
- 音量調整

**使用例**:
```kotlin
val mediaPlayer = MediaPlayer().apply {
    setDataSource(context, uri)
    prepare()
    start()
}
```

**ライフサイクル**:
```
Idle → Initialized → Prepared → Started → Paused → Stopped → Released
```

**注意点**:
- 使用後は必ず`release()`を呼ぶ
- 非同期で準備する場合は`prepareAsync()`を使用

---

### 2. Visualizer

**パッケージ**: `android.media.audiofx.Visualizer`

**用途**: 音声データの可視化

**主な機能**:
- 波形データの取得
- FFTデータの取得
- リアルタイムでのデータキャプチャ

**使用例**:
```kotlin
val visualizer = Visualizer(audioSessionId).apply {
    captureSize = Visualizer.getCaptureSizeRange()[1]
    
    setDataCaptureListener(
        object : Visualizer.OnDataCaptureListener {
            override fun onWaveFormDataCapture(
                visualizer: Visualizer,
                waveform: ByteArray,
                samplingRate: Int
            ) {
                // 波形データを処理
            }
            
            override fun onFftDataCapture(
                visualizer: Visualizer,
                fft: ByteArray,
                samplingRate: Int
            ) {
                // FFTデータを処理
            }
        },
        Visualizer.getMaxCaptureRate(),
        true,  // waveform
        false  // fft
    )
    
    enabled = true
}
```

**注意点**:
- `RECORD_AUDIO`権限が必要
- 使用後は必ず`release()`を呼ぶ
- オーディオセッションIDが必要

---

### 3. Storage Access Framework

**パッケージ**: `android.content.Intent`

**用途**: ファイルの選択

**主な機能**:
- ファイルピッカーの表示
- ファイルへのアクセス

**使用例**:
```kotlin
val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
    addCategory(Intent.CATEGORY_OPENABLE)
    type = "audio/*"
}
startActivityForResult(intent, REQUEST_CODE)
```

**Composeでの使用**:
```kotlin
val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument()
) { uri: Uri? ->
    // ファイルが選択された
}

launcher.launch(arrayOf("audio/*"))
```

---

## 🔧 ビルドツール

### Gradle

**バージョン**: 8.0

**用途**: ビルド自動化

**主な機能**:
- 依存関係の管理
- ビルドの自動化
- マルチモジュールのサポート

---

### Android Gradle Plugin

**バージョン**: 8.1.4

**用途**: AndroidアプリのビルドをGradleで実行

**主な機能**:
- APKの生成
- リソースの処理
- コード圧縮（ProGuard/R8）

---

### Kotlin Gradle Plugin

**バージョン**: 1.9.20

**用途**: KotlinコードのコンパイルをGradleで実行

**主な機能**:
- Kotlinコードのコンパイル
- Kotlin拡張のサポート

---

## 📊 依存関係グラフ

```
MusicVisualizerApp
├── AndroidX Core KTX
├── Lifecycle Runtime KTX
│   └── Coroutines
├── Activity Compose
│   ├── Activity KTX
│   └── Compose UI
├── Compose BOM
│   ├── Compose UI
│   ├── Compose Material 3
│   └── Compose Material Icons
├── Lifecycle ViewModel Compose
│   └── ViewModel
└── Accompanist Permissions
    └── Compose Runtime
```

---

## 🔒 権限の詳細

### RECORD_AUDIO

**用途**: 音声データの可視化

**理由**: Visualizer APIを使用するために必要

**データの取り扱い**:
- 音声データは録音・保存されません
- メモリ上でのみ処理
- 外部への送信なし

---

### READ_MEDIA_AUDIO (Android 13+)

**用途**: 音楽ファイルの読み込み

**理由**: 端末内の音楽ファイルにアクセスするために必要

**データの取り扱い**:
- ユーザーが選択したファイルのみアクセス
- ファイルの内容は変更されません

---

### READ_EXTERNAL_STORAGE (Android 12以前)

**用途**: 音楽ファイルの読み込み

**理由**: 端末内の音楽ファイルにアクセスするために必要

**データの取り扱い**:
- ユーザーが選択したファイルのみアクセス
- ファイルの内容は変更されません

---

## 📝 ライセンス

### オープンソースライブラリのライセンス

- **AndroidX**: Apache License 2.0
- **Jetpack Compose**: Apache License 2.0
- **Accompanist**: Apache License 2.0
- **Kotlin**: Apache License 2.0

### Android API

- **MediaPlayer**: Android Open Source Project (AOSP)
- **Visualizer**: Android Open Source Project (AOSP)

---

## 🔄 更新とメンテナンス

### ライブラリの更新

依存関係を最新に保つために、定期的に更新を確認してください：

```bash
# 依存関係の更新確認
./gradlew dependencyUpdates
```

### 非推奨APIの確認

```bash
# Lintチェック
./gradlew lint
```

---

## 📚 参考資料

- [AndroidX リリースノート](https://developer.android.com/jetpack/androidx/versions)
- [Jetpack Compose リリースノート](https://developer.android.com/jetpack/androidx/releases/compose)
- [Accompanist ドキュメント](https://google.github.io/accompanist/)
- [Android API リファレンス](https://developer.android.com/reference)

---

**すべてのライブラリは、Androidアプリ開発のベストプラクティスに従って選定されています。**
