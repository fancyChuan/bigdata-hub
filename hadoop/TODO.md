## TODO

- 从源码编译按照hadoop的细节（动手实操一遍）

一些思考题：
- Fsimage中没有记录块所对应DataNode，为什么？
- NameNode如何确定下次开机启动的时候合并哪些Edits？


体会妙处：
- CombineTextInputFormat用于合并小文件形成更合适的切片。设置最大值后，为什么要跟2倍做比较。好好体会下，这个设计是挺妙的
