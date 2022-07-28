TODO:

- Actually write this README.
- Add an architecture overview.
- Do we have to support operators separately from function calls?
  - Maybe define this as a limitation/trade-off.
- Test inner classes vis-à-vis class name handling.







Features:

- Load a `.jfr` CPU snapshot created by e.g. async-profiler or IntelliJ.
- Annotate each method call with an inlay hint showing the average run time.
- The Tools->Snapvis menu provides the following actions:
  - *Load CPU Snapshot:* Select and load a `.jfr` snapshot.


Choices:

- The plugin is written in Kotlin and targets IntelliJ 2022.2 and later, using JDK 17.
- Only Kotlin highlighting is supported. Java support could be added later.
- The plugin supports the JFR file format created by the IntelliJ profiler / async-profiler. I believe async-profiler is a good first choice, because it's open source and integrated well with IntelliJ. Other formats can be supported later. I've avoided using proprietary profilers due to their limited trial periods.
- Method execution time is reported with an inline hint at the call site. Hints are only added to project files. Support for third-party files (such as library sources) could be added later. 


Tradeoffs:

- JFR format:
  - Sampling doesn't seem to be ideal for collecting the per-call execution time of methods. The higher the sampling rate, the smaller the error, but instrumentation might overall provide more consistent results, albeit with a larger overhead. However, as noted above, the JFR format is a first choice because of the open-source async-profiler and its IntelliJ integration. Therefore, it's important to be aware of the following tradeoffs:
    - Calls to small methods might be missed altogether. In such cases, the plugin would not provide a hint for the method call.
    - If a method call appears in two adjacent stack traces, the plugin must assume that the second call is still the same call as the first one. However, in some cases, between the two sampling points, the method might have finished and been called again from the same location (even multiple times). The plugin doesn't have enough information to separate the method calls, so an error will be introduced into the per-call execution time.
  - JFR stack trace locations carry line numbers, but not column numbers. This makes it impossible to easily differentiate between two calls to the same method on the same line. It's probably possible to use the bytecode index of the location to disambiguate between two method calls, but this would require the plugin to know the bytecode index of a method call (so that, when a method call is encountered in the PSI, the correct execution time can be queried). For the sake of simplicity, I'll group method calls by line and accept the inaccuracy for this edge case.
- When the user changes a file, the line numbers from the snapshot and the line numbers from the editor will become misaligned. A solution to this seems non-trivial, so I'd like to simplify the requirements here:
  - If a file is changed, the hints of misaligned lines will disappear.
  - Hints will disappear when the user closes the editor, and the user will have to load the snapshot again.
  - Method calls that have been added after loading a snapshot will not be annotated with a hint.
- The Kotlin file to Java `.class` file mapping is not one-to-one. The snapshot might contain class names which need some processing until they are recognizable by the plugin, such as normalizing class names with `$` (e.g. `calculator.Tokenizer$tryOperand$1$2` to `calculator.Tokenizer`). Conversely, a Kotlin project file might contain package-level functions, which require special class name handling as per the documentation: https://kotlinlang.org/docs/java-to-kotlin-interop.html#package-level-functions. To keep the task simple in this area, only absolutely necessary normalization and support for package-level functions will be implemented.


Additional Feature Ideas:

- Add an item to the right-click menu of the hint that allows clearing the current snapshot, to remove all hints until the next snapshot has been loaded.
- When the caret is placed inside the method's name, additionally show minimum and maximum run time.
  - Whether this information is useful heavily depends on the sampling interval. If the sampling interval is 1ms, for example, there isn't nearly enough information available to provide meaningful minimum and maximum run times.
- Add settings for absolute and relative thresholds. If a method call took less than x nanoseconds or less than x% of total time, hide the hint unless the caret is placed inside the method's name.
