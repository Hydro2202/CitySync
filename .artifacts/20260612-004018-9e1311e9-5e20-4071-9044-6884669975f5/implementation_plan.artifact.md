# Fix AAR Metadata Compilation Error

Update the project to compile against Android API 37 to satisfy the requirements of `androidx.core:core:1.19.0`.

## Proposed Changes

### Build Configuration

#### [build.gradle.kts](file:///D:/CitySync/app/build.gradle.kts)

- Update `compileSdk` to 37.
- Update `targetSdk` to 37.

```diff
     namespace = "com.example.citysync"
-    compileSdk {
-        version = release(36) {
-            minorApiLevel = 1
-        }
-    }
+    compileSdk = 37

     defaultConfig {
         applicationId = "com.example.citysync"
         minSdk = 24
-        targetSdk = 36
+        targetSdk = 37
         versionCode = 1
```

## Verification Plan

### Automated Tests
- Run `./gradlew :app:checkDebugAarMetadata` to ensure the specific task that failed now passes.
- Run `./gradlew assembleDebug` to ensure the whole project builds.

### Manual Verification
- Perform a Gradle Sync in the IDE.
