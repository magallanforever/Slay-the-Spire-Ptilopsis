# Tests

这个目录目前放轻量级本地检查脚本。

## run-source-smoke.ps1

执行 Maven 编译，并确认关键类能从 `src/main/java` 重新生成：

```powershell
./tests/run-source-smoke.ps1
```

它不会启动游戏，也不会复制 jar 到游戏目录；完整打包仍使用：

```powershell
mvn package
```
