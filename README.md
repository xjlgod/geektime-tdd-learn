# 《极客时间|徐昊·TDD 项目实战 70 讲》个人实现

[极客时间专栏地址](http://gk.link/a/11giR)

## 练习一：命令行参数解析
练习源自 Robert C. Martin 的 *Clean Code* 第十四章的一个例子。需求描述如下：
> 我们中的大多数人都不得不时不时地解析一下命令行参数。如果我们没有一个方便的工具，那么我们就简单地处理一下传入 main 函数的字符串数组。有很多开源工具可以完成这个任务，但它们可能并不能完全满足我们的要求。所以我们再写一个吧。　
 > 
> 传递给程序的参数由标志和值组成。标志应该是一个字符，前面有一个减号。每个标志都应该有零个或多个与之相关的值。例如：　
 > 
> -l -p 8080 -d /usr/logs　
 > 
> “l”（日志）没有相关的值，它是一个布尔标志，如果存在则为 true，不存在则为 false。“p”（端口）有一个整数值，“d”（目录）有一个字符串值。标志后面如果存在多个值，则该标志表示一个列表：　
 > 
> -g this is a list -d 1 2 -3 5　"g"表示一个字符串列表[“this”, “is”, “a”, “list”]，“d"标志表示一个整数列表[1, 2, -3, 5]。　
 > 
> 如果参数中没有指定某个标志，那么解析器应该指定一个默认值。例如，false 代表布尔值，0 代表数字，”"代表字符串，[]代表列表。如果给出的参数与模式不匹配，重要的是给出一个好的错误信息，准确地解释什么是错误的。　
 > 
> 确保你的代码是可扩展的，即如何增加新的数值类型是直接和明显的。

## 练习二：DI容器
### 功能
* 一个依赖注入容器（Dependency Injection Container/IoC Container）
![img.png](img.png)
### 目标：
* 以 Jakarta EE 中的 Jakarta Dependency Injection 为主要功能参考，并对其适当简化
* 依赖注入容器的大致功能
### 参考：
关于依赖注入的来龙去脉可以参看 Martin Fowler 在 2004 年写的文章 《IoC 容器与依赖注入模式》：
## 参考实现：
[万有引力笔记](https://www.wyyl1.com/)