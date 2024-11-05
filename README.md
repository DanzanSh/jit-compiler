# Research and Development

Данное исследование направлено на глубокое изучение JIT-компилятора.

Источниками данных служили:
- [Oreilly. Chapter 4. Working with the JIT Compiler](https://www.oreilly.com/library/view/java-performance-the/9781449363512/ch04.html)
- [OpenJDK. Hotspot sourcecode](https://github.com/openjdk/jdk/tree/master/src/hotspot)
- [Серия статей Java HotSpot JIT компилятор на Habr](https://habr.com/ru/articles/536288/)

## Что удалось узнать нового? (Данный раздел будет дополняться)

### Tier4CompileThreshold не равен 10000
В интернете много информации, связанной с порогом компиляции для C2-компилятора. Много источников пишут, что threshold = 10.000. 
Т.е. достаточно вызвать какой-либо метод 10.000 раз и он будет скомпилирован C2-компилятором, но это не так. 
Чтобы метод был скомпилирован C1 или C2 компилятором данное логическое выражение должно отдать истину:

_i > TierXInvocationThreshold * s || (𝑖 > TierXMinInvocationThreshold * s && i + b > TierXCompileThreshold * s)_, где

**i – количество вызовов метода,
b – количество вызовов цикла,
s – коэффициент скалирования**

_s = queue_size_x / TierXLoadFeedBack * compiler_count_x) + 1_

**Tier3InvocationThreshold=200, Tier3MinInvocationThreshold = 100, Tier3CompileThreshold = 2000,
Tier3LoadFeedback = 5
Tier4InvocationThreshold=5000, Tier4MinInvocationThreshold = 600, Tier4CompileThreshold = 15000,
Tier4LoadFeedback = 3**

### Intellij IDEA 
IDEA добавляет флаг -XX:TieredStopAtLevel=1 в JVM Option, позволяя сервисам на Spring Boot быстрее стартовать.
[Ссылка](https://youtrack.jetbrains.com/issue/IDEA-297872/IDEA-silently-adds-XXTieredStopAtLevel1-JVM-option-for-Spring-Boot-app) на обсуждение в YouTrack
Поэтому проверять performance тесты в своей IDEA плохая идея

### Code cache
TBD

## Результаты исследования, тесты и т.п. (Данный раздел будет дополняться)

В условиях с ограниченными ресурсами, когда CPU < 1, заметили, что время старта приложения линейно зависимо от количества выделенных ядер процессора.

| CPU  | JVM startUp time, s |
|------|---------------------|
| 4    | 25.35               |
| 2    | 27.72               |
| 1    | 47.495              |
| 0.5  | 108.068             |
| 0.25 | 203.794             |

Отключение TieredCompilation в условиях ограниченных ресурсов сократило время старта приложения значительно, а также уменьшило CPU throttling. 
Поэтому возникла идея сравнить производительность простого приложения с высоким потреблением CPU & IO нагрузки с различными уровнями JIT-компиляции.

В директории async-profiler можно посмотреть результаты тестов Java-приложения, замеряющие время работы сервиса при CPU & IO нагрузках 
с различными уровнями JIT-компиляции, только C1 (level = 1) vs. TieredCompilation.

Для тестирования выделялись 2 полных ядра процессора и половина ядра. 
В конечном счет, даже в условиях ограниченнхы ресурсов, CPU < 1, TieredCompilation показал лучше производительность, чем TieredStopAtLevel = 1