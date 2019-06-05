package com.wanggang.familytree.familytree

// 记录当行左右范围

data class LeftRightBoard(var left: Int, var right: Int) {
    override fun toString(): String {
        return "LeftRightBoard(left=$left, right=$right)"
    }
}