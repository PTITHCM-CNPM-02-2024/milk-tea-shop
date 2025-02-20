# Convention

## Description

- Đây là thư mục chứa `convention plugin` cho toàn bộ dự án.

## Cấu trúc thư mục

```bash
tree -L4
./   ~/W/M/milk-tea-shop/b/build-l/convention    dev +216 !3 ?9  tree ./ -L4      ✔  08:28:04  
├── build
│   ├── classes
│   │   └── kotlin
│   │       └── main
│   ├── generated-sources
│   │   ├── kotlin-dsl-accessors
│   │   │   └── kotlin
│   │   ├── kotlin-dsl-external-plugin-spec-builders
│   │   │   └── kotlin
│   │   └── kotlin-dsl-plugins
│   │       └── kotlin
│   ├── kotlin
│   │   └── compileKotlin
│   │       ├── cacheable
│   │       └── classpath-snapshot
│   ├── kotlin-dsl
│   │   ├── plugins-blocks
│   │   │   ├── compiled
│   │   │   └── extracted
│   │   └── precompiled-script-plugins-metadata
│   │       ├── accessors
│   │       └── plugin-spec-builders
│   ├── libs
│   │   ├── convention-1.0-SNAPSHOT.jar
│   │   └── convention.jar
│   ├── pluginDescriptors
│   │   ├── mts-backend.java-application-convention.properties
│   │   ├── mts-backend.java-convention.properties
│   │   └── mts-backend.java-library-convention.properties
│   ├── resources
│   │   └── main
│   │       └── META-INF
│   └── tmp
│       ├── generatePrecompiledScriptPluginAccessors
│       │   ├── accessors14539699338627889597
│       │   ├── accessors15759539289357372918
│       │   └── accessors4423988281078512546
│       └── jar
│           └── MANIFEST.MF
├── build.gradle.kts
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── HELP.md
├── settings.gradle.kts
└── src
    ├── main
    │   ├── kotlin
    │   │   ├── mts-backend.java-application-convention.gradle.kts
    │   │   ├── mts-backend.java-convention.gradle.kts
    │   │   └── mts-backend.java-library-convention.gradle.kts
    │   └── resources
    └── test
        ├── java
        ├── kotlin
        └── resources
```

## Source code

- Source code của `convention plugin` được chứa trong thư mục `src/main/kotlin`:
  - `mts-backend.java-application-convention.gradle.kts`: Plugin cấu hình cho module `bootstrap`.
  - `mts-backend.java-convention.gradle.kts`: Plugin cấu hình cho toàn bộ dự án.
  - `mts-backend.java-library-convention.gradle.kts`: Plugin cấu hình cho module `domain`, `infrastructure`, `application`.