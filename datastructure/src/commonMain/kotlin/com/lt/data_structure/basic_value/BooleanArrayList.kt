/*
 * Copyright lt 2023
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lt.data_structure.basic_value

private typealias BasicBoolean = Boolean

/**
 * creator: lt  2021/11/10  lt.dygzs@qq.com
 * effect : 性能更好的ArrayList<Boolean>,线程不安全
 * warning:[initSize]初始化容量
 * ps:json无法转化成[],但可以调用toString()
 */
class BooleanArrayList(initSize: Int = 0) : RandomAccess {
    constructor(booleanArray: BooleanArray) : this(booleanArray.size) {
        data = booleanArray.copyOf()
        size = data.size
    }

    constructor(booleanArrayList: BooleanArrayList) : this(booleanArrayList.data.copyOf(booleanArrayList.size))

    constructor(list: Collection<BasicBoolean>) : this(list.size) {
        list.forEach(::add)
    }

    //内部数据
    private var data: BooleanArray = BooleanArray(initSize) { false }

    /**
     * 获取内部的总数量
     */
    var size: Int = 0
        private set

    /**
     * 获取数据
     */
    operator fun get(index: Int): BasicBoolean {
        if (index >= size)
            throw IndexOutOfBoundsException("size = $size ,the index = $index")
        return data[index]
    }

    /**
     * 获取数据,如果索引越界,就返回else的返回值
     */
    inline fun getOrElse(index: Int, defaultValue: () -> BasicBoolean): BasicBoolean {
        if (index !in 0 until size)
            return defaultValue()
        return get(index)
    }

    fun getOrElse(index: Int, defaultValue: BasicBoolean): BasicBoolean {
        if (index !in 0 until size)
            return defaultValue
        return get(index)
    }

    /**
     * 获取数据,如果索引越界,就返回null
     */
    fun getOrNull(index: Int): BasicBoolean? {
        if (index !in 0 until size)
            return null
        return get(index)
    }

    /**
     * 添加数据
     * 扩容机制:容量翻倍
     */
    fun add(element: BasicBoolean) {
        if (size == data.size)
            data = data.copyOf(if (data.isEmpty()) 10 else data.size * 2)
        data[size] = element
        size++
    }

    /**
     * 根据数据移除
     */
    fun removeElement(element: BasicBoolean) {
        val indexOf = indexOf(element)
        if (indexOf >= 0) {
            removeAtIndex(indexOf)
        }
    }

    /**
     * 根据索引移除
     */
    fun removeAtIndex(index: Int) {
        if (index >= size)
            throw IndexOutOfBoundsException("size = $size ,the index = $index")
        //将需要移除的索引的后面数据往前移
        repeat(size - index - 1) {
            //将后面索引的内容前移
            data[it + index] = data[it + index + 1]
        }
        size--
    }

    /**
     * 移除第一个位置
     */
    fun removeFirst() = removeAtIndex(0)

    /**
     * 移除最后一个位置
     */
    fun removeLast() = removeAtIndex(size - 1)

    /**
     * 设置某个索引的数据
     */
    operator fun set(index: Int, element: BasicBoolean): BasicBoolean {
        if (index >= size)
            throw IndexOutOfBoundsException("size = $size ,the index = $index")
        val oldElement = get(index)
        data[index] = element
        return oldElement
    }

    /**
     * 如果[index]没有超过size就设置,否则丢弃该次修改
     */
    fun setOrDiscard(index: Int, element: BasicBoolean) {
        if (index >= size || index < 0) return
        set(index, element)
    }

    /**
     * 获取内部是否没有数据
     */
    fun isEmpty(): Boolean = size == 0

    /**
     * 获取对应数据的索引,如果没有则返回-1
     */
    fun indexOf(element: BasicBoolean): Int {
        forEachIndexed { index, datum ->
            if (element == datum)
                return index
        }
        return -1
    }

    /**
     * 从后往前获取对应数据的索引,如果没有则返回-1
     */
    fun lastIndexOf(element: BasicBoolean): Int {
        forEachReversedIndexed { index, datum ->
            if (element == datum)
                return index
        }
        return -1
    }

    /**
     * 获取是否存在对应数据
     */
    operator fun contains(element: BasicBoolean): Boolean = indexOf(element) >= 0

    /**
     * 获取包装类型迭代器
     */
    operator fun iterator(): BooleanMutableIterator = BooleanMutableIterator()

    /**
     * 获取基础类型迭代器,相对于[iterator]方法,效率更高
     */
    fun iteratorWithBasic(): BasicBooleanMutableIterator = BasicBooleanMutableIterator()

    /**
     * 遍历的方法,inline后是基础类型,如果无法inline,则使用[forEachWithBasic]系列方法
     * ps:使用forEach系列比for性能好(因为迭代器的next()返回的是对象)
     */
    inline fun forEach(action: (element: BasicBoolean) -> Unit) {
        forEachIndexed { _, element -> action(element) }
    }

    inline fun forEachIndexed(action: (index: Int, element: BasicBoolean) -> Unit) {
        var index = 0
        while (index < size) {
            action(index, get(index))
            index++
        }
    }

    /**
     * 倒序遍历
     */
    inline fun forEachReversedIndexed(action: (index: Int, element: BasicBoolean) -> Unit) {
        var index = size - 1
        while (index >= 0) {
            action(index, get(index))
            index--
        }
    }

    /**
     * 基础类型遍历方法(适用于无法kotlin inline的情况)
     */
    fun forEachWithBasic(action: OnBasicBoolean) {
        var index = 0
        while (index < size) {
            action.onBasicBoolean(get(index))
            index++
        }
    }

    fun forEachIndexedWithBasic(action: OnBasicBooleanWithIndex) {
        var index = 0
        while (index < size) {
            action.onBasicBooleanWithIndex(index, get(index))
            index++
        }
    }

    /**
     * 倒序遍历
     */
    fun forEachReversedIndexedWithBasic(action: OnBasicBooleanWithIndex) {
        var index = size - 1
        while (index >= 0) {
            action.onBasicBooleanWithIndex(index, get(index))
            index--
        }
    }

    /**
     * 获取一段BooleanArrayList
     */
    fun subList(fromIndex: Int, toIndex: Int): BooleanArrayList {
        if (toIndex > size)
            throw IndexOutOfBoundsException("size = $size ,the toIndex = $toIndex")
        return BooleanArrayList(data.copyOfRange(fromIndex, toIndex))
    }

    /**
     * 安全的subList,索引超限部分不会返回内容
     */
    fun subListWithSafe(fromIndex: Int, toIndex: Int): BooleanArrayList =
        BooleanArrayList(data.copyOfRange(maxOf(0, fromIndex), minOf(size, toIndex)))

    /**
     * 批量添加数据
     */
    fun addAll(elements: Collection<BasicBoolean>) {
        elements.forEach(::add)
    }

    fun addAll(elements: BooleanArrayList) {
        addAll(elements.data.copyOf(elements.size))
    }

    fun addAll(elements: BooleanArray) {
        elements.forEach(::add)
    }

    fun addAllNotNull(elements: Collection<BasicBoolean?>?) {
        elements?.forEach {
            if (it != null)
                add(it)
        }
    }

    /**
     * 批量移除数据
     */
    fun removeAll(elements: Collection<BasicBoolean>) {
        elements.forEach(::removeElement)
    }

    fun removeAll(elements: BooleanArrayList) {
        removeAll(elements.data.copyOf(elements.size))
    }

    fun removeAll(elements: BooleanArray) {
        elements.forEach(::removeElement)
    }

    /**
     * 清空数据
     */
    fun clear() {
        size = 0
    }

    /**
     * 转换数据结构
     */
    fun toBooleanArray() = data.copyOf(size)

    fun toMutableList() = toBooleanArray().toMutableList()

    override fun toString(): String {
        return "[" + toBooleanArray().joinToString(",") + "]"
    }

    /**
     * 包装类型迭代器
     */
    inner class BooleanMutableIterator : MutableIterator<BasicBoolean> {
        private var index = 0
        override fun hasNext(): Boolean = size > index
        override fun next(): BasicBoolean = get(index++)
        override fun remove() = removeAtIndex(--index)
    }

    /**
     * 基础类型迭代器
     */
    inner class BasicBooleanMutableIterator {
        private var index = 0
        fun hasNext(): Boolean = size > index
        fun next(): BasicBoolean = get(index++)
        fun remove() = removeAtIndex(--index)
    }

    /**
     * 基础类型的lambda
     */
    fun interface OnBasicBoolean {
        fun onBasicBoolean(basicBoolean: BasicBoolean)
    }

    fun interface OnBasicBooleanWithIndex {
        fun onBasicBooleanWithIndex(index: Int, basicBoolean: BasicBoolean)
    }
}

fun booleanArrayListOf(vararg elements: BasicBoolean): BooleanArrayList = BooleanArrayList(list=elements.toMutableList())