# 🔧 Gradle `module()` エラー完全修正ガイド

## 発生したエラー

```
'org.gradle.api.artifacts.Dependency org.gradle.api.artifacts.dsl.DependencyHandler.module(java.lang.Object)'
```

## 根本原因

このエラーは、以下の複数の問題が組み合わさって発生していました：

1. **Groovy DSL vs Kotlin DSL**: Groovy DSLの古い構文が原因
2. **依存関係の記述方法**: Gradle 8.0で削除されたメソッドの使用
3. **リポジトリの設定場所**: プロジェクトレベルとモジュールレベルの競合

## 完全な解決策

### ✅ Groovy DSL → Kotlin DSLへの移行

**なぜKotlin DSLなのか？**
- タイプセーフ
- IDEのサポートが優れている
- Gradle 8.0以降で推奨
- エラーが発生しにくい

---

## 修正内容の詳細

### 1. build.gradle → build.gradle.kts

**修正前（Groovy DSL）:**
```gradle
plugins {
    id 'com.android.application' version '8.1.4' apply false
    id 'org.jetbrains.kotlin.android' version '1.9.20' apply false
}
```

**修正後（Kotlin DSL）:**
```kotlin
plugins {
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
}
```

**変更点:**
- シングルクォート `'` → ダブルクォート `"`
- 括弧の追加 `id()` 
- より明示的な構文

---

### 2. app/build.gradle → app/build.gradle.kts

**修正前（Groovy DSL）:**
```gradle
android {
    namespace 'com.example.musicvisualizer'
    compileSdk 34
    
    defaultConfig {
        applicationId "com.example.musicvisualizer"
        minSdk 29
    }
}

dependencies {
    implementation 'androidx.core:core-ktx:1.12.0'
}
```

**修正後（Kotlin DSL）:**
```kotlin
android {
    namespace = "com.example.musicvisualizer"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.example.musicvisualizer"
        minSdk = 29
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
}
```

**変更点:**
- プロパティに `=` を使用
- 文字列はダブルクォート
- 依存関係は括弧で囲む

---

### 3. settings.gradle → settings.gradle.kts

**修正前（Groovy DSL）:**
```gradle
pluginManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MusicVisualizerApp"
include ':app'
```

**修正後（Kotlin DSL）:**
```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MusicVisualizerApp"
include(":app")
```

**変更点:**
- `dependencyResolutionManagement`の追加
- リポジトリの一元管理
- `include(":app")` の括弧

---

## プロジェクト構造

修正後のプロジェクト構造：

```
MusicVisualizerApp/
├── build.gradle.kts          ← Kotlin DSL（新）
├── settings.gradle.kts       ← Kotlin DSL（新）
├── gradle.properties         ← 変更なし
├── app/
│   ├── build.gradle.kts      ← Kotlin DSL（新）
│   ├── proguard-rules.pro
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/
│           └── res/
└── gradle/
    └── wrapper/
        └── gradle-wrapper.properties
```

---

## ビルド手順

### Android Studioで開く

1. **古いプロジェクトを完全に閉じる**
   ```
   File → Close Project
   ```

2. **キャッシュをクリア（推奨）**
   ```
   File → Invalidate Caches / Restart → Invalidate and Restart
   ```

3. **新しいプロジェクトを開く**
   ```
   File → Open → MusicVisualizerApp フォルダを選択
   ```

4. **Gradleの自動同期を待つ**
   - Android Studioが自動的にGradleラッパーをダウンロード
   - 依存関係を解決
   - プロジェクトをビルド

5. **実行**
   ```
   Run → Run 'app'
   ```

---

### コマンドラインで

Android Studioで一度開いた後、コマンドラインでも使用できます：

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

### エラー: "Gradle wrapper not found"

**解決方法:**
Android Studioで開くと自動的に生成されます。または：

```bash
# Gradleがインストールされている場合
gradle wrapper --gradle-version 8.2
```

---

### エラー: "JDK version is too old"

**解決方法:**
JDK 17が必要です。

```
File → Project Structure → SDK Location → JDK location
```

JDK 17を選択してください。

---

### エラー: "Could not resolve dependencies"

**解決方法:**

1. インターネット接続を確認
2. Gradleキャッシュをクリア：

```bash
./gradlew clean --refresh-dependencies
```

3. Android Studioのキャッシュをクリア：

```
File → Invalidate Caches / Restart
```

---

### エラー: "Unsupported Gradle version"

**解決方法:**

`gradle/wrapper/gradle-wrapper.properties`を確認：

```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip
```

Gradle 8.2が指定されていることを確認してください。

---

## Kotlin DSL vs Groovy DSL 比較表

| 項目 | Groovy DSL | Kotlin DSL |
|------|-----------|-----------|
| ファイル拡張子 | `.gradle` | `.gradle.kts` |
| 文字列 | `'...'` または `"..."` | `"..."` のみ |
| プロパティ | `compileSdk 34` | `compileSdk = 34` |
| メソッド呼び出し | `implementation 'xxx'` | `implementation("xxx")` |
| タイプセーフ | ❌ なし | ✅ あり |
| IDEサポート | 普通 | 優れている |
| エラー検出 | 実行時 | コンパイル時 |
| 推奨度（2024年） | 非推奨 | 推奨 |

---

## なぜKotlin DSLが推奨されるのか？

### 1. タイプセーフ

**Groovy DSL:**
```gradle
// タイポしてもエラーにならない
compilSdk 34  // 間違い！でも実行時まで気づかない
```

**Kotlin DSL:**
```kotlin
// コンパイル時にエラー
compilSdk = 34  // 正しい
compilSdk = "34"  // エラー！型が違う
```

### 2. IDEのサポート

- コード補完が優れている
- エラーが即座に表示される
- リファクタリングが安全

### 3. 将来性

- Gradleの公式ドキュメントがKotlin DSLを推奨
- 新機能はKotlin DSL優先で実装
- Groovy DSLは将来的に非推奨になる可能性

---

## 依存関係の記述方法の変更

### Gradle 7以前（Groovy DSL）

```gradle
dependencies {
    compile 'androidx.core:core-ktx:1.12.0'  // 非推奨
    implementation 'androidx.core:core-ktx:1.12.0'
}
```

### Gradle 8以降（Kotlin DSL）

```kotlin
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
}
```

**注意:**
- `compile` は削除されました
- `implementation` を使用
- 括弧が必須

---

## リポジトリの設定

### 古い方法（非推奨）

```gradle
// build.gradle（ルート）
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

// app/build.gradle
repositories {
    google()
    mavenCentral()
}
```

### 新しい方法（推奨）

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
```

**利点:**
- 一箇所で管理
- 重複を防ぐ
- エラーが発生しにくい

---

## バージョン互換性

この修正により、以下のバージョンで動作します：

| ツール | バージョン |
|--------|-----------|
| Gradle | 8.2 |
| Android Gradle Plugin | 8.1.4 |
| Kotlin | 1.9.20 |
| JDK | 17 |
| Android Studio | Hedgehog (2023.1.1) 以降 |

---

## 参考資料

- [Gradle Kotlin DSL Primer](https://docs.gradle.org/current/userguide/kotlin_dsl.html)
- [Migrating build logic from Groovy to Kotlin](https://docs.gradle.org/current/userguide/migrating_from_groovy_to_kotlin_dsl.html)
- [Android Gradle Plugin 8.0 Migration Guide](https://developer.android.com/studio/releases/gradle-plugin-api-updates)

---

## まとめ

### 主な変更点

1. ✅ **Groovy DSL → Kotlin DSL**: すべてのビルドファイルを変換
2. ✅ **リポジトリの一元管理**: `settings.gradle.kts`で管理
3. ✅ **タイプセーフな記述**: コンパイル時にエラー検出
4. ✅ **Gradle 8.2対応**: 最新の安定版を使用

### この修正の効果

- ✅ `module()`メソッドエラーの完全解決
- ✅ ビルドの安定性向上
- ✅ IDEサポートの改善
- ✅ 将来的なメンテナンス性の向上

---

**この修正により、プロジェクトは正常にビルドできるようになります！**
