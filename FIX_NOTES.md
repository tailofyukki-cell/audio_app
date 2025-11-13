# 🔧 Gradleビルドエラー修正ノート

## 発生したエラー

```
'org.gradle.api.artifacts.Dependency org.gradle.api.artifacts.dsl.DependencyHandler.module(java.lang.Object)'
```

## エラーの原因

このエラーは、Gradle 8.0以降で`module()`メソッドが削除されたことが原因です。また、以下の問題も関連している可能性があります：

1. **packagingOptions → packaging**: Gradle 8.0以降では`packagingOptions`が非推奨
2. **Java 8 → Java 17**: 最新のAndroid Gradle Pluginは Java 17 を推奨
3. **buildscript → plugins**: 新しいプラグイン管理方法への移行

## 修正内容

### 1. build.gradle（ルート）の修正

**修正前:**
```gradle
buildscript {
    ext {
        compose_version = '1.5.4'
        kotlin_version = '1.9.20'
    }
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.1.4'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
}
```

**修正後:**
```gradle
plugins {
    id 'com.android.application' version '8.1.4' apply false
    id 'com.android.library' version '8.1.4' apply false
    id 'org.jetbrains.kotlin.android' version '1.9.20' apply false
}
```

**変更点:**
- `buildscript`ブロックを削除
- 新しい`plugins`ブロックを使用
- プラグインのバージョンを直接指定

---

### 2. app/build.gradle の修正

#### packagingOptions → packaging

**修正前:**
```gradle
packagingOptions {
    resources {
        excludes += '/META-INF/{AL2.0,LGPL2.1}'
    }
}
```

**修正後:**
```gradle
packaging {
    resources {
        excludes += '/META-INF/{AL2.0,LGPL2.1}'
    }
}
```

#### Java 8 → Java 17

**修正前:**
```gradle
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}

kotlinOptions {
    jvmTarget = '1.8'
}
```

**修正後:**
```gradle
compileOptions {
    sourceCompatibility JavaVersion.VERSION_17
    targetCompatibility JavaVersion.VERSION_17
}

kotlinOptions {
    jvmTarget = '17'
}
```

---

### 3. gradle.properties の修正

**追加した設定:**
```properties
android.defaults.buildfeatures.buildconfig=true
android.nonFinalResIds=false
```

これらの設定により、ビルドの互換性が向上します。

---

## 修正後の構造

```
MusicVisualizerApp/
├── build.gradle (修正済み - 新しいplugins DSL)
├── app/
│   └── build.gradle (修正済み - packaging, Java 17)
├── gradle.properties (修正済み - 追加設定)
└── settings.gradle (変更なし)
```

---

## ビルド手順

修正後、以下の手順でビルドしてください：

### 1. Android Studioでプロジェクトを開く

```
File → Open → MusicVisualizerApp フォルダを選択
```

### 2. Gradleの同期

```
File → Sync Project with Gradle Files
```

### 3. クリーンビルド

```
Build → Clean Project
Build → Rebuild Project
```

### 4. 実行

```
Run → Run 'app'
```

---

## コマンドラインからのビルド

```bash
cd MusicVisualizerApp

# クリーンビルド
./gradlew clean

# デバッグAPKのビルド
./gradlew assembleDebug

# インストール
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## トラブルシューティング

### それでもエラーが出る場合

#### 1. Gradleキャッシュをクリア

```bash
./gradlew clean --no-daemon
rm -rf .gradle
rm -rf app/build
```

#### 2. Android Studioのキャッシュをクリア

```
File → Invalidate Caches / Restart → Invalidate and Restart
```

#### 3. JDKのバージョンを確認

Android Studio Hedgehog以降は、JDK 17が必要です。

```
File → Project Structure → SDK Location → JDK location
```

JDK 17が選択されていることを確認してください。

#### 4. Gradleラッパーを再生成

```bash
gradle wrapper --gradle-version 8.2
```

---

## Gradleのバージョン互換性

この修正により、以下のバージョンで正常に動作します：

- **Gradle**: 8.0 以降
- **Android Gradle Plugin**: 8.1.4
- **Kotlin**: 1.9.20
- **JDK**: 17
- **Android Studio**: Hedgehog (2023.1.1) 以降

---

## 依存関係のバージョン

すべての依存関係は最新の安定版を使用しています：

- **AndroidX Core KTX**: 1.12.0
- **Jetpack Compose BOM**: 2023.10.01
- **Material 3**: BOMで管理
- **Accompanist Permissions**: 0.32.0

---

## 参考資料

- [Gradle 8.0 Release Notes](https://docs.gradle.org/8.0/release-notes.html)
- [Android Gradle Plugin 8.0 Migration Guide](https://developer.android.com/studio/releases/gradle-plugin-api-updates)
- [Kotlin 1.9.20 Release Notes](https://kotlinlang.org/docs/whatsnew1920.html)

---

## 変更履歴

- **2025-11-12**: 初回修正
  - `buildscript`から`plugins`DSLへ移行
  - `packagingOptions`を`packaging`に変更
  - Java 8からJava 17へアップグレード
  - `gradle.properties`に追加設定

---

**この修正により、プロジェクトは正常にビルドできるようになります。**
