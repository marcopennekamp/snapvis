# Calculator for reverse Polish notation

### Overview

This RPN calculator is an example project to use with the CPU snapshot visualizer plugin. It is a port of a very similar example I've written for my programming language Lore: [https://github.com/marcopennekamp/lore/tree/master/test/calculator](https://github.com/marcopennekamp/lore/tree/master/test/calculator). 

The motivation for building this example project is twofold: (1) it allowed me to get acquainted with Kotlin before diving into development of the IntelliJ plugin, and (2) I wanted to develop the plugin while having access to a non-trivial example project that I have full control over and knowledge of.

The calculator is not intended to be used from the command line. Its `main` function in `calculator.Calculator` should be run with IntelliJ's CPU profiler, or another profiler that can create a snapshot.



### Arithmetic Expressions

The calculator can parse and evaluate strings that contain arithmetic expressions in [reverse Polish notation](https://en.wikipedia.org/wiki/Reverse_Polish_notation).

##### Syntax

- *Numbers* are real-valued and have the following format: `[0-9]+(\.[0-9]*)?`.
- *Operators*:
  - Addition: `+`
  - Subtraction: `-`
  - Multiplication: `*`
  - Division: `/`

All operators are binary and follow their operands. Whitespace is not significant and only used to separate numbers.

##### Evaluation

Evaluation of an expression is stack-based. Initially, the operand stack is empty. When the evaluator encounters a number, it is placed on the operand stack. Operators are binary and thus consume the two topmost operands on the stack and place a single result on the stack. The operand on top of the stack is the operator's *second* operand, the order being relevant for subtraction and division.

When the operand stack has less than 2 elements and an operator is encountered, the expression is malformed and evaluation will terminate. 

The result of an expression is the top of the operand stack. If the stack is empty, the expression is malformed. If the stack contains multiple operands, the other operands apart from the top operand are ignored.

##### Examples

```
1 2 +                      // => 3
0.9 1 / 2.5 3 * +          // => 8.4
0 1 1 2 3 5 8 + + + + + +  // => 20
```
