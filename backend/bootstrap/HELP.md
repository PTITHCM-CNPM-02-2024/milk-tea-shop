# Bootstrap

- Đây là thư mục chỉ chứa duy nhất module `bootstrap` của dự án.

## Cấu trúc thư mục

```bash
 tree -d -L 1
❯ tree -d -L 2
.   ~/W/M/milk-tea-shop/b/bootstrap    dev +220 !1 ?1  tree -d -L 2              ✔  21:11:28  
├── build
│   ├── classes
│   ├── distributions
│   ├── generated
│   ├── libs
│   ├── reports
│   ├── resources
│   ├── scripts
│   ├── test-results
│   └── tmp
├── gradle
│   └── wrapper
└── src
    ├── main
    └── test
```

- Chú ý:
  - Các thư mục `build`, `gradle` sẽ chứa các file, thư mục sinh ra khi chạy lệnh build project.
  - Các thư mục `src/main`, `src/test` chứa source code của module.
