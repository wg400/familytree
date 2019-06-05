package com.wanggang.familytree.model

import com.wanggang.familytree.familytree.TreePoint

/**
 * 家族树数据模型
 * */
data class FamilyMemberModel(var memberEntity: FamilyMemberEntity) {

    // 配偶
    var spouseEntity: FamilyMemberEntity? = null

    // 孩子
    var childModels: List<FamilyMemberModel>? = null

    // 当前模型所处的代数
    var level = 0

    // 显示位置的中心点坐标
    var centerPoint: TreePoint? = null

    override fun toString(): String {
        return "{memberEntity=$memberEntity, centerPoint=$centerPoint}"
    }


}