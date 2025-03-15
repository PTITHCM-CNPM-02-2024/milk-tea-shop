# Backend Folder

## Description

- Đây là thư mục chứa toàn bộ cấu trúc và logic backend cho Frontend của dự án.
- Backend xây dựng trên nền tảng Java và sử dụng build tool Gradle.

## Cấu trúc thư mục

```bash
 tree -d -L 1
.   ~/W/M/milk-tea-shop/backend    dev +219 ?1  tree -d -L 1                     ✔  09:02:03  
├── application
├── bootstrap
├── build
├── build-cache
├── build-logic
├── domain
├── gradle
├── infrastructure
└── platform

```

- Chú ý: 
  - Các thư mục `build`, `build-cache`, `build-logic` sẽ được sinh ra khi chạy lệnh build project.
  - Các thư mục `gradle` sẽ chứa các file gradle wrapper.

## Cấu trúc dự án

```bash
 ❯ ./gradlew :projects
------------------------------------------------------------
Root project 'mts_backend'
------------------------------------------------------------

Root project 'mts_backend'
No sub-projects

Included builds:

+--- Included build ':infrastructure'
+--- Included build ':build-logic'
+--- Included build ':platform'
+--- Included build ':bootstrap'
+--- Included build ':domain'
\--- Included build ':application'
```

- Cấu trúc dự án tuân thủ theo kiến trúc Modular Monolith, kết hợp với kiến trúc Clean Architecture.
- Các module chính:
  - `infrastructure`: Tuân thủ tầng `infrastructure` của Clean Architecture, chứa các cấu hình, cài đặt liên quan đến việc kết nối với các hệ thống bên ngoài như Database, Orm, Restful API, ...
  - `domain`: Tuân thủ tầng `domain` của Clean Architecture, chứa các cấu hình, cài đặt liên quan đến domain logic (nghiệp vụ phần mềm).
  - `application`: Tuân thủ tầng `application` của Clean Architecture, chứa các `use case` của dự án.
  - `build-logic`: Thực hiện `convention plugin` cho toàn bộ dự án.
  - `platform`: Thực hiện `shared platform` cho toàn bộ dự án.
  - `bootstrap`: Là `main` module của dự án, chỉ phụ thuộc vào `infrastructure`, chưa hàm `main` để chạy dự án.
